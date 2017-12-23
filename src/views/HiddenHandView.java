package views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HiddenHandView {
    
    public final List<HiddenCardView> cardViews;

    public HiddenHandView(List<HiddenCardView> cardViews){
        this.cardViews = Collections.unmodifiableList(
            new ArrayList<>(cardViews)
        );
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HiddenHandView)) { return false; }
        HiddenHandView that = (HiddenHandView) other;
        return this.cardViews.equals(that.cardViews);
    }

    @Override
    public int hashCode() {
        return this.cardViews.hashCode();
    }

    @Override
    public String toString() {
        String rep = "[";
        for (HiddenCardView cardView : cardViews) {
            rep += cardView.toString() + ", ";
        }
        rep += "]";
        return rep;
    }
}
