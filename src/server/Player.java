package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContext;

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
	
	public void moveRoom(Room newRoom) {
		this.room.removePlayer(this);
		this.room = newRoom;
		this.room.addPlayer(this);
	}
	
	public void logout() {
		this.room.removePlayer(this);
		synchronized(context) {
			Config.getAllUsernames(context).remove(this.name);
			Config.getPlayersBySessionID(context).remove(this.sessionID);
		}
	}

}
