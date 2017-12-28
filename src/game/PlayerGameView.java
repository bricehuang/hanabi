package game;

import java.util.Map;

import hanabi.CardSpec;
import hanabi.Color;
import move.Move;
import util.ImList;
import views.HiddenHandView;
import views.VisibleHandView;

public class PlayerGameView {

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

}
