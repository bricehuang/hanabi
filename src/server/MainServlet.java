package server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/server/play")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
		/*
		 *  contract between server and GET clients: requests go to server/play and 
		 *  take the form of a long-poll request with empty content and a 90 second 
		 *  timeout.
		 *  
		 *  Server replies in JSON.  Replies to non-authenticated clients have the 
		 *  following fields:
		 *    - authorized: false
		 *    - error_msg: string describing the error message
		 *  
		 *  To prevent requests from timing out, the server will send back a blank 
		 *  message after 60 seconds if it has nothing to say, which will prompt the 
		 *  client to re-issue a request.  These blank responses take the following 
		 *  form: 
		 *    - authorized: true
		 *    - is_null: true
		 *  
		 *  Non-blank responses from the server take the following form:
		 *    - authorized: true
		 *    - is_null: false
		 *    - type: keyword describing message content (full list below)
		 *    - content: stringified JSON object with content of request.  (should 
		 *    be {} when no content necessary, e.g. logout-ack)
		 *  
		 *  Currently possible types: 
		 *    - server-to-chat
		 *          content: {
		 *              message: string
		 *          }
		 *    - user-to-chat
		 *          content: {
		 *              from: string
		 *              message: string
		 *          }
		 *    - logout-ack
		 *          content: {}
		 */

        /*
         *  Contract between server and workers: 
         *  Messages put on a player's outbound queue should have type and content fields.  
         *  When server retrieves an outbound message to send to client, it will add the 
         *  fields authorized = true, is_null = false.      
         */
        
        Player player = Authentication.getPlayerOfRequest(request, this.getServletContext());
		if (player == null) {
			// invalid session key.  Reject user.  
			try {
				JSONObject result;
				result = new JSONObject()
						.put("authorized", false)
						.put("error_msg", "Unauthorized. Please sign in.");
				response.getWriter().println(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		} else {
			try {
				JSONObject message = player.getMessageToSend().put("authorized", true);
				System.out.println(message);
				response.getWriter().println(message);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Player player = Authentication.getPlayerOfRequest(request, this.getServletContext());
		if (player == null) {
			// invalid session key.  Ignore request.  
			return;
		}

		/*
		 *  contract between server and POST clients: requests go to server/play and 
		 *  consist of a parameter "data" with two fields:
		 *    - cmd: keyword describing what action to take (full list below)
		 *    - content: stringified JSON object with content of request.  (should 
		 *    be {} when no content necessary, e.g. logout)
		 *    
		 *  On valid requests from valid (authenticated) clients, server performs 
		 *  requested actions and puts responses on players' outbound queues.  On 
		 *  invalid requests, server takes no action and returns silently. 
		 *  Any request from a non-authenticated client, or a request without a valid 
		 *  cmd field or correctly-JSON-formatted content field, is automatically 
		 *  rejected.  
		 *  
		 *  Currently accepted cmd strings: 
		 *    - logout
		 *    - chat
		 */
		
		/*
		 *  contract between server and dispatchers: server selects dispatcher by cmd 
		 *  string and sends dispatcher Player object and JSONified content.  Content is 
		 *  guaranteed to be a properly parsed JSON object.  Each dispatcher sets own 
		 *  contract for what content should be, and has the responsibility of doing 
		 *  nothing when that contract is not fulfilled.        
		 */
		
		String cmd;
		JSONObject content;
		try {
			JSONObject data = new JSONObject(request.getParameter("data"));
			cmd = data.getString("cmd");
			content = data.getJSONObject("content");
		} catch (JSONException e1) {
			// invalid request, do nothing
			e1.printStackTrace();
			return;
		}

		switch (cmd) {
		    case "logout": 
		    		logoutHandler(player);
		        break;
		    case "chat":
		    	    chatHandler(player, content);
		    	    break;
		    	default:
		    		// invalid cmd, do nothing
		    		break;
		}
	}
	
	private void logoutHandler(Player player) {
		try {
			player.logout();
		} catch (InterruptedException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void chatHandler(Player player, JSONObject content) {
		try {
			player.chat(content.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
