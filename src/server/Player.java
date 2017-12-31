package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

public class Player {

	public final String sessionID;
	public final String name;
	private Room room;
	private final BlockingQueue<JSONObject> messages;
	private final ServletContext context;

	public Player(String sessionID, String name, Room room, ServletContext context) {
		this.sessionID = sessionID;
		this.name = name;
		this.room = room;
		this.messages = new LinkedBlockingQueue<>();
		this.context = context;
		room.addPlayer(this);
	}

	public void sendMessage(JSONObject message) throws InterruptedException {
		messages.put(message);
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

	public void moveRoom(Room newRoom) {
		this.room.removePlayer(this);
		this.room = newRoom;
		this.room.addPlayer(this);
	}
	
	public void logout() throws InterruptedException, JSONException {
		this.room.removePlayer(this);
		synchronized(context) {
			this.sendMessage(
				new JSONObject()
				    .put("type", "logout-ack")
				    .put("content", new JSONObject())
			);
			Config.getAllUsernames(context).remove(this.name);
			Config.getPlayersBySessionID(context).remove(this.sessionID);
		}
	}
	
	public void chat(String content) {
		this.room.chat(this, content);
	}

}
