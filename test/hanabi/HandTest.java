package hanabi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import views.HandView;
import views.HiddenCardView;
import views.OmnescientCardView;
import views.OmnescientHandView;
import views.VisibleCardView;

public class HandTest {
    
    private static final Card B5_NO_INFO = new Card(Color.BLUE, 5);
    private static final Card B5_NOT_RED = new Card(Color.BLUE, 5);
    private static final Card B5_NOT_RED_NOT_2 = new Card(Color.BLUE, 5);
    static{ 
        B5_NOT_RED.learnColor(Color.RED);
        B5_NOT_RED_NOT_2.learnColor(Color.RED);
        B5_NOT_RED_NOT_2.learnNumber(2);
    }
    private static final Card R1_NO_INFO = new Card(Color.RED, 1);
    private static final Card R1_RED = new Card(Color.RED, 1);
    private static final Card R1_RED_NOT_2 = new Card(Color.RED, 1);
    static{
        R1_RED.learnColor(Color.RED);
        R1_RED_NOT_2.learnColor(Color.RED);
        R1_RED_NOT_2.learnNumber(2);
    }
    private static final Card R2_NO_INFO = new Card(Color.RED, 2);
    private static final Card R2_RED = new Card(Color.RED, 2);
    private static final Card R2_RED_2 = new Card(Color.RED, 2);
    static{
        R2_RED.learnColor(Color.RED);
        R2_RED_2.learnColor(Color.RED);
        R2_RED_2.learnNumber(2);
    }
    private static final Card G2_NO_INFO = new Card(Color.GREEN, 2);
    private static final Card G2_NOT_RED = new Card(Color.GREEN, 2);
    private static final Card G2_NOT_RED_2 = new Card(Color.GREEN, 2);
    static{
        G2_NOT_RED.learnColor(Color.RED);
        G2_NOT_RED_2.learnColor(Color.RED);
        G2_NOT_RED_2.learnNumber(2);
    }

    private static final VisibleCardView B5 = new VisibleCardView(
        Color.BLUE, 5
    );
    private static final VisibleCardView R1 = new VisibleCardView(
        Color.RED, 1
    );
    private static final VisibleCardView R2 = new VisibleCardView(
        Color.RED, 2
    );
    private static final VisibleCardView G2 = new VisibleCardView(
        Color.GREEN, 2
    );
    private static final HiddenCardView ALL_CARDS = new HiddenCardView(
        Color.ALL_COLORS, Card.ALL_NUMBERS
    );
    
    private static final OmnescientCardView B5_ALL_CARDS = 
        new OmnescientCardView(B5, ALL_CARDS);
    private static final OmnescientCardView R1_ALL_CARDS = 
        new OmnescientCardView(R1, ALL_CARDS);
    private static final OmnescientCardView R2_ALL_CARDS = 
        new OmnescientCardView(R2, ALL_CARDS);
    private static final OmnescientCardView G2_ALL_CARDS = 
        new OmnescientCardView(G2, ALL_CARDS);

    private static Hand genTestHand() {
        Card b5 = new Card(Color.BLUE, 5);
        Card r1 = new Card(Color.RED, 1);
        Card r2 = new Card(Color.RED, 2);
        Card g2 = new Card(Color.GREEN, 2);
        return new Hand(4, Arrays.asList(b5, r1, r2, g2));        
    }

    private static boolean cardContentsEqual(Card card1, Card card2) {
        return (
            card1.color().equals(card2.color()) && 
            card1.number() == card2.number() &&
            card1.possibleColors().equals(card2.possibleColors()) && 
            card1.possibleNumbers().equals(card2.possibleNumbers())
        );
    }

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test
    public void testGetters() {
        Hand hand = genTestHand();
        assertEquals(4, hand.size());
        assertEquals(false, hand.isFinished());
    }

    @Test
    public void testViews() {
        Hand hand = genTestHand();
        assertEquals(
            new HandView(
                true, 
                Arrays.asList(B5, R1, R2, G2)
            ), 
            hand.getView(true)
        );
        assertEquals(
            new HandView(
                false, 
                Arrays.asList(ALL_CARDS, ALL_CARDS, ALL_CARDS, ALL_CARDS)
            ), 
            hand.getView(false)
        );
        assertEquals(
            new OmnescientHandView(
                Arrays.asList(
                    B5_ALL_CARDS, 
                    R1_ALL_CARDS, 
                    R2_ALL_CARDS, 
                    G2_ALL_CARDS
                )
            ),
            hand.getOmnescientView()
        );
        
    }

    @Test
    public void testHint() {
        Hand hand = genTestHand();
        assertTrue(cardContentsEqual(B5_NO_INFO, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(1)));
        assertTrue(cardContentsEqual(R2_NO_INFO, hand.cards().get(2)));
        assertTrue(cardContentsEqual(G2_NO_INFO, hand.cards().get(3)));
        hand.hintColor(Color.RED);
        assertTrue(cardContentsEqual(B5_NOT_RED, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R1_RED, hand.cards().get(1)));
        assertTrue(cardContentsEqual(R2_RED, hand.cards().get(2)));
        assertTrue(cardContentsEqual(G2_NOT_RED, hand.cards().get(3)));
        hand.hintNumber(2);
        assertTrue(cardContentsEqual(B5_NOT_RED_NOT_2, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R1_RED_NOT_2, hand.cards().get(1)));
        assertTrue(cardContentsEqual(R2_RED_2, hand.cards().get(2)));
        assertTrue(cardContentsEqual(G2_NOT_RED_2, hand.cards().get(3)));
    }

    @Test
    public void testPlay() {
        Hand hand = genTestHand();
        assertTrue(cardContentsEqual(B5_NO_INFO, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(1)));
        assertTrue(cardContentsEqual(R2_NO_INFO, hand.cards().get(2)));
        assertTrue(cardContentsEqual(G2_NO_INFO, hand.cards().get(3)));
        hand.playOrDiscard(0, new Card(Color.RED, 1));
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R2_NO_INFO, hand.cards().get(1)));
        assertTrue(cardContentsEqual(G2_NO_INFO, hand.cards().get(2)));
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(3)));
        hand.playOrDiscardLast(1);
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(0)));
        assertTrue(cardContentsEqual(G2_NO_INFO, hand.cards().get(1)));
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(2)));
        assertTrue(hand.cards().size() == 3);
        assertTrue(hand.isFinished());
    }

    @Test
    public void testHintAndPlay() {
        Hand hand = genTestHand();
        assertTrue(cardContentsEqual(B5_NO_INFO, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(1)));
        assertTrue(cardContentsEqual(R2_NO_INFO, hand.cards().get(2)));
        assertTrue(cardContentsEqual(G2_NO_INFO, hand.cards().get(3)));
        hand.hintColor(Color.RED);
        assertTrue(cardContentsEqual(B5_NOT_RED, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R1_RED, hand.cards().get(1)));
        assertTrue(cardContentsEqual(R2_RED, hand.cards().get(2)));
        assertTrue(cardContentsEqual(G2_NOT_RED, hand.cards().get(3)));
        hand.playOrDiscard(0, new Card(Color.RED, 1));
        assertTrue(cardContentsEqual(R1_RED, hand.cards().get(0)));
        assertTrue(cardContentsEqual(R2_RED, hand.cards().get(1)));
        assertTrue(cardContentsEqual(G2_NOT_RED, hand.cards().get(2)));
        assertTrue(cardContentsEqual(R1_NO_INFO, hand.cards().get(3)));
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