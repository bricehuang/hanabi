package hanabi;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import game.Util;

public class PlayState {

    private final Map<Color, Integer> playsByColor;
    private Map<Color, Integer> immutableView;

    public PlayState() {
        this.playsByColor = new TreeMap<>();
        for (Color color : Color.ALL_COLORS) {
            playsByColor.put(color, 0);
        }
        refreshView();
        checkRep();
    }

    private void checkRep() {
        assert(playsByColor.keySet().equals(Color.ALL_COLORS));
        for (Color color : playsByColor.keySet()) {
            int play = playsByColor.get(color);
            assert(0 <= play && play <= 5);
        }
        assert playsByColor.equals(immutableView);
    }

    private void refreshView() {
        this.immutableView = Collections.unmodifiableMap(
            new TreeMap<Color, Integer>(playsByColor)
        );
    }

    public boolean hasBeenPlayed(Color color, int number) {
        return (number <= playsByColor.get(color));
    }

    public boolean isPlayable(Color color, int number) {
        return (number == playsByColor.get(color) + 1);
    }

    public void playCard(Color color, Integer number) {
        assert (this.isPlayable(color, number));
        this.playsByColor.put(color, number);
        refreshView();
        checkRep();
    }

    public Map<Color, Integer> getView() {
        return immutableView;
    }

    @Override
    public String toString() {
        return Util.playsRep(playsByColor);
    }

}
