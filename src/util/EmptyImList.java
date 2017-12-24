package util;

public class EmptyImList<T> implements ImList<T> {

    @Override
    public int length() {
        return 0;
    }

    @Override
    public ImList<T> start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T last() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImList<T> extend(T next) {
        return new ConsImList<T>(this, next);
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof EmptyImList<?>);
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
