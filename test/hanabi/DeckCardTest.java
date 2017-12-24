package hanabi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import views.ViewTestFramework;

public class DeckCardTest extends ViewTestFramework {

    private static final DeckCard R5 = new DeckCard(RED, 5);
    
    @Test
    public void testCreateCard() {
        Card r5Card = R5.createCard();
        assertEquals(
            makeVisibleCard(RED, 5, ANY_COLOR, ANY_NUMBER),
            r5Card.visibleView()
        );
    }
    
    @Test
    public void testToString() {
        assertEquals("R5", R5.toString());
    }

}
