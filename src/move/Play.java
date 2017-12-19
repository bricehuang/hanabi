package move;

public class Play implements Move {

    public final int actor;

    public final int position;
    public final boolean isCorrect;
    
    public Play(int actor, int position, boolean isCorrect) {
        assert 1 <= actor && actor <= 5;
        this.actor = actor;
        this.position = position;
        this.isCorrect = isCorrect;
    }
    
    @Override
    public String verboseRep() {
        String correctness = this.isCorrect ? "correct" : "incorrect";  
        return "Player " + actor + " played his card from position " + position + 
            ". It was " + correctness + ".";
    }
    
    @Override
    public String toString() {
        String correctness = this.isCorrect ? "correct" : "incorrect";
        return "" + actor + " PLAY " + position + " " + correctness; 
    }
}
