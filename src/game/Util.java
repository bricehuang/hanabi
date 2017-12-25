package game;

import java.util.List;
import java.util.Map;

import hanabi.Color;
import hanabi.DeckCard;
import javafx.util.Pair;
import move.Move;
import util.ImList;
import views.VisibleHandView;

public class Util {

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

    public static String discardsRep(Map<Pair<Color, Integer>, Integer> discards) {
        String discardsRep = "Discards:\n";
        for (Pair<Color, Integer> colorAndNumber : discards.keySet()) {
            Color color = colorAndNumber.getKey();
            int number = colorAndNumber.getValue();
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
    public static String deckRep(ImList<DeckCard> cards){
        ImList<DeckCard> deckPointer = cards;
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
        return repr;
    }

}
