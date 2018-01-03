package server;

import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Lobby extends Room {

    @Override
    protected void setAbstractFields() {
        this.joinAckType = "join_lobby_ack";
        this.leaveAckType = "leave_lobby_ack";
        this.serverChatType = "server_to_lobby";
        this.userChatType = "user_to_lobby";
        this.userListType = "present_lobby_users";
    }
    @Override
    protected boolean isLobby() {
        return true;
    }
    public Lobby(ServletContext context) {
        super(context);
    }
    
    // generators for message types
    /**
     * Open games message.  
     * @return
     * @throws JSONException
     */
    public JSONObject openGames() throws JSONException {
        JSONArray games = new JSONArray();
        Map<Integer, GameRoom> gamesById = Config.getActiveGames(context);
        for (Integer gameID : gamesById.keySet()) {
            GameRoom room = gamesById.get(gameID);
            games.put(
                new JSONObject()
                    .put("id", gameID)
                    .put("players", room.playersPresent())
                    .put("capacity", room.nPlayers())
                    .put("state", room.state())
            );
        }
        return makePlayerMessage("open_games", new JSONObject().put("games", games));
    }

    // response methods
    @Override
    public void onJoin(Player player) throws InterruptedException, JSONException {
        player.sendMessage(joinAck());
        player.sendMessage(openGames());
        broadcast(playersInRoom());
        broadcast(serverMessage(player.name + " entered the room."));
    }
    @Override
    public void onLeave(Player player) throws InterruptedException, JSONException {
        player.sendMessage(leaveAck());
        broadcast(playersInRoom());
    }

    /**
     * Constructs a new room and adds it to the global context.  
     * @param nPlayers
     * @throws JSONException
     * @throws InterruptedException
     */
    public void makeAndJoinNewGame(Player player, int nPlayers) throws InterruptedException, JSONException {
        int gameID = Config.genGameID(context);
        GameRoom newRoom = new GameRoom(context, gameID, nPlayers);
        Config.getActiveGames(context).put(gameID, newRoom);
        
        // this will not fail because gameID was just added to the global registry
        joinGameRoom(player, gameID);
        broadcast(serverMessage(player.name + " started game " + gameID));
    }

    public void joinGameRoom(Player player, int gameID) throws InterruptedException, JSONException {
        Map<Integer, GameRoom> gamesByID = Config.getActiveGames(context);
        if (!gamesByID.containsKey(gameID)) { return; }

        GameRoom room = gamesByID.get(gameID);
        player.moveRoom(room);
        broadcast(openGames());
    }

}
