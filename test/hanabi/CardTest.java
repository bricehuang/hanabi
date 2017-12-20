package hanabi;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class CardTest {

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    private static final Set<Color> COLORS_EXCEPT_BLUE = new TreeSet<>(
        Arrays.asList(Color.GREEN, Color.RED, Color.WHITE, Color.YELLOW)
    );
    private static final Set<Color> COLORS_ONLY_GREEN = new TreeSet<>(
        Arrays.asList(Color.GREEN)
    );

    private static final Set<Integer> NUMBERS_EXCEPT_2 = new TreeSet<>(
        Arrays.asList(1,3,4,5)
    );
    private static final Set<Integer> NUMBERS_EXCEPT_24 = new TreeSet<>(
        Arrays.asList(1,3,5)
    );
    private static final Set<Integer> NUMBERS_ONLY_3 = new TreeSet<>(
        Arrays.asList(3)
    );

    @Test
    public void testGetters() {
        Card card = new Card(Color.RED, 5); // GA-BOH-MEEM
        assertEquals(Color.RED, card.color());
        assertEquals(5, card.number());
        assertEquals(Color.ALL_COLORS, card.possibleColors());
        assertEquals(Card.ALL_NUMBERS, card.possibleNumbers());
    }

    @Test
    public void testLearn() {
        Card card = new Card(Color.GREEN, 3);
        assertEquals(Color.ALL_COLORS, card.possibleColors());
        assertEquals(Card.ALL_NUMBERS, card.possibleNumbers());

        card.learnColor(Color.BLUE);
        assertEquals(COLORS_EXCEPT_BLUE, card.possibleColors());
        assertEquals(Card.ALL_NUMBERS, card.possibleNumbers());

        card.learnNumber(2);
        assertEquals(COLORS_EXCEPT_BLUE, card.possibleColors());
        assertEquals(NUMBERS_EXCEPT_2, card.possibleNumbers());

        card.learnColor(Color.GREEN);
        assertEquals(COLORS_ONLY_GREEN, card.possibleColors());
        assertEquals(NUMBERS_EXCEPT_2, card.possibleNumbers());

        card.learnNumber(4);
        assertEquals(COLORS_ONLY_GREEN, card.possibleColors());
        assertEquals(NUMBERS_EXCEPT_24, card.possibleNumbers());

        card.learnNumber(3);
        assertEquals(COLORS_ONLY_GREEN, card.possibleColors());
        assertEquals(NUMBERS_ONLY_3, card.possibleNumbers());
    }

    @Test
    public void testToString() {
        Card card = new Card(Color.BLUE, 1);
        assertEquals("B1 (BGRWY, 12345)", card.toString());
        card.learnColor(Color.BLUE);
        assertEquals("B1 (B    , 12345)", card.toString());
        card.learnNumber(4);
        assertEquals("B1 (B    , 1235 )", card.toString());
    }

}
