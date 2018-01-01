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

    public void addPlayer(Player player) {
        players.add(player);
        try {
            broadcast(
                serverChatType,
                new JSONObject()
                    .put("message", player.name+" entered the room.")
            );
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
    public void removePlayer(Player player) {
        players.remove(player);
        try {
            broadcast(
                serverChatType,
                new JSONObject()
                    .put("message", player.name+" left the room.")
            );
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
    public void chat(Player player, String message) {
        try {
            broadcast(
                userChatType,
                new JSONObject()
                    .put("from", player.name)
                    .put("message", message)
                
            );
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }

}
