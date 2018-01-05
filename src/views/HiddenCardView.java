package views;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import game.JsonUtil;
import hanabi.Card;
import hanabi.Color;
import util.JSONifiable;

public class HiddenCardView implements JSONifiable {

    public static final HiddenCardView NO_INFO = new HiddenCardView(
        Color.ALL_COLORS, Card.ALL_NUMBERS, false, false
    );

    private final Set<Color> colors;
    private final Set<Integer> numbers;
    private final boolean colorHinted;
    private final boolean numberHinted;

    public HiddenCardView(Set<Color> colors, Set<Integer> numbers, boolean colorHinted, boolean numberHinted) {
        this.colors = new TreeSet<>(colors);
        this.numbers = new TreeSet<>(numbers);
        this.colorHinted = colorHinted;
        this.numberHinted = numberHinted;
        checkRep();
    }
    
    private void checkRep() {
        if (colorHinted) { assert colors.size() == 1; }
        if (numberHinted) { assert numbers.size() == 1; }
    }

    public Set<Color> colors() {
        return Collections.unmodifiableSet(colors);
    }
    public Set<Integer> numbers() {
        return Collections.unmodifiableSet(numbers);
    }
    public boolean colorHinted() {
        return colorHinted;
    }
    public boolean numberHinted() {
        return numberHinted;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HiddenCardView)) { return false; }
        HiddenCardView that = (HiddenCardView) other;
        return (
            this.colors.equals(that.colors) &&
            this.numbers.equals(that.numbers) && 
            this.colorHinted == that.colorHinted &&
            this.numberHinted == that.numberHinted
        );
    }

    @Override
    public int hashCode() {
        return this.colors.hashCode() + this.numbers.hashCode();
    }

    private static<T> T extractEltFromSingletonSet(Set<T> set) {
        assert(set.size() == 1);
        return (set.iterator().next());
    }

    @Override
    public String toString() {
        String colorStr = (colors.size() == 1) ?
            extractEltFromSingletonSet(colors).toString() : "?";
        String numberStr = (numbers.size() == 1) ?
            extractEltFromSingletonSet(numbers).toString() : "?";
        String colorsStr = "";
        for (Color color : this.colors) {
            colorsStr += color;
        }
        for (int i=0; i< Color.NUM_COLORS - this.colors.size(); i++) {
            colorsStr += " ";
        }
        String numbersStr = "";
        for (Integer number: this.numbers) {
            numbersStr += number;
        }
        for (int i=0; i< Card.NUMBER_MAX - this.numbers.size(); i++) {
            numbersStr += " ";
        }
        return colorStr+numberStr+" ("+colorsStr+", "+numbersStr+")";
    }

    @Override
    public JSONObject jsonify() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(
            JsonUtil.COLOR, 
            colorHinted ? extractEltFromSingletonSet(colors).toString() : JsonUtil.UNKNOWN
        );
        result.put(
            JsonUtil.NUMBER, 
            numberHinted ? extractEltFromSingletonSet(numbers).toString() : JsonUtil.UNKNOWN
        );
        result.put(JsonUtil.HINTED, colorHinted || numberHinted);
        return result;
    }

}
