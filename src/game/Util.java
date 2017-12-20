package game;

import java.util.List;
import java.util.Map;

import hanabi.Card;
import hanabi.Color;
import hanabi.Hand;
import hanabi.HandView;
import move.Move;

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
    public static String discardsRep(Map<Color, Map<Integer, Integer> > discards) {
        String discardsRep = "Discards:\n";
        for (Color color : Color.ALL_COLORS) {
            for (Integer number : discards.get(color).keySet()) {
                discardsRep += "  " + color.toString() + number + ": " + discards.get(color).get(number) + "\n";                 
            }
        }
        return discardsRep;
    }

    public static String handsRep(int nPlayers, List<Hand> hands) {
        String handsRep = "Hands:\n";
        for (int i=0; i<nPlayers; i++){
            handsRep += "  Player " + i + ": " + hands.get(i).toString() + "\n";
        }
        return handsRep;
    }
    public static String handsViewRep(int nPlayers, List<HandView> hands) {
        String handViewsRep = "Hand Views:\n";
        for (int i=0; i<nPlayers; i++){
            handViewsRep += "  Player " + i + ": " + hands.get(i).toString() + "\n";
        }
        return handViewsRep;
    }

    public static String lastMoveRep(List<Move> history) {
        String lastMoveRep = "";
        if (history.size() > 0) {
            lastMoveRep = history.get(history.size() - 1).verboseRep() + "\n";
        }
        return lastMoveRep;
    }
    public static String historyRep(List<Move> history) {
        String historyRep = "History:\n";
        for (Move move : history) {
            historyRep += "  " + move.toString() + "\n";
        }
        return historyRep;
    }
    private static final int PRINT_CARDS_PER_ROW = 10;
    public static String deckRep(List<Card> deck) {
        String deckRep = "Remaining Cards:\n";
        int thisRowCards = 0;
        for (Card card : deck) {
            deckRep += card.shortRep()+ " ";
            thisRowCards ++;
            if (thisRowCards == PRINT_CARDS_PER_ROW) {
                deckRep += "\n";
                thisRowCards = 0;
            }
        }
        deckRep += "\n";
        return deckRep;
    }
}
