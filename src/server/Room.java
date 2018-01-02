package server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Room {

    // abstract fields.  setAbstractFields() is a somewhat hacky way
    // to get around Java not having static fields.
    protected String serverChatType = "";
    protected String userChatType = "";
    protected abstract void setAbstractFields();
    protected abstract boolean isLobby();

    protected final ServletContext context;
    protected List<Player> players;

    public Room(ServletContext context) {
        this.context = context;
        this.players = new ArrayList<>();
        setAbstractFields();
    }
    
    public JSONObject makeMessageForPlayerQueue(String type, JSONObject content) throws JSONException {
        return new JSONObject()
            .put("type", type)
            .put("content", content);
    }
    public void broadcast(String type, JSONObject content) throws InterruptedException, JSONException {
        for (Player player : players) {
            player.sendMessage(makeMessageForPlayerQueue(type, content));
        }
    }

    public void addPlayer(Player player) throws InterruptedException, JSONException {
        players.add(player);
        broadcastServerMsg(player.name + " entered the room.");
    }
    public void removePlayer(Player player) throws InterruptedException, JSONException {
        players.remove(player);
    }
    protected void broadcastServerMsg(String message) throws InterruptedException, JSONException {
        broadcast(
            serverChatType,
            new JSONObject()
                .put("message", message)
        );
    }
    public void chat(Player player, String message) throws InterruptedException, JSONException {
        broadcast(
            userChatType,
            new JSONObject()
                .put("from", player.name)
                .put("message", message)
        );
    }

}
