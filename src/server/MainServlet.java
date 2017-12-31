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

        Player player = Authentication.getPlayerOfRequest(request, this.getServletContext());
		if (player == null) {
			// invalid session key.  Reject user.  
			try {
				JSONObject result;
				result = new JSONObject()
						.put("success", false)
						.put("error_type", "unauthorized")
						.put("error_msg", "Unauthorized. Please sign in.");
				response.getWriter().println(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		} else {
			try {
				JSONObject message = player.getMessageToSend().put("success", true);
				System.out.println(message);
				response.getWriter().println(message);
			} catch (InterruptedException | JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Got post request.");
		Player player = Authentication.getPlayerOfRequest(request, this.getServletContext());
		if (player == null) {
			// invalid session key.  Ignore request.  
			return;
		}
		String cmd = request.getParameter("cmd");
		if (cmd.equals("logout")) {
			try {
				player.logout();
			} catch (InterruptedException | JSONException e) {
				e.printStackTrace();
			}
		} else if (cmd.equals("chat")) {
			String content = request.getParameter("content");
			player.chat(content);
		}
	}

}
