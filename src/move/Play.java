package move;

import org.json.JSONException;
import org.json.JSONObject;

import game.JsonUtil;

public class Play implements Move {

    public final int actor;

    public final int position;
    public final boolean isCorrect;

    public Play(int actor, int position, boolean isCorrect) {
        this.actor = actor;
        this.position = position;
        this.isCorrect = isCorrect;
    }

    @Override
    public String verboseRep() {
        String correctness = this.isCorrect ? "correct" : "incorrect";
        return "Player " + actor + " played his card from position " + position +
            ". It was " + correctness + ".";
    }

    @Override
    public String toString() {
        String correctness = this.isCorrect ? "correct" : "incorrect";
        return "" + actor + " PLAY " + position + " " + correctness;
    }

    @Override
    public JSONObject jsonify() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(JsonUtil.MOVE_TYPE, JsonUtil.PLAY);
        result.put(JsonUtil.ACTOR, actor);
        result.put(JsonUtil.POSITION, position);
        result.put(JsonUtil.CORRECT, isCorrect);
        return result;
    }
}
