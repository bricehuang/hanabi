package game;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import hanabi.CardSpec;
import hanabi.Color;
import move.Move;
import util.ImList;
import util.JSONifiable;
import views.HiddenHandView;
import views.VisibleHandView;

public class PlayerGameView implements JSONifiable {

    public final int playerID;
    public final int nPlayers;
    public final int handSize;
    public final int playerToMove;
    public final int lives;
    public final int hints;
    public final ImList<Move> history;
    public final Map<Color, Integer> plays;
    public final Map<CardSpec, Integer> discards;
    public final HiddenHandView myHand;
    public final Map<Integer, VisibleHandView> otherHands;
    public final int cardsLeft;
    public final boolean isOver;

    public PlayerGameView(
        int playerID,
        int nPlayers,
        int handSize,
        int playerToMove,
        int lives,
        int hints,
        ImList<Move> history,
        Map<Color, Integer> plays, // expected immutable
        Map<CardSpec, Integer> discards, // expected immutable
        HiddenHandView myHand,
        Map<Integer, VisibleHandView> otherHands,
        int cardsLeft,
        boolean isOver
    ){
        this.playerID = playerID;
        this.nPlayers = nPlayers;
        this.handSize = handSize;
        this.playerToMove = playerToMove;
        this.lives = lives;
        this.hints = hints;
        this.history = history;
        this.plays = plays;
        this.discards = discards;
        this.myHand = myHand;
        this.otherHands = otherHands;
        this.cardsLeft = cardsLeft;
        this.isOver = isOver;
    }

    public String toString() {
        return (
            Util.playerIDRep(playerID) +
            Util.toMoveRep(playerToMove) +
            Util.livesRep(lives) +
            Util.hintsRep(hints) +
            Util.historyRep(history) +
            Util.playsRep(plays) +
            Util.discardsRep(discards) +
            Util.deckLengthRep(cardsLeft) +
            Util.handsRep(playerID, otherHands, myHand) +
            Util.lastMoveRep(history) +
            Util.isOverRep(isOver, plays)
        );
    }

    @Override
    public JSONObject jsonify() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(JsonUtil.IS_OMNESCIENT, false);
        result.put(JsonUtil.N_PLAYERS, nPlayers);
        result.put(JsonUtil.HAND_SIZE, handSize);
        result.put(JsonUtil.TO_MOVE, playerToMove);
        result.put(JsonUtil.LIVES, lives);
        result.put(JsonUtil.HINTS, hints);
        result.put(JsonUtil.HISTORY, history.jsonArrayify());
        result.put(JsonUtil.LAST_MOVE, Util.lastMoveRep(history));
        result.put(JsonUtil.PLAYS, JsonUtil.jsonifyPlays(plays));
        result.put(JsonUtil.DISCARDS, JsonUtil.jsonifyDiscards(discards));
        result.put(JsonUtil.HANDS, JsonUtil.jsonifyPlayerHandViews(otherHands, myHand, playerID));
        result.put(JsonUtil.CARDS_LEFT, cardsLeft);
        result.put(JsonUtil.IS_OVER, isOver);
        return result;
    }

}
