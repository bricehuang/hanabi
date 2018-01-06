package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hanabi.Card;
import hanabi.Color;
import hanabi.Deck;
import hanabi.DiscardState;
import hanabi.Hand;
import hanabi.PlayState;
import javafx.util.Pair;
import move.ColorHint;
import move.Discard;
import move.Move;
import move.NumberHint;
import move.Play;
import move.Resignation;
import util.ImList;
import views.VisibleHandView;

public class Game {

    private final int nPlayers;
    private final int handSize;
    private int playerToMove;
    private int lives;
    private int hints;
    private final Deck deck;
    private ImList<Move> history;
    private final PlayState plays;
    private final DiscardState discards;
    private final List<Hand> hands;
    private boolean isOver;

    private OmnescientGameView omnescientView;
    private List<PlayerGameView> playerViews;

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

    public Game(int nPlayers) {
        this(new Deck(), nPlayers);
    }

    public Game(Deck deck, int nPlayers) {
        this.nPlayers = nPlayers;
        assert HAND_SIZE_BY_NPLAYERS.keySet().contains(nPlayers);
        this.handSize = HAND_SIZE_BY_NPLAYERS.get(nPlayers);
        this.playerToMove = 0;
        this.lives = LIVES;
        this.hints = HINTS;
        this.deck = deck;
        this.history = ImList.<Move>empty();
        this.plays = new PlayState();
        this.discards = new DiscardState();
        this.hands = new ArrayList<>();
        for (int i=0; i<this.nPlayers; i++) {
            LinkedList<Card> dealtCards = new LinkedList<>();
            for (int j=0; j<this.handSize; j++) {
                dealtCards.addLast(deck.draw());
            }
            this.hands.add(new Hand(this.handSize, dealtCards));
        }
        this.isOver = false;
        refreshViews();
    }

    private void refreshViews() {
        List<VisibleHandView> handViews = new ArrayList<>();
        for (Hand hand : hands) {
            handViews.add(hand.visibleView());
        }
        this.omnescientView = new OmnescientGameView(
            nPlayers,
            handSize,
            playerToMove,
            lives,
            hints,
            deck.getView(),
            history,
            plays.getView(),
            discards.getView(),
            Collections.unmodifiableList(handViews),
            isOver
        );
        List<PlayerGameView> playerViews = new ArrayList<>();
        for (int i=0; i<nPlayers; i++) {
            playerViews.add(constructPlayerView(i));
        }
        this.playerViews = Collections.unmodifiableList(playerViews);
    }

    private PlayerGameView constructPlayerView(int playerID) {
        assert (0<= playerID && playerID < nPlayers);
        Map<Integer, VisibleHandView> visibleHands = new TreeMap<>();
        for (int i=0; i<nPlayers; i++) {
            if (i != playerID) {
                visibleHands.put(i, hands.get(i).visibleView());
            }
        }
        return new PlayerGameView(
            playerID,
            nPlayers,
            handSize,
            playerToMove,
            lives,
            hints,
            history,
            plays.getView(),
            discards.getView(),
            hands.get(playerID).hiddenView(),
            Collections.unmodifiableMap(visibleHands),
            deck.size(),
            isOver
        );
    }

    public OmnescientGameView getOmnescientView() {
        return this.omnescientView;
    }

    public PlayerGameView getPlayerView(int player) {
        return this.playerViews.get(player);
    }

    /*
     * Play functions.  These modify game state.
     */
    private void updatePlayerToMove() {
        this.playerToMove = (this.playerToMove + 1)%nPlayers;
    }

