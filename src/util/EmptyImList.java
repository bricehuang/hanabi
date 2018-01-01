package util;

import org.json.JSONArray;
import org.json.JSONException;

public class EmptyImList<T extends JSONifiable> implements ImList<T> {

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

    @Override
    public JSONArray jsonArrayify() throws JSONException {
        return new JSONArray();
    }

}
