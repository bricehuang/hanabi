package move;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import hanabi.CardSpec;
import hanabi.Color;
import util.ImList;

public class MoveTest {

    private static final Move COLOR_HINT = new ColorHint(0, 3, Color.RED, Arrays.asList(1,2,3));
    private static final Move NUMBER_HINT = new NumberHint(1, 2, 2, Arrays.asList(2,3,4));
    private static final Move PLAY = new Play(2, 2, false, new CardSpec(Color.RED, 2));
    private static final Move DISCARD = new Discard(3, 2, true, new CardSpec(Color.RED, 5));

    @Test
    public void testMove() {
        assertEquals("0 HINT 3 R", COLOR_HINT.toString());
        assertEquals("1 HINT 2 2", NUMBER_HINT.toString());
        assertEquals("2 PLAY 2 incorrect", PLAY.toString());
        assertEquals("3 DISCARD 2 safe", DISCARD.toString());
    }

    @Test
    public void testMoveHistory() {
        ImList<Move> history = ImList.<Move>empty().extend(
            COLOR_HINT).extend(NUMBER_HINT).extend(PLAY).extend(DISCARD);
        assertEquals(
            ImList.<Move>empty(), 
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
        assertEquals(
            history, 
            ImList.<Move>convert(
                Arrays.asList(COLOR_HINT, NUMBER_HINT, PLAY, DISCARD)
            )
        );
    }

}
