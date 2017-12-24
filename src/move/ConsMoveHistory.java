package move;

public class ConsMoveHistory implements MoveHistory {

    private final int length;
    private final MoveHistory start;
    private final Move last;

    public ConsMoveHistory(MoveHistory start, Move last) {
        this.length = start.length() + 1;
        this.start = start;
        this.last = last;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public MoveHistory start() {
        return start;
    }

    @Override
    public Move last() {
        return last;
    }
    
    @Override
    public MoveHistory extend(Move next) {
        return new ConsMoveHistory(this, next);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConsMoveHistory)) { return false; }
        ConsMoveHistory that = (ConsMoveHistory) other;
        return (this.start.equals(that.start) && this.last.equals(that.last));
    }

    @Override
    public int hashCode(){
        return 13*start.hashCode() + last.hashCode();
    }

    @Override
    public String toString(){
        return start.toString() + last.toString() + "\n"; 
    }

}
