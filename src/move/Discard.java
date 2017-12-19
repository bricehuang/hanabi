package move;

public class Discard implements Move {

    public final int actor;

    public final int position;
    public final boolean isSafe;

    public Discard(int actor, int position, boolean isSafe) {
        assert 1 <= actor && actor <= 5;
        this.actor = actor;
        this.position = position;
        this.isSafe = isSafe;
    }

    @Override
    public String verboseRep() {
        String safety = this.isSafe ? "safe" : "unsafe";
        return "Player " + actor + " discarded his card from position " + position +  
            ". It was " + safety + ".";
    }

    @Override
    public String toString() {
        String safety = this.isSafe ? "safe" : "unsafe";
        return "" + actor + " DISCARD " + position + " " + safety;
    }

}
