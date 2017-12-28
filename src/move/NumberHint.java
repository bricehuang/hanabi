package move;

import org.json.JSONException;
import org.json.JSONObject;

import game.JsonUtil;

public class NumberHint implements Move {

    public final int actor;

    public final int hintee;
    public final Integer number;

    public NumberHint(int actor, int hintee, Integer number) {
        assert actor != hintee;
        this.actor = actor;
        this.hintee = hintee;
        this.number = number;
    }

    @Override
    public String verboseRep() {
        return "Player " + actor + " hinted Player " + hintee + 
                "'s " + number + "s.";
    }

    @Override
    public String toString() {
        return "" + actor + " HINT " + hintee + " " + number;
    }

	@Override
	public JSONObject jsonify() throws JSONException {
		JSONObject result = new JSONObject();
		result.put(JsonUtil.MOVE_TYPE, JsonUtil.NUMBER_HINT);
		result.put(JsonUtil.ACTOR, actor);
		result.put(JsonUtil.HINTEE, hintee);
		result.put(JsonUtil.NUMBER, number);
		return result;
	}

}
