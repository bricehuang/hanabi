package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class Player {

    public final String sessionID;
    public final String name;
    private Room room;
    private final BlockingQueue<JSONObject> messages;
    private boolean beingLoggedOut = false; // when set to true, outbound queue ignores all messages except logout ack

    public Player(String sessionID, String name, Room room) throws InterruptedException, JSONException {
        this.sessionID = sessionID;
        this.name = name;
        this.room = room;
        this.messages = new LinkedBlockingQueue<>();
        this.beingLoggedOut = false;
        room.addPlayer(this);
    }

    public boolean isInLobby() {
        return this.room.isLobby();
    }

    // message sending mechanics
    public void sendMessage(JSONObject message) throws InterruptedException, JSONException {
        if (!beingLoggedOut || message.getString("type").equals("logout_ack")) {
            messages.put(message);            
        }
    }
    public JSONObject getMessageToSend() throws JSONException {
        JSONObject message;
        try {
            message = messages.poll(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // return a null message; client will retry
            message = null;
        }

        if (message != null) {
            return message.put("is_null", false);
        } else {
            return new JSONObject().put("is_null", true);
        }
    }
    public void setBeingLoggedOut() {
        beingLoggedOut = true;
    }
    
    // command
    public void routeCommand(String cmd, JSONObject content) throws InterruptedException, JSONException {
        this.room.handleCommand(cmd, this, content);
    }


    public void moveRoom(Room newRoom) throws InterruptedException, JSONException {
        this.room.removePlayer(this);
        this.room = newRoom;
        this.room.addPlayer(this);
    }
    
    public void logout() throws InterruptedException, JSONException {
        this.room.logout(this);
    }

}
