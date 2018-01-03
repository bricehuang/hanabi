package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.Game;

public class GameRoom extends Room {

    @Override
    protected void setAbstractFields() {
        this.joinAckType = "join_game_ack";
        this.leaveAckType = "leave_game_ack";
        this.serverChatType = "server_to_game";
        this.userChatType = "user_to_game";
        this.userListType = "present_game_users";
    }
    @Override
    protected boolean isLobby() {
        return false;
    }

    private final int gameID;
    private final int nPlayers;
    private final Game game;
    private boolean started;
    private boolean finished;

    public GameRoom(ServletContext context, int gameID, int nPlayers) {
        super(context);
        this.gameID = gameID;
        this.nPlayers = nPlayers;
        this.game = new Game(nPlayers);
        this.started = false;
        this.finished = false;
    }

    // getters
    public int gameID() {
        return gameID;
    }
    public int nPlayers() {
        return nPlayers;
    }
    public int playersPresent() {
        return players.size();
    }

    public String state() {
        if (!started) {
            return "Waiting";
        } else if (!finished) {
            return "In progress";
        } else {
            return "Finished";
        }
    }

    // generators for message types
    public JSONObject startNotification() throws JSONException {
        return makePlayerMessage("game_start", new JSONObject());
    }
    public JSONObject endNotification() throws JSONException {
        int score;
        score = game.getOmnescientView().score();            
        return makePlayerMessage("game_end", new JSONObject().put("score", score));
    }
    public List<JSONObject> allPlayerViews() throws JSONException {
        JSONArray usernames = allUsernames();
        List<JSONObject> result = new ArrayList<>();
        for (int i=0; i<players.size(); i++) {
            result.add(
                new JSONObject()
                    .put("users", usernames)
                    .put("state", game.getPlayerView(i).jsonify())
            );
        }
        return result;
    }
    
    // command
    @Override
    public void handleCommand(String cmd, Player player, JSONObject content) {
        switch (cmd) {
            case MainServlet.CHAT: 
                chatHandler(player, content);
                break;
            case MainServlet.START_GAME: 
                startGameHandler(player);
                break;
            case MainServlet.GAME_ACTION: 
                gameActionHandler(player, content);
                break;
            case MainServlet.EXIT_GAME: 
                exitGameHandler(player);
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
     * Starts a game.  Only valid when player is in game, and game is
     * at capacity and not started.
     * Sends:
     *   - game_start, to game room
     *   - game_state, to game room
     *   - open_games, to lobby
     * @param player
     */
    private void startGameHandler(Player player) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Takes a game action.  Only valid when player is in game.  Valid
     * only on player's turn, except for resignations which are always
     * valid any time.
     * Sends:
     *   - game_state, to game room
     *   If game over:
     *     - game_end, to game room
     *     - open games, to lobby
     * @param player
     * @param content TODO
     */
    private void gameActionHandler(Player player, JSONObject content) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Leaves a game room to return to lobby.  Only valid when player is
     * in game.  Notifies game and lobby that player left and joined.  If
     * game is in progress, resigns game.
     * Sends:
     *   - leave_game_ack, to player
     *   If game in progress:
     *     - game_content to game room
     *     - game_end to game room
     *   - server_to_game
     *   - present_game_users
     *
     *   - join_lobby_ack, to player
     *   - present_lobby_users, to lobby
     *   - open_games, to lobby
     *
     * @param player
     */
    private void exitGameHandler(Player player) {
        try {
            returnToLobby(player);
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    // response methods    
    @Override
    protected void onJoin(Player player) throws InterruptedException, JSONException {
        player.sendMessage(joinAck());
        broadcast(playersInRoom());
        broadcast(serverMessage(player.name + " entered the room."));
        Lobby lobby = Config.getLobby(context);
        lobby.broadcast(lobby.openGames());
    }
    @Override
    protected void onLeave(Player player) throws InterruptedException, JSONException {
        // TODO if game is in progress, player should resign
        if (this.playersPresent() == 0) {
            this.kill();
        }
        player.sendMessage(leaveAck());
        broadcast(playersInRoom());
        broadcast(serverMessage(player.name + " left the room."));
        Lobby lobby = Config.getLobby(context);
        lobby.broadcast(lobby.openGames());
    }
    
    
    private void returnToLobby(Player player) throws InterruptedException, JSONException {
        Lobby lobby = Config.getLobby(context);
        player.moveRoom(lobby);
    }
    /**
     * Removes game from global registry
     */
    private void kill() {
        Map<Integer, GameRoom> gamesByID = Config.getActiveGames(context);
        System.out.println("here");
        gamesByID.remove(gameID);
    }

}
