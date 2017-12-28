package move;

import util.JSONifiable;

public interface Move extends JSONifiable {
    
    public String verboseRep();
    
    @Override
    public String toString();
}
