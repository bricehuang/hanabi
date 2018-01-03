package server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Room {

    protected static String logoutAckType = "logout_ack";
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
    public static JSONObject makePlayerMessage(String type, JSONObject content) throws JSONException {
        return new JSONObject()
            .put("type", type)
            .put("content", content);
    }

    // generators for message types
    /**
     * Join ack message.  
     * @return
     * @throws JSONException
     */
    public JSONObject joinAck() throws JSONException {
        return makePlayerMessage(joinAckType, new JSONObject());
    }
    /**
     * Leave ack message.  
     * @return
     * @throws JSONException
     */
    public JSONObject leaveAck() throws JSONException {
        return makePlayerMessage(leaveAckType, new JSONObject());
    }
    /**
     * JSONArray of all usernames.    
     * @return
     */
    protected JSONArray allUsernames() {
        JSONArray allUsernames = new JSONArray();
        for (Player player : players) {
            allUsernames.put(player.name);
        }            
        return allUsernames;
    }
    /**
     * All usernames in room message.  
     * @return
     * @throws JSONException
     */
    public JSONObject playersInRoom() throws JSONException {
        return makePlayerMessage(userListType, new JSONObject().put("users", allUsernames()));
    }
    /**
     * Chat message from player.  
     * @param player
     * @param message
     * @return
     * @throws JSONException
     */
    public JSONObject userMessage(Player player, String message) throws JSONException {
        return makePlayerMessage(userChatType, new JSONObject().put("from", player.name).put("message", message));
    }
    /**
     * Chat message from server.  
     * @param message
     * @return
     * @throws JSONException
     */
    public JSONObject serverMessage(String message) throws JSONException {
        return makePlayerMessage(serverChatType, new JSONObject().put("message", message));
    }
    /**
     * Logout ack message.  
     * @return
     * @throws JSONException
     */
    public static JSONObject logoutAck() throws JSONException {
        return makePlayerMessage(logoutAckType, new JSONObject());
    }
    
    // response methods
    /**
     * Adds a player to the room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    public void addPlayer(Player player) throws InterruptedException, JSONException {
        players.add(player);
        onJoin(player);            
    }
    /**
     * Fires when player joins room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    public abstract void onJoin(Player player) throws InterruptedException, JSONException;

    /**
     * Removes a player from the room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    public void removePlayer(Player player) throws InterruptedException, JSONException {
        players.remove(player);
        onLeave(player);
    }
    /**
     * Fires when player leaves room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    public abstract void onLeave(Player player) throws InterruptedException, JSONException;

    public void broadcast(JSONObject message) throws InterruptedException, JSONException {
        for (Player player : players) {
            player.sendMessage(message);
        }            
    }
    
    // TODO
    public void chat(Player player, String message) throws InterruptedException, JSONException {
        broadcast(userMessage(player, message));
    }
    public void broadcastServerMsg(String message) throws InterruptedException, JSONException {
        broadcast(serverMessage(message));
    }


}
