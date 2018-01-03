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

    // response methods    
    @Override
    public void onJoin(Player player) throws InterruptedException, JSONException {
        player.sendMessage(joinAck());
        broadcast(playersInRoom());
        broadcast(serverMessage(player.name + " entered the room."));
    }
    @Override
    public void onLeave(Player player) throws InterruptedException, JSONException {
        player.sendMessage(leaveAck());
        broadcast(playersInRoom());
        broadcast(serverMessage(player.name + " left the room."));
    }
    
    
    /**
     * Removes game from global registry
     */
    public void kill() {
        Map<Integer, GameRoom> gamesByID = Config.getActiveGames(context);
        System.out.println("here");
        gamesByID.remove(gameID);
    }

}
