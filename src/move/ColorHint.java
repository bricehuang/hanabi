package move;

import hanabi.Color;

public class ColorHint implements Move {

    public final int actor;

    public final int hintee;
    public final Color color;

    public ColorHint(int actor, int hintee, Color color) {
        assert actor != hintee;
        this.actor = actor;
        this.hintee = hintee;
        this.color = color;
    }

    @Override
    public String verboseRep() {
        return "Player " + actor + " hinted Player " + hintee + 
                "'s " + color.verboseRep() + "s.";
    }

    @Override
    public String toString() {
        return "" + actor + " HINT " + hintee + " " + color.toString();
    }

}
