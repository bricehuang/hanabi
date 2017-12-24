package hanabi;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import util.ImList;
import views.ViewTestFramework;

public class DeckTest extends ViewTestFramework {

    private static final DeckCard R5 = new DeckCard(RED, 5);
    private static final DeckCard G2 = new DeckCard(GREEN, 2);
    
    @Test
    public void testDeck() {
        Deck deck = new Deck(Arrays.asList(R5, G2));
        assertEquals(2, deck.size());
        ImList<DeckCard> view1 = deck.getView();
        assertEquals(
            makeVisibleCard(GREEN, 2, ANY_COLOR, ANY_NUMBER),
            deck.draw().visibleView()
        );
        assertEquals(1, deck.size());
        ImList<DeckCard> view2 = deck.getView();
        assertEquals(
            makeVisibleCard(RED, 5, ANY_COLOR, ANY_NUMBER),
            deck.draw().visibleView()
        );
        assertEquals(0, deck.size());
        ImList<DeckCard> view3 = deck.getView();
        assertEquals(G2, view1.last());
        assertEquals(view2, view1.start());
        assertEquals(R5, view2.last());
        assertEquals(view3, view2.start());
        assertEquals(ImList.<DeckCard>empty(), view3);
    }
}
