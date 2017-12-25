package game;

import java.util.List;
import java.util.Map;

import hanabi.Color;
import hanabi.DeckCard;
import javafx.util.Pair;
import move.Move;
import util.ImList;
import views.VisibleHandView;

/**
 * An immutable class representing an omnescient snapshot of the game
 */
public class OmnescientGameView {

    public final int nPlayers;
    public final int handSize;
    public final int playerToMove;
    public final int lives;
    public final int hints;
    public final ImList<DeckCard> deck; 
    public final ImList<Move> history;
    public final Map<Color, Integer> plays;
    public final Map<Pair<Color, Integer>, Integer> discards;
    public final List<VisibleHandView> hands;

    /**
     * Constructor
     * @param nPlayers
     * @param handSize
     * @param playerToMove
     * @param lives
     * @param hints
     * @param deck
     * @param history
     * @param plays expected immutable
     * @param discards expected immutable
     * @param hands expected immutable
     */
    public OmnescientGameView(
        int nPlayers,
        int handSize,
        int playerToMove,
        int lives,
        int hints,
        ImList<DeckCard> deck,
        ImList<Move> history,
        Map<Color, Integer> plays,
        Map<Pair<Color, Integer>, Integer> discards,
        List<VisibleHandView> hands
    ) {
        this.nPlayers = nPlayers;
        this.handSize = handSize;
        this.playerToMove = playerToMove;
        this.lives = lives;
        this.hints = hints;
        this.deck = deck;
        this.history = history;
        this.plays = plays;
        this.discards = discards;
        this.hands = hands;
    }

    public String toString() {
        return (
            Util.toMoveRep(playerToMove) + 
            Util.livesRep(lives) +
            Util.hintsRep(hints) + 
            Util.historyRep(history) + 
            Util.playsRep(plays) +
            Util.discardsRep(discards) +
            Util.handsRep(hands) +
            Util.deckRep(deck) + 
            Util.lastMoveRep(history)
        );
    }

}
