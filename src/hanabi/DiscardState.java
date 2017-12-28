package hanabi;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import game.Util;

public class DiscardState {

    private final Map<CardSpec, Integer> discards;
    private Map<CardSpec, Integer> immutableView;

    public static final Comparator<CardSpec> LEX_COMPARATOR = new Comparator<CardSpec>() {
        @Override
        public int compare(CardSpec o1, CardSpec o2) {
            int colorComparison = o1.color.compareTo(o2.color);
            if (colorComparison != 0) {
                return colorComparison;
            } else {
                return o1.number - o2.number;
            }
        }
    };

    public DiscardState() {
        this.discards = new TreeMap<>(LEX_COMPARATOR);
        refreshView();
        checkRep();
    }

    private void checkRep() {
        assert(discards.equals(immutableView));
    }

    private void refreshView() {
        Map<CardSpec, Integer> newView = new TreeMap<CardSpec, Integer>(LEX_COMPARATOR);
        newView.putAll(discards);
        this.immutableView = Collections.unmodifiableMap(newView);
    }

    public void discardCard(Color color, Integer number) {
    		CardSpec colorAndNumber = new CardSpec(color, number);
        if (!discards.keySet().contains(colorAndNumber)) {
            discards.put(colorAndNumber, 0);
        }
        int currentCount = discards.get(colorAndNumber);
        discards.put(colorAndNumber, currentCount + 1);
        refreshView();
        checkRep();
    }

    public Map<CardSpec, Integer> getView() {
        return immutableView;
    }
    
    @Override
    public String toString() {
        return Util.discardsRep(discards);
    }
}
