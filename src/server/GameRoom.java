package server;

import javax.servlet.ServletContext;

import game.Game;

public class GameRoom extends Room {

    @Override
    protected void setAbstractFields() {
        this.serverChatType = "server_to_game";
        this.serverChatType = "user_to_game";        
    }    
    @Override
    protected boolean isLobby() {
        return false;
    }

    private final int gameID;
    private final int nPlayers;
    private final Game game;

    private boolean started;
    private boolean finished;

    public GameRoom(ServletContext context, int gameID, int nPlayers) {
        super(context);
        this.gameID = gameID;
        this.nPlayers = nPlayers;
        this.game = new Game(nPlayers);
        this.started = false;
        this.finished = false;
    }
    
    public int gameID() {
        return gameID;
    }
    public int nPlayers() {
        return nPlayers;
    }
    public int playersPresent() {
        return players.size();
    }

    public String state() {
        if (!started) {
            return "Waiting";
        } else if (!finished) {
            return "In progress";
        } else {
            return "Finished";
        }
    }

    private void refreshAllPlayersViews() {
        throw new RuntimeException("Unimplemented");
    }

}
