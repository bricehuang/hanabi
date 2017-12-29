package views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class VisibleHandView {
    
    public final List<VisibleCardView> cardViews;

    public VisibleHandView(List<VisibleCardView> cardViews){
        this.cardViews = Collections.unmodifiableList(
            new ArrayList<>(cardViews)
        );
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof VisibleHandView)) { return false; }
        VisibleHandView that = (VisibleHandView) other;
        return this.cardViews.equals(that.cardViews);
    }

    @Override
    public int hashCode() {
        return this.cardViews.hashCode();
    }

    @Override
    public String toString() {
        String rep = "[";
        for (VisibleCardView cardView : cardViews) {
            rep += cardView.toString() + ", ";
        }
        rep += "]";
        return rep;
    }

    public JSONArray jsonArrayify() throws JSONException {
    		JSONArray result = new JSONArray();
    		for (VisibleCardView cardView : cardViews) {
    			result.put(cardView.jsonify());
    		}
    		return result;
    }

}
