package server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Room {

	List<Player> players;

	public Room() {
		this.players = new ArrayList<>();
	}

	public void addPlayer(Player player) {
		players.add(player);
		try {
			broadcast(
				new JSONObject()
					.put("type", "server_to_chat")
					.put("chat", player.name+" has entered the room.")
			);
		} catch (InterruptedException | JSONException e) {
			e.printStackTrace();
		}
	}
	public void removePlayer(Player player) {
		players.remove(player);
		try {
			broadcast(
				new JSONObject()
					.put("type", "server_to_chat")
					.put("chat", player.name+" has left the room.")
			);
		} catch (InterruptedException | JSONException e) {
			e.printStackTrace();
		}
	}
	public void chat(Player player, String message) {
		try {
			broadcast(
				new JSONObject()
					.put("type", "user_to_chat")
					.put("chat", message)
			);
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
