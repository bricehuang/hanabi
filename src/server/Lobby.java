package server;

public class Lobby extends Room {

    @Override
    protected void setAbstractFields() {
        this.serverChatType = "server_to_lobby";
        this.userChatType = "user_to_lobby";
    }

}
