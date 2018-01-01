package util;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONifiable {

    public JSONObject jsonify() throws JSONException;

}
