package move;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hanabi.Color;

public class MoveTest {

    private static final Move COLOR_HINT = new ColorHint(0, 3, Color.RED);
    private static final Move NUMBER_HINT = new NumberHint(1, 2, 2);
    private static final Move PLAY = new Play(2, 2, false);
    private static final Move DISCARD = new Discard(3, 2, true);

    @Test
    public void testMove() {
        assertEquals("0 HINT 3 R", COLOR_HINT.toString());
        assertEquals("1 HINT 2 2", NUMBER_HINT.toString());
        assertEquals("2 PLAY 2 incorrect", PLAY.toString());
        assertEquals("3 DISCARD 2 safe", DISCARD.toString());
    }

    @Test
    public void testMoveHistory() {
        MoveHistory history = MoveHistory.empty().extend(COLOR_HINT).extend(
            NUMBER_HINT).extend(PLAY).extend(DISCARD);
        assertEquals(
            MoveHistory.empty(), 
            history.start().start().start().start()
        );
        assertEquals(
            COLOR_HINT, 
            history.start().start().start().last()
        );
        assertEquals(
            NUMBER_HINT, 
            history.start().start().last()
        );
        assertEquals(
            PLAY, 
            history.start().last()
        );
        assertEquals(
            DISCARD, 
            history.last()
        );
        assertEquals(
            "0 HINT 3 R\n" +
            "1 HINT 2 2\n" +
            "2 PLAY 2 incorrect\n" +
            "3 DISCARD 2 safe\n",
            history.toString()
        );
    }

}
