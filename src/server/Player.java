package server;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

public class Player {

    public final String sessionID;
    public final String name;
    private Room room;
    private final BlockingQueue<JSONObject> messages;
    private final ServletContext context;
    private boolean beingLoggedOut = false; // when set to true, outbound queue ignores all messages except logout ack

    public Player(String sessionID, String name, Room room, ServletContext context) throws InterruptedException, JSONException {
        this.sessionID = sessionID;
        this.name = name;
        this.room = room;
        this.messages = new LinkedBlockingQueue<>();
        this.context = context;
        this.beingLoggedOut = false;
        room.addPlayer(this);
    }

    public boolean isInLobby() {
        return this.room.isLobby();
    }

    public void sendMessage(JSONObject message) throws InterruptedException, JSONException {
        if (!beingLoggedOut || message.getString("type").equals("logout_ack")) {
            messages.put(message);            
        }
    }

    public JSONObject getMessageToSend() throws JSONException {
        JSONObject message;
        try {
            message = messages.poll(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // return a null message; client will retry
            message = null;
        }

        if (message != null) {
            return message.put("is_null", false);
        } else {
            return new JSONObject().put("is_null", true);
        }
    }

    public void chat(String message) throws InterruptedException, JSONException {
        room.broadcast(room.userMessage(this, message));
    }

    public void moveRoom(Room newRoom) throws InterruptedException, JSONException {
        this.room.removePlayer(this);
        this.room = newRoom;
        this.room.addPlayer(this);
    }

    public void logout() throws InterruptedException, JSONException {
        this.beingLoggedOut = true;
        this.room.removePlayer(this);
        this.sendMessage(Room.logoutAck());
        Config.getAllUsernames(context).remove(this.name);
        Config.getPlayersBySessionID(context).remove(this.sessionID);
    }
    
    /**
     * Constructs a new room and adds it to the global context.  
     * @param nPlayers
     * @throws JSONException
     * @throws InterruptedException
     */
    public void makeAndJoinNewGame(int nPlayers) throws InterruptedException, JSONException {
        assert this.isInLobby();
        int gameID = Config.genGameID(context);
        GameRoom newRoom = new GameRoom(context, gameID, nPlayers);
        Config.getActiveGames(context).put(gameID, newRoom);
        
        // this will not fail because gameID was just added to the global registry
        this.joinGameRoom(gameID);
        Lobby lobby = Config.getLobby(context);
        lobby.broadcast(lobby.serverMessage(this.name + " started game " + gameID));
    }

    public void joinGameRoom(int gameID) throws InterruptedException, JSONException {
        assert this.isInLobby();
        Map<Integer, GameRoom> gamesByID = Config.getActiveGames(context);
        if (!gamesByID.containsKey(gameID)) { return; }

        GameRoom room = gamesByID.get(gameID);
        moveRoom(room);
        Lobby lobby = Config.getLobby(context);
        lobby.broadcast(lobby.openGames());
    }

    public void returnToLobby() throws InterruptedException, JSONException {
        assert !this.isInLobby();
        GameRoom room = (GameRoom) this.room; // TODO this is hacky
        // TODO: if player is in middle of game he should resign
        if (room.playersPresent() == 1) {
            room.kill();
        }

        Lobby lobby = Config.getLobby(context);
        this.moveRoom(lobby);
        lobby.broadcast(lobby.openGames());
    }

}
