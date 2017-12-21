package views;

import java.util.Set;

import hanabi.Color;

public interface CardView {

    public boolean isVisible();

    public Color color();
    public Integer number();

    public Set<Color> colors();
    public Set<Integer> numbers();
}
