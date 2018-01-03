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

}
