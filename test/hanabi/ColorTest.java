package hanabi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ColorTest {

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test
    public void testString(){
        assertEquals("B", Color.BLUE.toString());
        assertEquals("G", Color.GREEN.toString());
        assertEquals("R", Color.RED.toString());
        assertEquals("W", Color.WHITE.toString());
        assertEquals("Y", Color.YELLOW.toString());
    }

}
