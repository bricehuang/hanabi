package hanabi;

import java.util.Collections;
import java.util.Set;

public class HiddenCardView implements CardView {
    
    private final Set<Color> colors;
    private final Set<Integer> numbers;

    public HiddenCardView(Set<Color> colors, Set<Integer> numbers) {
        this.colors = colors;
        this.numbers = numbers;
    }
    
    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public Color color() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer number() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Color> colors() {
        return Collections.unmodifiableSet(colors);
    }

    @Override
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

    @Override
    public String toString() {
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
        return "("+colorsStr+", "+numbersStr+")";
    }

}
