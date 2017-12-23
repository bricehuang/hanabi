package views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
