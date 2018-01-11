package move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.JsonUtil;

public class NumberHint implements Move {

    public final int actor;

    public final int hintee;
    public final Integer number;
    public final List<Integer> positions;

    public NumberHint(int actor, int hintee, Integer number, List<Integer> positions) {
        assert actor != hintee;
        this.actor = actor;
        this.hintee = hintee;
        this.number = number;
        this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
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
        JSONArray positionsJson = new JSONArray(positions);
        result.put(JsonUtil.POSITIONS, positionsJson);
        return result;
    }

}
