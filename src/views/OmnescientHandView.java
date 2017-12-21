package views;

import java.util.Collections;
import java.util.List;

public class OmnescientHandView {

    public final List<OmnescientCardView> cardViews;

    public OmnescientHandView(List<OmnescientCardView> cardViews) {
        this.cardViews = Collections.unmodifiableList(cardViews);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof OmnescientHandView)) { return false; }
        OmnescientHandView that = (OmnescientHandView) other;
        return (this.cardViews.equals(that.cardViews));
    }

    @Override
    public int hashCode() {
        return this.cardViews.hashCode();
    }

    @Override
    public String toString() {
        String rep = "[";
        for (OmnescientCardView cardView : cardViews) {
            rep += cardView.toString() + ", ";
        }
        rep += "]";
        return rep;
    }

}
