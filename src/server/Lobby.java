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
                    .put("status", room.status())
            );
        }
        return makePlayerMessage("open_games", new JSONObject().put("games", games));
    }
    
    // command
    @Override
    public void handleCommand(String cmd, Player player, JSONObject content) throws InterruptedException, JSONException {
        switch (cmd) {
            case MainServlet.CHAT:
                chatHandler(player, content);
                break;
            case MainServlet.MAKE_GAME:
                makeGameHandler(player);
                break;
            case MainServlet.JOIN_GAME:
                joinGameHandler(player, content);
                break;
            case MainServlet.LOGOUT:
                logoutHandler(player);
                break;
            default:
                // invalid cmd, do nothing
                break;
        }
    }
    
    /**
     * Creates a new game room and moves player to that room.  Only valid when
     * player currently in lobby.  Notifies lobby that new game created.
     * Sends:
     *   - leave_lobby_ack, to player
     *   - server_to_lobby, to lobby
     *   - present_lobby_users, to lobby
     *   - open_games, to lobby
     *   - join_game_ack, to player
     *   - server_to_game, to game room
     *   - present_game_users, new game room
     * @param player
     * @throws JSONException 
     * @throws InterruptedException 
     */
    private void makeGameHandler(Player player) throws InterruptedException, JSONException {
        makeAndJoinNewGame(player);
    }

    /**
     * Joins an existing game room.  Only valid when player currently
     * in lobby, and joined game is waiting and under capacity.
     * Notifies room that player joined.
     * Sends:
     *   - leave_lobby_ack, to player
     *   - present_lobby_users, to lobby
     *   - open_games, to lobby
     *   - join_game_ack, to player
     *   - server_to_game, to game room
     *   - present_game_users, to game room
     * @param player
     * @param content {game_id: game id int}
     * @throws JSONException 
     * @throws InterruptedException 
     */
    private void joinGameHandler(Player player, JSONObject content) throws InterruptedException, JSONException {
        int gameID;
        try {
            gameID = content.getInt("game_id");
        } catch (JSONException e1) {
            // invalid game ID, do nothing
            return;
        }

        joinGameRoom(player, gameID);
    }

    // response methods
    @Override
    protected void onJoin(Player player) throws InterruptedException, JSONException {
        player.sendMessage(joinAck());
        player.sendMessage(openGames());
        broadcast(playersInRoom());
        broadcast(serverMessage(player.name + " entered the room."));
    }
    @Override
    protected void onLeave(Player player) throws InterruptedException, JSONException {
        player.sendMessage(leaveAck());
        broadcast(playersInRoom());
    }
    
    private void makeAndJoinNewGame(Player player) throws InterruptedException, JSONException {
        int gameID = Config.genGameID(context);
        GameRoom newRoom = new GameRoom(context, gameID);
        Config.getActiveGames(context).put(gameID, newRoom);
        
        // this will not fail because gameID was just added to the global registry
        joinGameRoom(player, gameID);
        broadcast(serverMessage(player.name + " started game " + gameID));
    }
    private void joinGameRoom(Player player, int gameID) throws InterruptedException, JSONException {
        Map<Integer, GameRoom> gamesByID = Config.getActiveGames(context);

        // if ID not in registry, fail and do nothing
        if (!gamesByID.containsKey(gameID)) { return; }

        GameRoom room = gamesByID.get(gameID);
        if (!room.status().equals("Waiting") || room.isFull()) {
            // can't join room
            return;
        }
        player.moveRoom(room);
    }

}
