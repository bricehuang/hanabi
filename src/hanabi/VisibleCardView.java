package hanabi;

import java.util.Set;

public class VisibleCardView implements CardView {

    private final Color color;
    private final Integer number;

    public VisibleCardView(Color color, Integer number) {
        this.color = color;
        this.number = number;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Color color() {
        return color;
    }

    @Override
    public Integer number() {
        return number;
    }

    @Override
    public Set<Color> colors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> numbers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof VisibleCardView)) { return false; }
        VisibleCardView that = (VisibleCardView) other;
        return (
            this.color.equals(that.color) && 
            this.number == that.number
        );
    }

    @Override
    public int hashCode() {
        return this.color.hashCode() + this.number.hashCode();
    }

    @Override
    public String toString() {
        return this.color.toString()+this.number;
    }

}
