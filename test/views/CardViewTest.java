package views;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CardViewTest extends ViewTestFramework {

    @Test
    public void testHiddenCardView() {
        HiddenCardView notRedNot5 = makeHiddenCard(NOT_RED, NOT_5);
        assertEquals(notRedNot5.colors(), NOT_RED);
        assertEquals(notRedNot5.numbers(), NOT_5);
        assertEquals(notRedNot5.toString(), "?? (BGWY , 1234 )");
    }

    @Test
    public void testVisibleCardView() {
        VisibleCardView g2NotRedNot5 = makeVisibleCard(GREEN, 2, NOT_RED, NOT_5);
        assertEquals(GREEN, g2NotRedNot5.color());
        assertEquals( (Integer) 2, g2NotRedNot5.number());
        assertEquals(NOT_RED, g2NotRedNot5.colors());
        assertEquals(NOT_5, g2NotRedNot5.numbers());
        assertEquals("G2 (BGWY , 1234 )", g2NotRedNot5.toString());
    }

}
