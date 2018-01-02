package game;

import java.util.List;
import java.util.Map;

import hanabi.CardSpec;
import hanabi.Color;
import move.Move;
import util.ImList;
import views.HiddenHandView;
import views.VisibleHandView;

public class Util {

    public static String playerIDRep(int playerID) {
        return "You are: Player " + playerID + ".\n";
    }
    public static String toMoveRep(int playerToMove) {
        return "Player " + playerToMove + " to move.\n";
    }
    public static String livesRep(int lives) {
        return "Lives: " + lives + "\n";
    }
    public static String hintsRep(int hints) {
        return "Hints: " + hints + "\n";
    }

    public static String playsRep(Map<Color, Integer> plays) {
        String playsRep = "Plays:\n";
        for (Color color : Color.ALL_COLORS) {
            playsRep += "  " + color.verboseRep() + ": " + plays.get(color) + "\n"; 
        }
        return playsRep;
    }

    public static String discardsRep(Map<CardSpec, Integer> discards) {
        String discardsRep = "Discards:\n";
        for (CardSpec colorAndNumber : discards.keySet()) {
            Color color = colorAndNumber.color;
            int number = colorAndNumber.number;
            discardsRep += "  " + color + number + ": " + discards.get(colorAndNumber) + "\n";                 
        }
        return discardsRep;
    }

    public static String handsRep(List<VisibleHandView> hands) {
        String handsRep = "Hands:\n";
        for (int i=0; i<hands.size(); i++){
            handsRep += "  Player " + i + ": " + hands.get(i).toString() + "\n";
        }
        return handsRep;
    }

    private static String parseHandRow(int player, VisibleHandView hand) {
        return "  Player " + player + ": " + hand.toString() + "\n";
    }
    public static String handsRep(
        int playerID, 
        Map<Integer, VisibleHandView> otherHands, 
        HiddenHandView myHand
    ) {
        String handsRep = "Hands:\n";
        for (int i=0; i<playerID; i++) {
            handsRep += parseHandRow(i, otherHands.get(i));
        }
        handsRep += "  Player " + playerID + ": " + myHand.toString() + "\n";
        for (int i=playerID+1; i<otherHands.size()+1; i++) {
            handsRep += parseHandRow(i, otherHands.get(i));
        }
        return handsRep;
    }

    public static String lastMoveRep(ImList<Move> history) {
        return history.length() > 0 ? history.last().verboseRep()+"\n" : "";
    }

    private static String parseHistory(ImList<Move> history) {
        return history.length() > 0 
            ? parseHistory(history.start()) + "  " + history.last().toString() + "\n" 
            : "";
    }
    public static String historyRep(ImList<Move> history) {
        return "History:\n" + parseHistory(history); 
    }

    private static final int PRINT_CARDS_PER_ROW = 10;
    public static String deckRep(ImList<CardSpec> cards){
        ImList<CardSpec> deckPointer = cards;
        String repr = "Deck:";
        int cardsPrinted = 0;
        while (deckPointer.length() > 0) {
            if (cardsPrinted % PRINT_CARDS_PER_ROW == 0) {
                repr += "\n  ";
            } else {
                repr += " ";
            }
            repr += deckPointer.last().toString();
            cardsPrinted ++;
            deckPointer = deckPointer.start();
        }
        repr += "\n";
        return repr;
    }

    public static String deckLengthRep(int length) {
        return "There are " + length + " cards left in the deck.\n";
    }
    
    public static int score(Map<Color, Integer> plays) {
        int score = 0;
        for (Color color : Color.ALL_COLORS) {
            score += plays.get(color);
        }
        return score;
    }
    
    public static String isOverRep(boolean isOver) {
        if (isOver) {
            return "Game over.";
        } else{
            return "";
        }
    }

}
