package views;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import hanabi.Color;

public class ViewTestFramework {
    
    public static final Color BLUE = Color.BLUE;
    public static final  Color GREEN = Color.GREEN;
    public static final  Color RED = Color.RED;
    public static final  Color WHITE = Color.WHITE;
    public static final  Color YELLOW = Color.YELLOW;

    public static final Set<Color> ANY_COLOR = new TreeSet<>(
        Arrays.asList(BLUE, GREEN, RED, WHITE, YELLOW)
    );
    public static final Set<Color> NOT_RED = new TreeSet<>(
        Arrays.asList(BLUE, GREEN, WHITE, YELLOW)
    );
    public static final Set<Color> NOT_RED_GREEN = new TreeSet<>(
        Arrays.asList(BLUE, WHITE, YELLOW)
    );
    public static final Set<Color> ONLY_GREEN = new TreeSet<>(
        Arrays.asList(GREEN)
    );
    public static final Set<Color> ONLY_RED = new TreeSet<>(
        Arrays.asList(RED)
    );
    public static final Set<Color> ONLY_YELLOW = new TreeSet<>(
        Arrays.asList(YELLOW)
    );
    public static final Set<Integer> ANY_NUMBER = new TreeSet<>(
        Arrays.asList(1,2,3,4,5)
    );
    public static final Set<Integer> NOT_1 = new TreeSet<>(
        Arrays.asList(2,3,4,5)
    );
    public static final Set<Integer> NOT_2 = new TreeSet<>(
        Arrays.asList(1,3,4,5)
    );
    public static final Set<Integer> NOT_5 = new TreeSet<>(
        Arrays.asList(1,2,3,4)
    );
    public static final Set<Integer> NOT_12 = new TreeSet<>(
        Arrays.asList(3,4,5)
    );
    public static final Set<Integer> NOT_45 = new TreeSet<>(
        Arrays.asList(1,2,3)
    );
    public static final Set<Integer> ONLY_1 = new TreeSet<>(
        Arrays.asList(1)
    );
    public static final Set<Integer> ONLY_2 = new TreeSet<>(
        Arrays.asList(2)
    );
    
    public static HiddenCardView makeHiddenCard(
        Set<Color> colors, 
        Set<Integer> numbers, 
        boolean colorHinted, 
        boolean numberHinted
    ){
        return new HiddenCardView(colors, numbers, colorHinted, numberHinted);
    }
    public static VisibleCardView makeVisibleCard(
        Color color, 
        Integer number, 
        Set<Color> colors, 
        Set<Integer> numbers, 
        boolean colorHinted, 
        boolean numberHinted
    ){
        return new VisibleCardView(
            color, 
            number, 
            new HiddenCardView(
                colors, 
                numbers, 
                colorHinted, 
                numberHinted
            )
        );
    }

}
