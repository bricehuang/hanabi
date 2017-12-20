package hanabi;

import java.util.Set;

public interface CardView {

    public boolean isVisible();

    public Color color();
    public Integer number();

    public Set<Color> colors();
    public Set<Integer> numbers();
}
