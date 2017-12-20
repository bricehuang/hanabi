package hanabi;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public enum Color {
    BLUE, GREEN, RED, WHITE, YELLOW;

    public static final int NUM_COLORS = 5;
    public static final Set<Color> ALL_COLORS = Collections.unmodifiableSet(
        new TreeSet<>(Arrays.asList(BLUE, GREEN, RED, WHITE, YELLOW))
    );
    
    public String verboseRep() {
        if (this.equals(BLUE)) { return "Blue"; }
        else if (this.equals(GREEN)) { return "Green"; }
        else if (this.equals(RED)) { return "Red"; }
        else if (this.equals(WHITE)) { return "White"; }
        else if (this.equals(YELLOW)) { return "Yellow"; }
        else { throw new RuntimeException("Should not get here"); }        
    }

    @Override
    public String toString(){
        if (this.equals(BLUE)) { return "B"; }
        else if (this.equals(GREEN)) { return "G"; }
        else if (this.equals(RED)) { return "R"; }
        else if (this.equals(WHITE)) { return "W"; }
        else if (this.equals(YELLOW)) { return "Y"; }
        else { throw new RuntimeException("Should not get here"); }
    }

}
