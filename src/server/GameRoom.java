package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.Game;

public class GameRoom extends Room {

    private final int nPlayers;
    private final Game game;
    private final List<String> playerIDs;
    private final Map<String, String> usernamesByID;

    private boolean hasStarted;
    public int playersPresent;

    public GameRoom(int nPlayers) {
        super();
        this.nPlayers = nPlayers;
        this.game = new Game(nPlayers);this.hasStarted = false;
        this.playerIDs = new ArrayList<>();
        this.usernamesByID = new HashMap<>();

        this.hasStarted = false;
        this.playersPresent = 0;
    }

    private void refreshAllPlayersViews() {
        throw new RuntimeException("Unimplemented");
    }
}
