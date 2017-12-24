package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hanabi.Card;
import hanabi.Color;
import hanabi.Hand;
import javafx.util.Pair;
import move.ColorHint;
import move.Discard;
import move.Move;
import move.NumberHint;
import move.Play;
import util.ImList;

public class Game {

    private final int nPlayers;
    private final int handSize;
    private int playerToMove;
    private int lives;
    private int hints;
    private final LinkedList<Card> deck; 
    private ImList<Move> history;
    private final Map<Color, Integer> plays;
    private final Map<Color, Map<Integer, Integer> > discards;
    private final List<Hand> hands;

    private static final List<Integer> NUMBER_DIST = Arrays.asList(1,1,1,2,2,3,3,4,4,5);
    private static final Map<Integer, Integer> HAND_SIZE_BY_NPLAYERS;
    static{
        Map<Integer, Integer> tmp = new TreeMap<>();
        tmp.put(2, 5);
        tmp.put(3, 5);
        tmp.put(4, 4);
        tmp.put(5, 4);
        HAND_SIZE_BY_NPLAYERS = Collections.unmodifiableMap(tmp);
    }
    private static final int LIVES = 3;
    private static final int HINTS = 8;

    private static List<Card> generateRandomDeck() {
        List<Card> deck = new ArrayList<>();
        for (Color color : Color.ALL_COLORS) {
            for (Integer number : NUMBER_DIST) {
                deck.add(new Card(color, number));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    public Game(int nPlayers) {
        this(generateRandomDeck(), nPlayers);
    }

    public Game(List<Card> deck, int nPlayers) {
        this.nPlayers = nPlayers;
        assert HAND_SIZE_BY_NPLAYERS.keySet().contains(nPlayers);
        this.handSize = HAND_SIZE_BY_NPLAYERS.get(nPlayers);
        this.playerToMove = 0;
        this.lives = LIVES;
        this.hints = HINTS;
        this.deck = new LinkedList<>(deck);
        this.history = ImList.<Move>empty();
        this.plays = new TreeMap<>();
        this.discards = new TreeMap<>();
        for (Color color : Color.ALL_COLORS) {
            plays.put(color, 0);
            discards.put(color, new TreeMap<>());
        }
        this.hands = new ArrayList<>();
        for (int i=0; i<this.nPlayers; i++) {
            LinkedList<Card> dealtCards = new LinkedList<>();
            for (int j=0; j<this.handSize; j++) {
                dealtCards.addLast(this.deck.removeFirst());
            }
            this.hands.add(new Hand(this.handSize, dealtCards));
        }
    }

    /*
     * Play functions.  These modify game state.  
     */
    private void updatePlayerToMove() {
        this.playerToMove = (this.playerToMove + 1)%5;
    }

    /**
     * Gives a color hint.  Mutates game state.  
     * @param player player to hint
     * @param color color of hint given
     * @return (success, error message).  Valid hints return (true, "").  
     * invalid hints return (false, error message) and are guaranteed to 
     * not mutate game state.    
     */
    public Pair<Boolean, String> hint(int player, Color color) {
        if (player < 0 || player >= nPlayers) {
            return new Pair<Boolean, String>(false, "Invalid Player.");
        } else if (player == this.playerToMove) {
            return new Pair<Boolean, String>(false, "You cannot hint yourself.");
        } else if (hints == 0) {
            return new Pair<Boolean, String>(false, "You have no hints.");
        } else {
            boolean success = hands.get(player).hintColor(color);
            if (success){
                this.history = history.extend(
                    new ColorHint(this.playerToMove, player, color)
                );
                this.hints -= 1;
                updatePlayerToMove();
                return new Pair<Boolean, String>(true, "");
            } else {
                return new Pair<Boolean, String>(false, "That color doesn't exist in that player's hand");                
            }
        }
    }

    /**
     * Gives a number hint.  Mutates game state.  
     * @param player player to hint
     * @param number number of hint given
     * @return (success, error message).  Valid hints return (true, "").  
     * invalid hints return (false, error message) and are guaranteed to 
     * not mutate game state.    
     */
    public Pair<Boolean, String> hint(int player, int number) {
        if (player < 0 || player >= nPlayers) {
            return new Pair<Boolean, String>(false, "Invalid Player.");
        } else if (number < Card.NUMBER_MIN || number > Card.NUMBER_MAX){
            return new Pair<Boolean, String>(false, "Invalid Number.");
        } else if (player == this.playerToMove) {
            return new Pair<Boolean, String>(false, "You cannot hint yourself.");
        } else if (hints == 0) {
            return new Pair<Boolean, String>(false, "You have no hints.");
        } else {
            boolean success = hands.get(player).hintNumber(number);
            if (success){
                this.history = history.extend(
                    new NumberHint(this.playerToMove, player, number)
                );
                this.hints -= 1;
                updatePlayerToMove();
                return new Pair<Boolean, String>(true, "");
            } else {
                return new Pair<Boolean, String>(false, "That number doesn't exist in that player's hand");                
            }
        }
    }

    private Card playOrDiscardPosition(int position) { 
        if (deck.size() > 0) {
            Card newCard = deck.removeFirst();
            return hands.get(playerToMove).playOrDiscard(position, newCard);
        } else {
            return hands.get(playerToMove).playOrDiscardLast(position);
        }
    }

    private void addDiscard(Card card) { 
        if (!discards.get(card.color()).containsKey(card.number())){
            discards.get(card.color()).put(card.number(), 0);
        }
        int currentDiscardCount = discards.get(card.color()).get(card.number());
        discards.get(card.color()).put(card.number(), currentDiscardCount + 1);
    }

    public Pair<Boolean, String> play(int position) {
        if (position < 0 || position >= handSize) {
            return new Pair<Boolean, String>(false, "Invalid Card Position.");
        } else {
            Card playedCard = playOrDiscardPosition(position);
            int expectedRank = plays.get(playedCard.color()) + 1;
            boolean playCorrect = (playedCard.number() == expectedRank);
            if (playCorrect) {
                plays.put(playedCard.color(), (Integer) playedCard.number());
            } else {
                addDiscard(playedCard);
                this.lives--;
            }
            this.history = history.extend(
                new Play(this.playerToMove, position, playCorrect)
            );
            updatePlayerToMove();
            return new Pair<Boolean, String>(true, "");
        }
    }

    public Pair<Boolean, String> discard(int position) {
        if (position < 0 || position >= handSize) {
            return new Pair<Boolean, String>(false, "Invalid Card Position.");
        } else {
            Card discardedCard = playOrDiscardPosition(position);
            int safeRank = plays.get(discardedCard.color());
            addDiscard(discardedCard);
            if (hints < 8) {
                hints++;
            }
            boolean discardSafe = (discardedCard.number() <= safeRank);
            this.history = history.extend(
                new Discard(this.playerToMove, position, discardSafe)
            );
            updatePlayerToMove();
            return new Pair<Boolean, String>(true, "");
        }
    }

    /*
     * Print functions
     */

    public String fullState() {
        return (
            Util.toMoveRep(playerToMove) + 
            Util.livesRep(lives) + 
            Util.hintsRep(hints) + 
            Util.historyRep(history) + 
            Util.playsRep(plays) + 
            Util.discardsRep(discards) + 
            Util.handsRep(nPlayers, hands) + 
            Util.deckRep(deck) + 
            Util.lastMoveRep(history)
        );
    }

    @Override
    public String toString() {
        return (
            Util.toMoveRep(playerToMove) + 
            Util.livesRep(lives) + 
            Util.hintsRep(hints) + 
            Util.playsRep(plays) + 
            Util.discardsRep(discards) + 
            Util.handsRep(nPlayers, hands) + 
            Util.lastMoveRep(history)
        );
    }

}
