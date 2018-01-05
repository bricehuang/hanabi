package hanabi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import views.ViewTestFramework;

public class CardSpecTest extends ViewTestFramework {

    private static final CardSpec R5 = new CardSpec(RED, 5);
    
    @Test
    public void testCreateCard() {
        Card r5Card = R5.createCard();
        assertEquals(
            makeVisibleCard(RED, 5, ANY_COLOR, ANY_NUMBER, false, false),
            r5Card.visibleView()
        );
    }
    
    @Test
    public void testToString() {
        assertEquals("R5", R5.toString());
    }

}
