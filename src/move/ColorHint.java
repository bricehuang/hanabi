package move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.JsonUtil;
import hanabi.Color;

public class ColorHint implements Move {

    public final int actor;

    public final int hintee;
    public final Color color;
    public final List<Integer> positions;

    public ColorHint(int actor, int hintee, Color color, List<Integer> positions) {
        assert actor != hintee;
        this.actor = actor;
        this.hintee = hintee;
        this.color = color;
        this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
    }

    @Override
    public String verboseRep() {
        return "Player " + actor + " hinted Player " + hintee +
            "'s " + color.verboseRep() + "s.";
    }

    @Override
    public String toString() {
        return "" + actor + " HINT " + hintee + " " + color.toString();
    }

    @Override
    public JSONObject jsonify() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(JsonUtil.MOVE_TYPE, JsonUtil.COLOR_HINT);
        result.put(JsonUtil.ACTOR, actor);
        result.put(JsonUtil.HINTEE, hintee);
        result.put(JsonUtil.COLOR, color.toString());
        JSONArray positionsJson = new JSONArray(positions);
        result.put(JsonUtil.POSITIONS, positionsJson);
        return result;
    }

}
