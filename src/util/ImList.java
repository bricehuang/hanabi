package util;

import java.util.List;

public interface ImList<T> {

    public int length();
    public ImList<T> start();
    public T last();
    public ImList<T> extend(T next);

    @Override
    public boolean equals(Object other);
    @Override
    public int hashCode();
    @Override 
    public String toString();

    public static <T> ImList<T> empty() {
        return new EmptyImList<T>();
    }
    
    public static <T> ImList<T> convert(List<T> items) {
        int len = items.size();
        if (len == 0) {
            return new EmptyImList<T>();
        } else {
            return convert(items.subList(0, len-1)).extend(items.get(len-1));
        }
    }

}
