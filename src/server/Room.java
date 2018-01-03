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
    protected void broadcast(JSONObject message) throws InterruptedException, JSONException {
        for (Player player : players) {
            player.sendMessage(message);
        }            
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
    
    // command
    public abstract void handleCommand(String cmd, Player player, JSONObject content);
    
    /**
     * Sends chat from player to his room.  Routed to either lobby or game room.
     * Sends:
     *   - user_to_lobby or user_to_game, whichever relevant, to room
     * @param player
     * @param content {message: chat string}
     */
    protected void chatHandler(Player player, JSONObject content) {
        try {
            String message = content.getString("message");
            broadcast(userMessage(player, message));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out a player. Removes player's credentials from the global context.
     * If player is in game room, notifies room that player left.  If game is in
     * progress, resigns the game.
     * Sends:
     *   - leave_game_ack or leave_lobby_ack, whichever relevant, to player
     *   - logout_ack to player
     *   If user in lobby:
     *     - present_lobby_users
     *   If user in game room:
     *     If game in progress:
     *       - game_content to game room
     *       - game_end to game room
     *     - server_to_game
     *     - present_game_users
     *
     * @param player
     */
    protected void logoutHandler(Player player) {
        try {
            logout(player);
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
    
    // response methods
    /**
     * Adds a player to the room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    protected void addPlayer(Player player) throws InterruptedException, JSONException {
        players.add(player);
        onJoin(player);            
    }
    /**
     * Fires when player joins room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    protected abstract void onJoin(Player player) throws InterruptedException, JSONException;

    /**
     * Removes a player from the room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    protected void removePlayer(Player player) throws InterruptedException, JSONException {
        players.remove(player);
        onLeave(player);
    }
    /**
     * Fires when player leaves room.    
     * @param player
     * @throws InterruptedException
     * @throws JSONException
     */
    protected abstract void onLeave(Player player) throws InterruptedException, JSONException;

    public void logout(Player player) throws InterruptedException, JSONException {
        player.setBeingLoggedOut();
        removePlayer(player);
        player.sendMessage(logoutAck());
        Config.getAllUsernames(context).remove(player.name);
        Config.getPlayersBySessionID(context).remove(player.sessionID);
    }
}
