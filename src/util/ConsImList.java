package util;

import org.json.JSONArray;
import org.json.JSONException;

public class ConsImList<T extends JSONifiable> implements ImList<T> {

    private final int length;
    private final ImList<T> start;
    private final T last;
    
    public ConsImList (ImList<T> start, T last) {
        this.length = start.length() + 1;
        this.start = start;
        this.last = last;
    }
    
    @Override
    public int length() {
        return length;
    }

    @Override
    public ImList<T> start() {
        return start;
    }

    @Override
    public T last() {
        return last;
    }

    @Override
    public ImList<T> extend(T next) {
        return new ConsImList<T>(this, next);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConsImList<?>)) { return false; }
        ConsImList<?> that = (ConsImList<?>) other;
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

	@Override
	public JSONArray jsonArrayify() throws JSONException {
		return start.jsonArrayify().put(last.jsonify());
	}

}
