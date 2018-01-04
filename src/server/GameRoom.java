package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.Game;
import javafx.util.Pair;

public class GameRoom extends Room {
    
    // initialized on game start
    private List<Player> permanentPlayerListing = null;

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
    private Game game;
    private boolean started;
    private boolean finished;
    
    public static final int ROOM_CAPACITY = 5;

    public GameRoom(ServletContext context, int gameID) {
        super(context);
        this.gameID = gameID;
        this.game = null; // set on start
        this.started = false;
        this.finished = false;
    }

    // getters
    public int gameID() {
        return gameID;
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
    public boolean isFull() {
        return (players.size() == ROOM_CAPACITY);
    }
    protected boolean readyToStart() {
        return (players.size()>= 2 && players.size() <= 5);
    }
    private int getPlayerIndex(Player player) {
        for (int i=0; i<permanentPlayerListing.size(); i++) {
            if (permanentPlayerListing.get(i).equals(player)) {
                return i;
            }
        }
        throw new RuntimeException("Should not get here");
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
        for (int i=0; i<permanentPlayerListing.size(); i++) {
            result.add(
                makePlayerMessage(
                    "game_state", 
                    new JSONObject()
                        .put("users", usernames)
                        .put("state", game.getPlayerView(i).jsonify())
                )
            );
        }
        return result;
    }
    
    // command
    @Override
    public void handleCommand(String cmd, Player player, JSONObject content) throws InterruptedException, JSONException {
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
     * @throws JSONException 
     * @throws InterruptedException 
     */
    private void startGameHandler(Player player) throws InterruptedException, JSONException {
        // only start if not yet started, and room is full, and game is ready to start
        if (started || !readyToStart() ) { return; }
        started = true;
        permanentPlayerListing = Collections.unmodifiableList(new ArrayList<>(players));
        int nPlayers = permanentPlayerListing.size();
        game = new Game(nPlayers);

        broadcast(startNotification());
        broadcastPlayerViews();
        Lobby lobby = Config.getLobby(context);
        lobby.broadcast(lobby.openGames());
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
     * @param content {action: string} TODO make this better
     * @throws JSONException 
     */
    private void gameActionHandler(Player player, JSONObject content) throws InterruptedException, JSONException {
        if (!started || finished) { return; }
        int playerIndex = getPlayerIndex(player);
        String action = content.getString("action");
        if (playerIndex != game.getOmnescientView().playerToMove && !action.equals("resign")) { return;}
        Pair<Boolean, String> gameResponse = 
            action.equals("resign") ? 
            game.makeMove(action + " " + playerIndex) : 
            game.makeMove(action);
        if (gameResponse.getKey()) {
            broadcastPlayerViews();
            if (game.getOmnescientView().isOver) {
                finished = true;
                broadcast(endNotification());
                Lobby lobby = Config.getLobby(context);
                lobby.broadcast(lobby.openGames());
            }            
        } else {
            String errorMsg = gameResponse.getValue();
            player.sendMessage(serverMessage(errorMsg));
        }
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
     * @throws JSONException 
     * @throws InterruptedException 
     */
    private void exitGameHandler(Player player) throws InterruptedException, JSONException {
        Lobby lobby = Config.getLobby(context);
        player.moveRoom(lobby);
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
        // if game is in progress, resign
        if (started && !finished) {
            gameActionHandler(player, new JSONObject().put("action","resign"));
        }
        if (this.playersPresent() == 0) {
            this.kill();
        }
        player.sendMessage(leaveAck());
        broadcast(playersInRoom());
        broadcast(serverMessage(player.name + " left the room."));
        Lobby lobby = Config.getLobby(context);
        lobby.broadcast(lobby.openGames());
    }
    

    /**
     * Removes game from global registry
     */
    private void kill() {
        Map<Integer, GameRoom> gamesByID = Config.getActiveGames(context);
        System.out.println("here");
        gamesByID.remove(gameID);
    }

    private void broadcastPlayerViews() throws InterruptedException, JSONException {
        List<JSONObject> playerViews = allPlayerViews();
        for (int i=0; i<permanentPlayerListing.size(); i++) {
            Player player = permanentPlayerListing.get(i);
            if (players.contains(player)) {
                player.sendMessage(playerViews.get(i));                
            }
        }
    }
}
