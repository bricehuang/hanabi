package game;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hanabi.Color;
import move.Move;
import views.HandView;

public class PlayerGameView {
    public final int player;
    public final int nPlayers;
    public final int handSize;
    public final int playerToMove;
    public final int lives;
    public final int hints;
    public final List<Move> history;
    public final Map<Color, Integer> plays;
    public final Map<Color, Map<Integer, Integer> > discards;
    public final List<HandView> handViews;

    public PlayerGameView(
        int player,
        int nPlayers, 
        int handSize, 
        int playerToMove,
        int lives,
        int hints,
        List<Move> history,
        Map<Color, Integer> plays,
        Map<Color, Map<Integer, Integer> > discards,
        List<HandView> handViews
    ){
        this.player = player;
        this.nPlayers = nPlayers;
        this.handSize = handSize;
        this.playerToMove = playerToMove;
        this.lives = lives;
        this.hints = hints;
        this.history = Collections.unmodifiableList(history);
        this.plays = Collections.unmodifiableMap(plays);
        Map<Color, Map<Integer, Integer>> tmpDiscards = new TreeMap<>();
        for (Color color : Color.ALL_COLORS) {
            tmpDiscards.put(
                color, 
                Collections.unmodifiableMap(discards.get(color))
            );
        }
        this.discards = Collections.unmodifiableMap(tmpDiscards);
        this.handViews = Collections.unmodifiableList(handViews);
        checkRep();
    }

    private void checkRep() {
        for (int i=0; i<handViews.size(); i++) {
            assert(handViews.get(i).isVisible == (i != player));
        }
    }

}
