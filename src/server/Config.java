package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class Config
 *
 */
@WebListener
public class Config implements ServletContextListener {
    /*
     * The following global variables currently exist:
     *   - players_by_sessionID: map of Players keyed by their session ID
     *   - all_usernames: set of usernames of all logged in players
     *   - lobby: the Lobby room of this server
     *   - active_games: map of currently active (not yet started, in progress,
     *   and finished-with-people-still-in-the-room) games, indexed by game ID
     */

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  {
        ServletContext context = arg0.getServletContext();
        context.setAttribute("players_by_sessionID", new HashMap<String, Player>());
        context.setAttribute("all_usernames", new HashSet<String>());
        context.setAttribute("lobby", new Lobby(context));
        context.setAttribute("active_games", new TreeMap<Integer, GameRoom>());
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  {
        ServletContext context = arg0.getServletContext();
        context.removeAttribute("players_by_sessionID");
        context.removeAttribute("all_usernames");
        context.removeAttribute("lobby");
        context.removeAttribute("active_games");
    }

    public static Map<String, Player> getPlayersBySessionID(ServletContext context) {
        return (Map<String, Player>) context.getAttribute("players_by_sessionID");
    }

    public static Set<String> getAllUsernames(ServletContext context) {
        return (Set<String>) context.getAttribute("all_usernames");
    }

    public static Room getLobby (ServletContext context) {
        return (Room) context.getAttribute("lobby");
    }

    public static Map<Integer, Room> getActiveGames (ServletContext context) {
        return (Map<Integer, Room>) context.getAttribute("active_games");
    }

    private static final int SESSION_KEY_LENGTH = 16;
    private static final String[] HEX_CHARS = {
        "0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"
    };
    private static String randomHexChar() {
        return HEX_CHARS[(int) (Math.random()*16)];
    }
    private static String randomHexString(int length) {
        StringBuffer ans = new StringBuffer();
        for (int i=0; i<length; i++) {
            ans.append(randomHexChar());
        }
        return ans.toString();
    }
    public static String genSessionKey(ServletContext context) {
        String key;
        do {
            key = randomHexString(SESSION_KEY_LENGTH);
        } while (getPlayersBySessionID(context).containsKey(key));
        return key;
    }

    public static int genGameID(ServletContext context) {
        int gameID;
        do {
            gameID = (int) (Math.random() * 1000000);
        } while (getActiveGames(context).keySet().contains(gameID));
        return gameID;
    }
}
