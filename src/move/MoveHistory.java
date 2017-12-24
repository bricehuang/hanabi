package move;

public interface MoveHistory {

    public int length();
    public MoveHistory start();
    public Move last();
    
    public static MoveHistory empty() {
        return new EmptyMoveHistory();
    }

    public MoveHistory extend(Move next);

    @Override
    public boolean equals(Object other);

    @Override
    public int hashCode();

    @Override
    public String toString();

}
