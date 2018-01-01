package server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Room {

    List<Player> players;

    public Room() {
        this.players = new ArrayList<>();
        setAbstractFields();
    }
    
    public JSONObject makeMessageForPlayerQueue(String type, JSONObject content) throws JSONException {
        return new JSONObject()
            .put("type", type)
            .put("content", content);
    }
    
    // abstract fields.  setAbstractFields() is a somewhat hacky way
    // to get around Java not having static fields.
    protected String serverChatType = "";
    protected String userChatType = "";
    protected abstract void setAbstractFields();

    public void addPlayer(Player player) {
        players.add(player);
        try {
            broadcast(makeMessageForPlayerQueue(
                serverChatType,
                new JSONObject()
                    .put("message", player.name+" entered the room.")
            ));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
    public void removePlayer(Player player) {
        players.remove(player);
        try {
            broadcast(makeMessageForPlayerQueue(
                serverChatType,
                new JSONObject()
                    .put("message", player.name+" left the room.")
            ));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
    public void chat(Player player, String message) {
        try {
            broadcast(makeMessageForPlayerQueue(
                userChatType,
                new JSONObject()
                    .put("from", player.name)
                    .put("message", message)
                
            ));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }
    public void broadcast(JSONObject message) throws InterruptedException {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

}
