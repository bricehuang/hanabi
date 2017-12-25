package hanabi;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import game.Util;
import javafx.util.Pair;

public class DiscardState {

    private final Map<Pair<Color, Integer>, Integer> discards;
    private Map<Pair<Color, Integer>, Integer> immutableView;

    public static final Comparator<Pair<Color, Integer>> LEX_COMPARATOR = new Comparator<Pair<Color, Integer>>() {
        @Override
        public int compare(Pair<Color, Integer> o1, Pair<Color, Integer> o2) {
            int colorComparison = o1.getKey().compareTo(o2.getKey());
            if (colorComparison != 0) {
                return colorComparison;
            } else {
                return o1.getValue().compareTo(o2.getValue());
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
        Map<Pair<Color, Integer>, Integer> newView = new TreeMap<Pair<Color, Integer>, Integer>(LEX_COMPARATOR);
        newView.putAll(discards);
        this.immutableView = Collections.unmodifiableMap(newView);
    }

    public void discardCard(Color color, Integer number) {
        Pair<Color, Integer> colorAndNumber = new Pair<Color, Integer>(color, number);
        if (!discards.keySet().contains(colorAndNumber)) {
            discards.put(colorAndNumber, 0);
        }
        int currentCount = discards.get(colorAndNumber);
        discards.put(colorAndNumber, currentCount + 1);
        refreshView();
        checkRep();
    }

    public Map<Pair<Color, Integer>, Integer> getView() {
        return immutableView;
    }
    
    @Override
    public String toString() {
        return Util.discardsRep(discards);
    }
}
