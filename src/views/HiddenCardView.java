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
        Color.ALL_COLORS, Card.ALL_NUMBERS
    );

    private final Set<Color> colors;
    private final Set<Integer> numbers;

    public HiddenCardView(Set<Color> colors, Set<Integer> numbers) {
        this.colors = new TreeSet<>(colors);
        this.numbers = new TreeSet<>(numbers);
    }

    public Set<Color> colors() {
        return Collections.unmodifiableSet(colors);
    }

    public Set<Integer> numbers() {
        return Collections.unmodifiableSet(numbers);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HiddenCardView)) { return false; }
        HiddenCardView that = (HiddenCardView) other;
        return (
            this.colors.equals(that.colors) && 
            this.numbers.equals(that.numbers)
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
		if (colors.size() == 1) {
			result.put(JsonUtil.COLOR, extractEltFromSingletonSet(colors).toString());			
		} else {
			result.put(JsonUtil.COLOR, JsonUtil.UNKNOWN);
		}
		if (numbers.size() == 1) {
			result.put(JsonUtil.NUMBER, extractEltFromSingletonSet(numbers).toString());			
		} else {
			result.put(JsonUtil.NUMBER, JsonUtil.UNKNOWN);
		}
		result.put(JsonUtil.COLORS, JsonUtil.jsonifyColorSet(colors()));
		result.put(JsonUtil.NUMBERS, JsonUtil.jsonifyNumberSet(numbers()));
		return result;
	}

}
