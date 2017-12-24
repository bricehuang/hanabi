package move;

public class EmptyMoveHistory implements MoveHistory {
    
    public EmptyMoveHistory() {
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public MoveHistory start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Move last() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MoveHistory extend(Move next) {
        return new ConsMoveHistory(this, next);
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof EmptyMoveHistory);
    }

    @Override
    public int hashCode() {
        return 42;
    }

    @Override
    public String toString() {
        return "";
    }

}
