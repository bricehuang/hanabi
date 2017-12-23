package views;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class HandViewTest extends ViewTestFramework {
    
    private static final HiddenCardView NOT_RED_NOT_5 = makeHiddenCard(NOT_RED, NOT_5);
    private static final HiddenCardView ANY_COLOR_ONLY_1 = makeHiddenCard(ANY_COLOR, ONLY_1);
    private static final HiddenCardView ONLY_YELLOW_ANY_NUMBER = makeHiddenCard(ONLY_YELLOW, ANY_NUMBER);

    private static final VisibleCardView G2_NOT_RED_NOT_5 = makeVisibleCard(GREEN, 2, NOT_RED, NOT_5);
    private static final VisibleCardView W3_NOT_RED_NOT_5 = makeVisibleCard(WHITE, 3, NOT_RED, NOT_5);
    private static final VisibleCardView G1_ANY_COLOR_ONLY_1 = makeVisibleCard(GREEN, 1, ANY_COLOR, ONLY_1);
    private static final VisibleCardView Y2_ONLY_YELLOW_ANY_NUMBER = makeVisibleCard(YELLOW, 2, ONLY_YELLOW, ANY_NUMBER);
    
    private static final HiddenHandView HIDDEN_HAND = new HiddenHandView(
        Arrays.asList(NOT_RED_NOT_5, NOT_RED_NOT_5, ANY_COLOR_ONLY_1, ONLY_YELLOW_ANY_NUMBER)
    );
    private static final VisibleHandView VISIBLE_HAND = new VisibleHandView(
        Arrays.asList(G2_NOT_RED_NOT_5, W3_NOT_RED_NOT_5, G1_ANY_COLOR_ONLY_1, Y2_ONLY_YELLOW_ANY_NUMBER)
    );

    
    @Test
    public void testHiddenHandView() {
        assertEquals(NOT_RED_NOT_5, HIDDEN_HAND.cardViews.get(0));
        assertEquals(NOT_RED_NOT_5, HIDDEN_HAND.cardViews.get(1));
        assertEquals(ANY_COLOR_ONLY_1, HIDDEN_HAND.cardViews.get(2));
        assertEquals(ONLY_YELLOW_ANY_NUMBER, HIDDEN_HAND.cardViews.get(3));
        assertEquals(
            "[?? (BGWY , 1234 ), " +
             "?? (BGWY , 1234 ), " +
             "?? (BGRWY, 1    ), " +
             "?? (Y    , 12345), ]",
             HIDDEN_HAND.toString()
        );
    }

    @Test
    public void testVisibleHandView() {
        assertEquals(G2_NOT_RED_NOT_5, VISIBLE_HAND.cardViews.get(0));
        assertEquals(W3_NOT_RED_NOT_5, VISIBLE_HAND.cardViews.get(1));
        assertEquals(G1_ANY_COLOR_ONLY_1, VISIBLE_HAND.cardViews.get(2));
        assertEquals(Y2_ONLY_YELLOW_ANY_NUMBER, VISIBLE_HAND.cardViews.get(3));
        assertEquals(
            "[G2 (BGWY , 1234 ), " +
             "W3 (BGWY , 1234 ), " +
             "G1 (BGRWY, 1    ), " +
             "Y2 (Y    , 12345), ]",
             VISIBLE_HAND.toString()
        );
    }    

}
