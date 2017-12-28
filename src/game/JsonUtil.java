package game;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hanabi.CardSpec;
import hanabi.Color;

public class JsonUtil {

	// card
	public static final String COLOR = "color";
	public static final String NUMBER = "number";
	public static final String CARD_IDENTITY = "card";
	
	// play and discard
	public static final String COUNT = "count";

	// moves
	public static final String MOVE_TYPE = "move_type";
	public static final String COLOR_HINT = "color_hint";
	public static final String NUMBER_HINT = "number_hint";
	public static final String PLAY = "play";
	public static final String DISCARD = "discard";
	public static final String RESIGN = "resign";

	public static final String ACTOR = "actor";
	public static final String HINTEE = "hintee";
	public static final String POSITION = "position";
	public static final String CORRECT = "correcg";
	public static final String SAFE = "safe";
	
	public static final String N_PLAYERS = "n_players";
	public static final String HAND_SIZE = "hand_size";
	public static final String TO_MOVE = "to_move";
	public static final String LIVES = "lives";
	public static final String HINTS = "hints";
	public static final String DECK = "deck";
	public static final String HISTORY = "history";
	public static final String PLAYS = "plays";
	public static final String DISCARDS = "discards";
	public static final String HANDS = "hands";
	public static final String IS_OVER = "is_over";	

	public static JSONArray jsonifyPlays(Map<Color, Integer> plays) throws JSONException {
		JSONArray result = new JSONArray();
		for (Color color : plays.keySet()) {
			JSONObject colorCount = new JSONObject();
			colorCount.put(COLOR, color.toString());
			colorCount.put(COUNT, plays.get(color));
			result.put(colorCount);
		}
		return result;
	}

	public static JSONArray jsonifyDiscards(Map<CardSpec, Integer> discards) throws JSONException {
		JSONArray result = new JSONArray();
		for (CardSpec card : discards.keySet()) {
			JSONObject discardCounter = new JSONObject();
			discardCounter.put(CARD_IDENTITY, card.jsonify());
			discardCounter.put(COUNT, discards.get(card));
			result.put(discardCounter);
		}
		return result;
	}
}