    private void updateIsOver() {
        boolean livesEmpty = (lives == 0);
        boolean allCardsPlayed = true;
        for (Color color : Color.ALL_COLORS) {
            allCardsPlayed = allCardsPlayed && plays.hasBeenPlayed(color, Card.NUMBER_MAX);
        }
        boolean timeRunOut = true;
        for (Hand hand : hands) {
            timeRunOut = timeRunOut && hand.isFinished();
        }
        this.isOver = (livesEmpty || allCardsPlayed || timeRunOut);
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
        assert !isOver;
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
                updateIsOver();
                refreshViews();
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
        assert !isOver;
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
                updateIsOver();
                refreshViews();
                return new Pair<Boolean, String>(true, "");
            } else {
                return new Pair<Boolean, String>(false, "That number doesn't exist in that player's hand");
            }
        }
    }

    private Card playOrDiscardPosition(int position) {
        if (deck.size() > 0) {
            Card newCard = deck.draw();
            return hands.get(playerToMove).playOrDiscard(position, newCard);
        } else {
            return hands.get(playerToMove).playOrDiscardLast(position);
        }
    }

    /**
     * Plays card from specified position
     * @param position
     * @return (success, error message).  Valid hints return (true, "").
     * invalid hints return (false, error message) and are guaranteed to
     * not mutate game state.
     */
    public Pair<Boolean, String> play(int position) {
        assert !isOver;
        if (position < 0 || position >= handSize) {
            return new Pair<Boolean, String>(false, "Invalid Card Position.");
        } else {
            Card playedCard = playOrDiscardPosition(position);
            boolean playCorrect = plays.isPlayable(playedCard.color(), playedCard.number());
            if (playCorrect) {
                plays.playCard(playedCard.color(), playedCard.number());
            } else {
                discards.discardCard(playedCard.color(), playedCard.number());
                this.lives--;
            }
            this.history = history.extend(
                new Play(this.playerToMove, position, playCorrect)
            );
            updatePlayerToMove();
            updateIsOver();
            refreshViews();
            return new Pair<Boolean, String>(true, "");
        }
    }

    /**
     * Discards card from specified position
     * @param position
     * @return (success, error message).  Valid hints return (true, "").
     * invalid hints return (false, error message) and are guaranteed to
     * not mutate game state.
     */
    public Pair<Boolean, String> discard(int position) {
        assert !isOver;
        if (position < 0 || position >= handSize) {
            return new Pair<Boolean, String>(false, "Invalid Card Position.");
        } else {
            Card discardedCard = playOrDiscardPosition(position);
            discards.discardCard(discardedCard.color(), discardedCard.number());
            if (hints < 8) {
                hints++;
            }
            boolean discardSafe = plays.hasBeenPlayed(
                discardedCard.color(), discardedCard.number()
            );
            this.history = history.extend(
                new Discard(this.playerToMove, position, discardSafe)
            );
            updatePlayerToMove();
            updateIsOver();
            refreshViews();
            return new Pair<Boolean, String>(true, "");
        }
    }

    /**
     * Resigns and ends the game
     * @return (success, error message).  Valid hints return (true, "").
     * invalid hints return (false, error message) and are guaranteed to
     * not mutate game state.
     */
    public Pair<Boolean, String> resign(int actor) {
        assert !isOver;
        this.isOver = true;
        this.history = history.extend(
            new Resignation(actor)
        );
        refreshViews();
        return new Pair<Boolean, String>(true, "");
    }

    public Pair<Boolean, String> handleAction(int playerIndex, String move, JSONArray cards) throws JSONException{
        switch (move) {
            case "color_hint":
                assert playerIndex == this.playerToMove;
                return handleColorHint(cards);
            case "number_hint":
                assert playerIndex == this.playerToMove;
                return handleNumberHint(cards);
            case "play":
                assert playerIndex == this.playerToMove;
                return handlePlay(cards);
            case "discard":
                assert playerIndex == this.playerToMove;
                return handleDiscard(cards);
            case "resign":
                return resign(playerIndex);
            default:
                return new Pair<Boolean, String>(false, "Invalid action.");
        }
    }
    
    private static final String EMPTY_HINT_ERR = "You cannot hint zero cards.";
    private static final String MULTIPLE_PLAYER_HINT_ERR = "You cannot hint cards from more than one player.";
    private static final String SELF_HINT_ERR = "You cannot hint yourself.";
    private static final String INVALID_INDICES_ERR = "Invalid player or card index.";
    private static final String INVALID_COLOR_HINT_ERR = "Invalid color hint.";
    private static final String INVALID_NUMBER_HINT_ERR = "Invalid number hint.";
    private static final String PLAY_NOT_1_CARD_ERR = "You can only play one card per turn.";
    private static final String PLAY_NOT_OWN_CARD_ERR = "You can only play your own cards.";
    private static final String DISCARD_NOT_1_CARD_ERR = "You can only discard one card per turn.";
    private static final String DISCARD_NOT_OWN_CARD_ERR = "You can only discard your own cards.";
    private boolean cardsBelongToSamePlayer(JSONArray cards) throws JSONException {
        for (int i=0; i<cards.length(); i++) {
            if (cards.getJSONObject(i).getInt("player") != cards.getJSONObject(0).getInt("player")) {
                return false;
            }
        }
        return true;
    }
    private boolean cardsBelongToSelf(JSONArray cards) throws JSONException {
        return cards.getJSONObject(0).getInt("player") == this.playerToMove; 
    }
    private boolean indicesValid(JSONArray cards) throws JSONException {
        for (int i=0; i<cards.length(); i++) {
            JSONObject card = cards.getJSONObject(i);
            int player = card.getInt("player");
            int position = card.getInt("position");
            if (player < 0 || player >= nPlayers || position < 0 || position >= handSize) {
                return false;
            }
        }
        return true;
    }
    private boolean isValidColorHint(JSONArray cards) throws JSONException {
        int player = cards.getJSONObject(0).getInt("player");
        Set<Integer> hintedCardPositions = new TreeSet<>();
        for (int i=0; i<cards.length(); i++) {
            hintedCardPositions.add(cards.getJSONObject(i).getInt("position"));
        }
        return hands.get(player).isValidColorHint(hintedCardPositions);
    }
    private boolean isValidNumberHint(JSONArray cards) throws JSONException {
        int player = cards.getJSONObject(0).getInt("player");
        Set<Integer> hintedCardPositions = new TreeSet<>();
        for (int i=0; i<cards.length(); i++) {
            hintedCardPositions.add(cards.getJSONObject(i).getInt("position"));
        }
        return hands.get(player).isValidNumberHint(hintedCardPositions);
    }

    private Pair<Boolean, String> handleColorHint(JSONArray cards) throws JSONException {
        if (cards.length() == 0) {
            return new Pair<Boolean, String>(false, EMPTY_HINT_ERR);
        } else if (!cardsBelongToSamePlayer(cards)) {
            return new Pair<Boolean, String> (false, MULTIPLE_PLAYER_HINT_ERR);
        } else if (cardsBelongToSelf(cards)) {
            return new Pair<Boolean, String> (false, SELF_HINT_ERR);
        } else if (!indicesValid(cards)) {
            return new Pair<Boolean, String> (false, INVALID_INDICES_ERR);
        } else if (!isValidColorHint(cards)) {
            return new Pair<Boolean, String> (false, INVALID_COLOR_HINT_ERR);
        } else {
            JSONObject firstCard = cards.getJSONObject(0);
            int player = firstCard.getInt("player");
            int position = firstCard.getInt("position");
            Color color = omnescientView.hands.get(player).cardViews.get(position).color();
            return hint(player, color);
        }
    }
    private Pair<Boolean, String> handleNumberHint(JSONArray cards) throws JSONException {
        if (cards.length() == 0) {
            return new Pair<Boolean, String>(false, EMPTY_HINT_ERR);
        } else if (!cardsBelongToSamePlayer(cards)) {
            return new Pair<Boolean, String> (false, MULTIPLE_PLAYER_HINT_ERR);
        } else if (cardsBelongToSelf(cards)) {
            return new Pair<Boolean, String> (false, SELF_HINT_ERR);
        } else if (!indicesValid(cards)) {
            return new Pair<Boolean, String> (false, INVALID_INDICES_ERR);
        } else if (!isValidNumberHint(cards)) {
            return new Pair<Boolean, String> (false, INVALID_NUMBER_HINT_ERR);
        } else {
            JSONObject firstCard = cards.getJSONObject(0);
            int player = firstCard.getInt("player");
            int position = firstCard.getInt("position");
            Integer number = omnescientView.hands.get(player).cardViews.get(position).number();
            return hint(player, number);
        }
    }
    private Pair<Boolean, String> handlePlay(JSONArray cards) throws JSONException {
        if (cards.length() != 1) {
            return new Pair<Boolean, String>(false, PLAY_NOT_1_CARD_ERR);
        } else if (!indicesValid(cards)) {
            return new Pair<Boolean, String> (false, INVALID_INDICES_ERR);
        }
        JSONObject card = cards.getJSONObject(0);
        int player = card.getInt("player");
        int position = card.getInt("position");
        if (player != playerToMove) {
            return new Pair<Boolean, String>(false, PLAY_NOT_OWN_CARD_ERR);
        } else {
            return play(position);
        }
    }
    private Pair<Boolean, String> handleDiscard(JSONArray cards) throws JSONException {
        if (cards.length() != 1) {
            return new Pair<Boolean, String>(false, DISCARD_NOT_1_CARD_ERR);
        } else if (!indicesValid(cards)) {
            return new Pair<Boolean, String> (false, INVALID_INDICES_ERR);
        }
        JSONObject card = cards.getJSONObject(0);
        int player = card.getInt("player");
        int position = card.getInt("position");
        if (player != playerToMove) {
            return new Pair<Boolean, String>(false, DISCARD_NOT_OWN_CARD_ERR);
        } else {
            return discard(position);
        }
    }
    
    /*
     * Print functions
     */
    @Override
    public String toString() {
        return omnescientView.toString();
    }

    private static final Map<String, Color> COLOR_BY_SHORT_NAME;
    static{
        Map<String, Color> tmp = new TreeMap<>();
        tmp.put("B", Color.BLUE);
        tmp.put("G", Color.GREEN);
        tmp.put("R", Color.RED);
        tmp.put("W", Color.WHITE);
        tmp.put("Y", Color.YELLOW);
        COLOR_BY_SHORT_NAME = Collections.unmodifiableMap(tmp);
    }

    public Pair<Boolean, String> makeMove(String in) {
        int maxPlayerIndex = nPlayers-1;
        int maxCardIndex = handSize-1;
        if (! in.matches("(hint [0-"+maxPlayerIndex+"] (B|G|R|W|Y|1|2|3|4|5))|(play [0-"+maxCardIndex+"])|(discard [0-"+maxCardIndex+"])|(resign [0-"+maxPlayerIndex+"])")){
            return new Pair<Boolean, String> (
                false,
                "Allowed inputs:\n" +
                "hint [0-"+maxPlayerIndex+"] [12345BGRWY] to hint\n" +
                "play [0-"+maxCardIndex+"] to play\n" +
                "discard [0-"+maxCardIndex+"] to discard\n" +
                "resign [0-"+maxPlayerIndex+"] to resign\n"
            );
        }
        String[] tokens = in.split(" ");
        if (tokens[0].equals("hint")) {
            int hintee = Integer.parseInt(tokens[1]);
            if (tokens[2].matches("[1-5]")) {
                int number = Integer.parseInt(tokens[2]);
                return hint(hintee, number);
            } else {
                Color color = COLOR_BY_SHORT_NAME.get(tokens[2]);
                return hint(hintee, color);
            }
        } else if (tokens[0].equals("play")) {
            int position = Integer.parseInt(tokens[1]);
            return play(position);
        } else if (tokens[0].equals("discard")) {
            int position = Integer.parseInt(tokens[1]);
            return discard(position);
        } else if (tokens[0].equals("resign")){
        		int actor = Integer.parseInt(tokens[1]);
            return resign(actor);
        } else {
            throw new RuntimeException("Should not get here.");
        }
    }

    /**
     * Simulate a game
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Hanabi!\nHow many players?");
        int nPlayers = Integer.parseInt(scanner.nextLine());
        while (!(2 <= nPlayers && nPlayers <= 5)) {
            System.out.println("Number of players must be between 2 and 5.\nHow many players?");
            nPlayers = Integer.parseInt(scanner.nextLine());
        }
        Game game = new Game(nPlayers);
        System.out.println(game);
        while (! game.isOver) {
            String in = scanner.nextLine();
            Pair<Boolean, String> result = game.makeMove(in);
            if (! result.getKey()) {
                System.out.println(result.getValue());
            } else {
                System.out.println(game);
            }
        }
        scanner.close();
    }

}
