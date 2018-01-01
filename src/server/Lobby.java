package server;

import javax.servlet.ServletContext;

public class Lobby extends Room {

    public Lobby(ServletContext context) {
        super(context);
    }

    @Override
    protected void setAbstractFields() {
        this.serverChatType = "server_to_lobby";
        this.userChatType = "user_to_lobby";
    }
    
    /**
     * Constructs a new room and adds it to the global context.  Should
     * be called with locks on the global context.
     * @param nPlayers
     */
    private void makeNewRoom(int nPlayers) {
        int gameID = Config.genGameID(context);
        GameRoom newRoom = new GameRoom(context, gameID, nPlayers);
        Config.getActiveGames(context).put(gameID, newRoom);
        // TODO: announce room creation to all but the guy who made the room
    }

}
