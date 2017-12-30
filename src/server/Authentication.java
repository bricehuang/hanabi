package server;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Authentication {

    /**
     * Finds a session id cookie from a request's cookie set, if it exists
     * @param cookies
     * @return a session id cookie, if one exists; else null.  
     */
    public static Cookie findSessionCookie(Cookie[] cookies) {
    		if (cookies == null) { return null; }
        for (Cookie cookie : cookies) {
    			if (cookie.getName().equals("session_id")) {
    				return cookie;
    			}
        }
        return null;
    }

    /**
     * Get player object associated with a request, via its session key cookie 
     * @param request 
     * @return the Player associated with this request, or null if invalid
     */
    public static Player getPlayerOfRequest(HttpServletRequest request, ServletContext context) {
    		Map<String, Player> playersBySessionKey = Config.getPlayersBySessionID(context); 
		Cookie sessionKeyCookie = Authentication.findSessionCookie(request.getCookies());
    		
    		if (sessionKeyCookie == null) { return null; } 
    		if (playersBySessionKey.containsKey(sessionKeyCookie.getValue())) {
    			return playersBySessionKey.get(sessionKeyCookie.getValue());
    		} else {
    			return null;
    		}
    }

}
