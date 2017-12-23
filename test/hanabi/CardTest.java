package hanabi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import views.ViewTestFramework;

public class CardTest extends ViewTestFramework {

    @Test
    public void testViews() {
        Card card = new Card(RED, 5); // GA-BOH-MEEM
        assertEquals(
            makeHiddenCard(ANY_COLOR, ANY_NUMBER), 
            card.hiddenView()
        );
        assertEquals(
            makeVisibleCard(RED, 5, ANY_COLOR, ANY_NUMBER), 
            card.visibleView()
        );
    }

    @Test
    public void testLearn() {
        Card card = new Card(GREEN, 2);
        assertEquals(
            makeHiddenCard(ANY_COLOR, ANY_NUMBER), 
            card.hiddenView()
        );
        assertEquals(
            makeVisibleCard(GREEN, 2, ANY_COLOR, ANY_NUMBER), 
            card.visibleView()
        );

        card.learnNumber(5);
        assertEquals(
            makeHiddenCard(ANY_COLOR, NOT_5), 
            card.hiddenView()
        );
        assertEquals(
            makeVisibleCard(GREEN, 2, ANY_COLOR, NOT_5), 
            card.visibleView()
        );

        card.learnColor(RED);
        assertEquals(
            makeHiddenCard(NOT_RED, NOT_5), 
            card.hiddenView()
        );
        assertEquals(
            makeVisibleCard(GREEN, 2, NOT_RED, NOT_5), 
            card.visibleView()
        );

        card.learnNumber(4);
        assertEquals(
            makeHiddenCard(NOT_RED, NOT_45), 
            card.hiddenView()
        );
        assertEquals(
            makeVisibleCard(GREEN, 2, NOT_RED, NOT_45), 
            card.visibleView()
        );

        card.learnColor(GREEN);
        assertEquals(
            makeHiddenCard(ONLY_GREEN, NOT_45), 
            card.hiddenView()
        );
        assertEquals(
            makeVisibleCard(GREEN, 2, ONLY_GREEN, NOT_45), 
            card.visibleView()
        );

        card.learnNumber(2);
        assertEquals(
            makeHiddenCard(ONLY_GREEN, ONLY_2), 
            card.hiddenView()
        );
        assertEquals(
            makeVisibleCard(GREEN, 2, ONLY_GREEN, ONLY_2), 
            card.visibleView()
        );
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
