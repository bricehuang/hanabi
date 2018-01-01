package server;

import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Lobby extends Room {

    @Override
    protected void setAbstractFields() {
        this.serverChatType = "server_to_lobby";
        this.userChatType = "user_to_lobby";
    }
    @Override
    protected boolean isLobby() {
        return true;
    }

    public Lobby(ServletContext context) {
        super(context);
    }

    @Override
    public void addPlayer(Player player) throws InterruptedException, JSONException {
        super.addPlayer(player);
        player.sendMessage(makeMessageForPlayerQueue(
            "game_list", gameListAnnouncement()
        ));
        
    }

    public JSONObject gameListAnnouncement() throws JSONException {
        JSONArray gameList = new JSONArray();
        synchronized(context) {
            Map<Integer, GameRoom> gamesById = Config.getActiveGames(context);
            for (Integer gameID : gamesById.keySet()) {
                GameRoom room = gamesById.get(gameID);
                gameList.put(
                    new JSONObject()
                        .put("id", gameID)
                        .put("players", room.playersPresent())
                        .put("capacity", room.nPlayers())
                        .put("state", room.state())
                );
            }
        }
        return new JSONObject()
            .put("games", gameList);
    }

    // TODO: make this method private.  When a player creates a new room he should move 
    // to it immediately.  
    /**
     * Constructs a new room and adds it to the global context.  Should
     * be called with locks on the global context.
     * @param nPlayers
     * @throws JSONException 
     * @throws InterruptedException 
     */
    public void makeNewRoom(int nPlayers) throws InterruptedException, JSONException {
        synchronized(context) {
            int gameID = Config.genGameID(context);
            GameRoom newRoom = new GameRoom(context, gameID, nPlayers);
            Config.getActiveGames(context).put(gameID, newRoom);            
        }
        broadcast("game_list", gameListAnnouncement());
    }

}
