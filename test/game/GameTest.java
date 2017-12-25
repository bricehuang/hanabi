package game;

import org.junit.Test;

import game.Game;

public class GameTest {

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test
    public void testPrint() {
        Game game = new Game(5);
        System.err.println(game.toString());
        game.play(0);
        System.err.println(game.toString());
        game.discard(0);
        System.err.println(game.toString());
        System.out.println(game.getPlayerView(0).toString());
    }

}
