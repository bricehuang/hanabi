package util;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public interface ImList<T extends JSONifiable> {

    public int length();
    public ImList<T> start();
    public T last();
    public ImList<T> extend(T next);

    public JSONArray jsonArrayify() throws JSONException;

    @Override
    public boolean equals(Object other);
    @Override
    public int hashCode();
    @Override 
    public String toString();

    public static <T extends JSONifiable> ImList<T> empty() {
        return new EmptyImList<T>();
    }

    public static <T extends JSONifiable> ImList<T> convert(List<T> items) {
        int len = items.size();
        if (len == 0) {
            return new EmptyImList<T>();
        } else {
            return convert(items.subList(0, len-1)).extend(items.get(len-1));
        }
    }
    
}
