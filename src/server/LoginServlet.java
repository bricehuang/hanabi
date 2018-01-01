package server;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/server/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * If someone attempts to log in but already has a session id cookie,
     * logs out user corresponding to this cookie if he is still logged in.
     * Modifies global state, and should be called with locks on global context.
     * @param oldSessionCookie
     * @param context
     */
    private void logOutUserFromStaleCookie(Cookie oldSessionCookie, ServletContext context) {
        String oldSessionID = oldSessionCookie.getValue();
        Map<String, Player> playersBySessionID = Config.getPlayersBySessionID(context);
        if (playersBySessionID.containsKey(oldSessionID)) {
            Player player = playersBySessionID.get(oldSessionID);
            try {
                player.logout();
            } catch (InterruptedException | JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        ServletContext context = request.getServletContext();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Cookie oldSessionCookie = Authentication.findSessionCookie(request.getCookies());

        JSONObject result = new JSONObject();
        synchronized(context) {
            // if old session cookie exists, log out that user.
            if (oldSessionCookie != null) {
                logOutUserFromStaleCookie(oldSessionCookie, context);
            }

            // check if name is taken
            Set<String> allUsernames = Config.getAllUsernames(context);
            if (allUsernames.contains(name)) {
                try {
                    // fail.  Don't modify internal state and return to browser.
                    result.put("success", false);
                    result.put("error_msg", "That username already exists.");
                    response.getWriter().println(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String sessionID = Config.genSessionKey(context);
                    Room lobby = Config.getLobby(context);
                    Player player = new Player(sessionID, name, lobby, context);
                    Cookie cookie = new Cookie("session_id", sessionID);

                    // modify internal state
                    allUsernames.add(name);
                    Map<String, Player> playersBySessionID = Config.getPlayersBySessionID(context);
                    playersBySessionID.put(sessionID, player);
                    result.put("success", true);
                    System.out.println("All logged in players: " + allUsernames);

                    // return to browser
                    response.getWriter().println(result);
                    response.addCookie(cookie);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
