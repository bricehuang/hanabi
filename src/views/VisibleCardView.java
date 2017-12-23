package views;

import java.util.Set;

import hanabi.Color;

public class VisibleCardView {

    private final Color color;
    private final Integer number;
    private final HiddenCardView hiddenView; 
    
    public VisibleCardView(
        Color color, 
        Integer number, 
        HiddenCardView hiddenView
    ) {
        this.color = color;
        this.number = number;
        this.hiddenView = hiddenView;
    }

    public Color color() {
        return color;
    }

    public Integer number() {
        return number;
    }

    public Set<Color> colors() {
        return hiddenView.colors();
    }

    public Set<Integer> numbers() {
        return hiddenView.numbers();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof VisibleCardView)) { return false; }
        VisibleCardView that = (VisibleCardView) other;
        return (
            this.color.equals(that.color) && 
            this.number == that.number && 
            this.hiddenView.equals(that.hiddenView)
        );
    }

    @Override
    public int hashCode() {
        return (
            this.color.hashCode() + 
            this.number.hashCode() + 
            this.hiddenView.hashCode()
        );
    }

    @Override
    public String toString() {
        return (
            this.color.toString() + 
            this.number + 
            this.hiddenView.toString().substring(2)
        );
    }

}
