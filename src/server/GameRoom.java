package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import game.Game;

public class GameRoom extends Room {

    @Override
    protected void setAbstractFields() {
        this.serverChatType = "server_to_game";
        this.serverChatType = "user_to_game";        
    }    
    
    private final int gameID;
    private final int nPlayers;
    private final Game game;
    private final List<String> playerIDs;
    private final Map<String, String> usernamesByID;

    private boolean hasStarted;
    public int playersPresent;

    public GameRoom(ServletContext context, int gameID, int nPlayers) {
        super(context);
        this.gameID = gameID;
        this.nPlayers = nPlayers;
        this.game = new Game(nPlayers);
        this.hasStarted = false;
        this.playerIDs = new ArrayList<>();
        this.usernamesByID = new HashMap<>();

        this.hasStarted = false;
        this.playersPresent = 0;
    }

    private void refreshAllPlayersViews() {
        throw new RuntimeException("Unimplemented");
    }

}
