package hanabi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import javafx.util.Pair;
import util.ImList;
import views.ViewTestFramework;

public class StateTest extends ViewTestFramework {

    private static final DeckCard R5 = new DeckCard(RED, 5);
    private static final DeckCard G2 = new DeckCard(GREEN, 2);

    @Test
    public void testDeck() {
        Deck deck = new Deck(Arrays.asList(R5, G2));
        assertEquals(2, deck.size());
        assertEquals("Deck:\n  G2 R5\n", deck.toString());
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

    @Test
    public void testPlayState() {
        PlayState playState = new PlayState();
        assertFalse(playState.isPlayable(RED, 2));
        assertTrue(playState.isPlayable(RED, 1));
        assertFalse(playState.hasBeenPlayed(RED, 1));
        assertFalse(playState.hasBeenPlayed(RED, 2));
        Map<Color, Integer> view1 = playState.getView();
        Map<Color, Integer> expectedView1 = new TreeMap<>();
        expectedView1.put(BLUE, 0);
        expectedView1.put(GREEN, 0);
        expectedView1.put(RED, 0);
        expectedView1.put(WHITE, 0);
        expectedView1.put(YELLOW, 0);
        assertEquals(expectedView1, view1);
        assertEquals(
            "Plays:\n" +
            "  Blue: 0\n" +
            "  Green: 0\n" +
            "  Red: 0\n" +
            "  White: 0\n" +
            "  Yellow: 0\n",
            playState.toString()
        );

        playState.playCard(RED, 1);
        assertTrue(playState.isPlayable(RED, 2));
        assertTrue(playState.hasBeenPlayed(RED, 1));
        assertFalse(playState.hasBeenPlayed(RED, 2));
        Map<Color, Integer> view2 = playState.getView();
        Map<Color, Integer> expectedView2 = new TreeMap<>();
        expectedView2.put(BLUE, 0);
        expectedView2.put(GREEN, 0);
        expectedView2.put(RED, 1);
        expectedView2.put(WHITE, 0);
        expectedView2.put(YELLOW, 0);
        assertEquals(expectedView2, view2);
        assertEquals(
            "Plays:\n" +
            "  Blue: 0\n" +
            "  Green: 0\n" +
            "  Red: 1\n" +
            "  White: 0\n" +
            "  Yellow: 0\n",
            playState.toString()
        );

        // views should be immutable snapshots
        assertFalse(view1.equals(view2));
    }

    @Test
    public void testDiscardState() {
        DiscardState discardState = new DiscardState();
        Map<Pair<Color, Integer>, Integer> view1 = discardState.getView();
        Map<Pair<Color, Integer>, Integer> expectedView1 = new TreeMap<>(DiscardState.LEX_COMPARATOR);
        assertEquals(view1, expectedView1);
        assertEquals("Discards:\n", discardState.toString());
        
        discardState.discardCard(RED, 5); // GAAH-BOOH-MEEM
        Map<Pair<Color, Integer>, Integer> view2 = discardState.getView();
        Map<Pair<Color, Integer>, Integer> expectedView2 = new TreeMap<>(DiscardState.LEX_COMPARATOR);
        expectedView2.put(new Pair<Color, Integer>(RED, 5), 1);
        assertEquals(view2, expectedView2);
        assertEquals("Discards:\n  R5: 1\n", discardState.toString());
        
        // views should be immutable snapshots
        assertFalse(view1.equals(view2));
    }

}
