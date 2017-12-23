package hanabi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import views.HiddenCardView;
import views.HiddenHandView;
import views.ViewTestFramework;
import views.VisibleHandView;

public class HandTest extends ViewTestFramework {

    private static Hand genTestHand() {
        Card b5 = new Card(BLUE, 5);
        Card r1 = new Card(RED, 1);
        Card r2 = new Card(RED, 2);
        Card g2 = new Card(GREEN, 2);
        return new Hand(4, Arrays.asList(b5, r1, r2, g2));        
    }

    @Test
    public void testViews() {
        Hand hand = genTestHand();
        assertEquals(
            new HiddenHandView(Arrays.asList(
                HiddenCardView.NO_INFO,
                HiddenCardView.NO_INFO,
                HiddenCardView.NO_INFO,
                HiddenCardView.NO_INFO
            )),
            hand.hiddenView()
        );
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(RED, 1, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(RED, 2, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(GREEN, 2, ANY_COLOR, ANY_NUMBER)
            )), 
            hand.visibleView()
        );
    }

    @Test
    public void testHint() {
        Hand hand = genTestHand();
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(RED, 1, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(RED, 2, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(GREEN, 2, ANY_COLOR, ANY_NUMBER)
            )), 
            hand.visibleView()
        );
        hand.hintColor(Color.RED);
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, NOT_RED, ANY_NUMBER),
                makeVisibleCard(RED, 1, ONLY_RED, ANY_NUMBER),
                makeVisibleCard(RED, 2, ONLY_RED, ANY_NUMBER),
                makeVisibleCard(GREEN, 2, NOT_RED, ANY_NUMBER)
            )), 
            hand.visibleView()
        );
        hand.hintNumber(2);
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, NOT_RED, NOT_2),
                makeVisibleCard(RED, 1, ONLY_RED, NOT_2),
                makeVisibleCard(RED, 2, ONLY_RED, ONLY_2),
                makeVisibleCard(GREEN, 2, NOT_RED, ONLY_2)
            )), 
            hand.visibleView()
        );
        hand.hintColor(GREEN);
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, NOT_RED_GREEN, NOT_2),
                makeVisibleCard(RED, 1, ONLY_RED, NOT_2),
                makeVisibleCard(RED, 2, ONLY_RED, ONLY_2),
                makeVisibleCard(GREEN, 2, ONLY_GREEN, ONLY_2)
            )), 
            hand.visibleView()
        );
    }

    @Test
    public void testPlay() {
        Hand hand = genTestHand();
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(RED, 1, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(RED, 2, ANY_COLOR, ANY_NUMBER),
                makeVisibleCard(GREEN, 2, ANY_COLOR, ANY_NUMBER)
            )), 
            hand.visibleView()
        );
        assertFalse(hand.isFinished());
        hand.hintNumber(1);
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, ANY_COLOR, NOT_1),
                makeVisibleCard(RED, 1, ANY_COLOR, ONLY_1),
                makeVisibleCard(RED, 2, ANY_COLOR, NOT_1),
                makeVisibleCard(GREEN, 2, ANY_COLOR, NOT_1)
            )), 
            hand.visibleView()
        );
        assertFalse(hand.isFinished());
        hand.playOrDiscard(1, new Card(YELLOW, 3));
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, ANY_COLOR, NOT_1),
                makeVisibleCard(RED, 2, ANY_COLOR, NOT_1),
                makeVisibleCard(GREEN, 2, ANY_COLOR, NOT_1),
                makeVisibleCard(YELLOW, 3, ANY_COLOR, ANY_NUMBER)
            )), 
            hand.visibleView()
        );
        assertFalse(hand.isFinished());
        hand.hintNumber(2);
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(BLUE, 5, ANY_COLOR, NOT_12),
                makeVisibleCard(RED, 2, ANY_COLOR, ONLY_2),
                makeVisibleCard(GREEN, 2, ANY_COLOR, ONLY_2),
                makeVisibleCard(YELLOW, 3, ANY_COLOR, NOT_2)
            )), 
            hand.visibleView()
        );
        assertFalse(hand.isFinished());
        hand.playOrDiscardLast(0);
        assertEquals(
            new VisibleHandView(Arrays.asList( 
                makeVisibleCard(RED, 2, ANY_COLOR, ONLY_2),
                makeVisibleCard(GREEN, 2, ANY_COLOR, ONLY_2),
                makeVisibleCard(YELLOW, 3, ANY_COLOR, NOT_2)
            )), 
            hand.visibleView()
        );
        assertTrue(hand.isFinished());
    }

    @Test
    public void testToString() {
        Hand hand = genTestHand();
        assertEquals(
            "[B5 (BGRWY, 12345), "+
             "R1 (BGRWY, 12345), "+
             "R2 (BGRWY, 12345), "+
             "G2 (BGRWY, 12345), ]", 
             hand.toString()
        );
        hand.hintColor(Color.RED);
        assertEquals(
            "[B5 (BGWY , 12345), "+
             "R1 (R    , 12345), "+
             "R2 (R    , 12345), "+
             "G2 (BGWY , 12345), ]", 
             hand.toString()
        );
        hand.hintNumber(2);
        assertEquals(
            "[B5 (BGWY , 1345 ), "+
             "R1 (R    , 1345 ), "+
             "R2 (R    , 2    ), "+
             "G2 (BGWY , 2    ), ]", 
             hand.toString()
        );
    }

}