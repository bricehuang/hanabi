package move;

public class Resignation implements Move {

    public final int actor;

    public Resignation(int actor) {
        this.actor = actor;
    }

    @Override
    public String verboseRep() {
        return "Player " + actor + " resigned.";
    }

    @Override
    public String toString() {
        return "" + actor + " RESIGN";
    }

}
