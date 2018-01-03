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
     * locks on context.   
     * @return
     * @throws JSONException
     */
    public JSONObject openGames() throws JSONException {
        JSONArray games = new JSONArray();
        synchronized(context) {
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
     * Constructs a new room and adds it to the global context.  Should
     * be called with locks on the global context.
     * @param nPlayers
     * @throws JSONException
     * @throws InterruptedException
     */
    private GameRoom makeNewRoom(int nPlayers) throws InterruptedException, JSONException {
        synchronized(context) {
            int gameID = Config.genGameID(context);
            GameRoom newRoom = new GameRoom(context, gameID, nPlayers);
            Config.getActiveGames(context).put(gameID, newRoom);
            return newRoom;
        }
    }

    public void createGameRoom(Player player, int nPlayers) throws InterruptedException, JSONException {
        GameRoom newRoom = makeNewRoom(nPlayers);
        synchronized(context) {
            player.moveRoom(newRoom);
        }
        broadcastServerMsg(player.name + " started game " + newRoom.gameID());
        broadcast(openGames());
    }

}
