package server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Room {

    protected String logoutAckType = "logout_ack";
    // abstract fields.  setAbstractFields() is a somewhat hacky way
    // to get around Java not having static fields.
    protected String joinAckType = "";
    protected String leaveAckType = "";
    protected String serverChatType = "";
    protected String userChatType = "";
    protected String userListType = "";
    protected abstract void setAbstractFields();
    protected abstract boolean isLobby();

    protected final ServletContext context;
    protected List<Player> players;

    public Room(ServletContext context) {
        this.context = context;
        this.players = new ArrayList<>();
        setAbstractFields();
    }

    // util for player-queue messages
    public JSONObject makePlayerMessage(String type, JSONObject content) throws JSONException {
        return new JSONObject()
            .put("type", type)
            .put("content", content);
    }

    // generators for message types
    public JSONObject joinAck() throws JSONException {
        return makePlayerMessage(joinAckType, new JSONObject());
    }
    public JSONObject leaveAck() throws JSONException {
        return makePlayerMessage(leaveAckType, new JSONObject());
    }
    protected JSONArray allUsernames() {
        JSONArray allUsernames = new JSONArray();
        synchronized(this) {
            for (Player player : players) {
                allUsernames.put(player.name);
            }            
        }
        return allUsernames;
    }
    public JSONObject playersInRoom() throws JSONException {
        return makePlayerMessage(userListType, new JSONObject().put("users", allUsernames()));
    }
    public JSONObject userMessage(Player player, String message) throws JSONException {
        return makePlayerMessage(userChatType, new JSONObject().put("from", player.name).put("message", message));
    }
    public JSONObject serverMessage(String message) throws JSONException {
        return makePlayerMessage(serverChatType, new JSONObject().put("message", message));
    }
    public JSONObject logoutAck() throws JSONException {
        return makePlayerMessage(logoutAckType, new JSONObject());
    }
    
    // TODO
    public void broadcast(JSONObject message) throws InterruptedException, JSONException {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
    public void chat(Player player, String message) throws InterruptedException, JSONException {
        broadcast(userMessage(player, message));
    }
    public void broadcastServerMsg(String message) throws InterruptedException, JSONException {
        broadcast(serverMessage(message));
    }

    public void addPlayer(Player player) throws InterruptedException, JSONException {
        players.add(player);
        broadcastServerMsg(player.name + " entered the room.");
    }
    public void removePlayer(Player player) throws InterruptedException, JSONException {
        players.remove(player);
    }

}
