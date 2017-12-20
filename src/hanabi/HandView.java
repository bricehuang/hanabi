package hanabi;

import java.util.Collections;
import java.util.List;

public class HandView {

    public final boolean isVisible;
    public final List<CardView> cardViews;

    public HandView(boolean isVisible, List<CardView> cardViews){
        this.isVisible = isVisible;
        this.cardViews = Collections.unmodifiableList(cardViews);
        checkRep();
    }

    private void checkRep() {
        for (CardView cardView : cardViews) {
            assert (cardView.isVisible() == isVisible);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HandView)) { return false; }
        HandView that = (HandView) other;
        return (
            this.isVisible == that.isVisible && 
            this.cardViews.equals(that.cardViews)
        );
    }

    @Override
    public int hashCode() {
        return (this.isVisible ? 1337 : 9001) + this.cardViews.hashCode();
    }

    @Override
    public String toString() {
        String rep = "[";
        for (CardView cardView : cardViews) {
            rep += cardView.toString();
        }
        rep += "]";
        return rep;
    }

}
