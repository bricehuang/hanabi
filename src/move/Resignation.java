package move;

import org.json.JSONException;
import org.json.JSONObject;

import game.JsonUtil;

public class Resignation implements Move {

    public final int actor;

    public Resignation(int actor) {
        this.actor = actor;
    }

    @Override
    public String verboseRep() {
        return "Player " + actor + " resigned.";
    }

    @Override
    public String toString() {
        return "" + actor + " RESIGN";
    }

	@Override
	public JSONObject jsonify() throws JSONException {
		JSONObject result = new JSONObject();
		result.put(JsonUtil.MOVE_TYPE, JsonUtil.RESIGN);
		result.put(JsonUtil.ACTOR, actor);
		return result;
	}

}
