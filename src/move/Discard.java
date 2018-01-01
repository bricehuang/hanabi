package move;

import org.json.JSONException;
import org.json.JSONObject;

import game.JsonUtil;

public class Discard implements Move {

    public final int actor;

    public final int position;
    public final boolean isSafe;

    public Discard(int actor, int position, boolean isSafe) {
        this.actor = actor;
        this.position = position;
        this.isSafe = isSafe;
    }

    @Override
    public String verboseRep() {
        String safety = this.isSafe ? "safe" : "unsafe";
        return "Player " + actor + " discarded his card from position " + position +
            ". It was " + safety + ".";
    }

    @Override
    public String toString() {
        String safety = this.isSafe ? "safe" : "unsafe";
        return "" + actor + " DISCARD " + position + " " + safety;
    }

    @Override
    public JSONObject jsonify() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(JsonUtil.MOVE_TYPE, JsonUtil.DISCARD);
        result.put(JsonUtil.ACTOR, actor);
        result.put(JsonUtil.POSITION, position);
        result.put(JsonUtil.SAFE, isSafe);
        return result;
    }

}
