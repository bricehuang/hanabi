package server;

import java.io.IOException;

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
         *    - join_lobby_ack
         *        content: {}
         *    - present_lobby_users
         *        content: {
         *            users: [username1, ... ]
         *        }
         *    - open_games
         *        content: {
         *            games: [
         *                {
         *                    id: int
         *                    players: int
         *                    capacity: int
         *                    state: string
         *                }
         *            ]
         *        }
         *    - user_to_lobby
         *        content: {
         *            from: string
         *            message: string
         *        }
         *    - server_to_lobby
         *        content: {
         *            message: string
         *        }
         *    - leave_lobby_ack
         *        content: {}
         *
         *    - join_game_ack
         *        content: {}
         *    - present_game_users
         *        content: {
         *            users: [username1, ... ]
         *        }
         *    - user_to_game
         *        content: {
         *            from: string
         *            message: string
         *        }
         *    - server_to_game
         *        content: {
         *            message: string
         *        }
         *    - game_start
         *        content: {}
         *    - game_state
         *        content: {
         *            users: [username1, ... ], 
         *            state: HiddenHandView.jsonify()
         *        }
         *    - game_end
         *        content: {score: int}
         *    - leave_game_ack
         *        content: {}
         *
         *    - logout_ack
         *        content: {}
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
                System.out.println("Outgoing message");
                System.out.println(message);
                System.out.println();
                if (
                    !message.getBoolean("is_null") &&
                    message.getString("type").equals("logout_ack")
                ) {
                    // session key is useless once logged out anyway.  But delete it just
                    // for good measure.
                    Cookie cookie = new Cookie("session_id", "");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
                response.getWriter().println(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // allowed commands
    private static final String CHAT = "chat";
    private static final String LOGOUT = "logout";
    private static final String MAKE_GAME = "make_game";
    private static final String JOIN_GAME = "join_game";
    private static final String START_GAME = "start_game";
    private static final String GAME_ACTION = "game_action";
    private static final String EXIT_GAME = "exit_game";

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
         *  While in lobby:
         *    - chat
         *    - make_game
         *    - join_game
         *    - logout
         *  While in game:
         *    - chat
         *    - start_game
         *    - game_action
         *    - exit_game
         *    - logout
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
        System.out.println(cmd);
        System.out.println(content);
        System.out.println();

        switch (cmd) {
            case CHAT:
                chatHandler(player, content);
                break;
            case LOGOUT:
                logoutHandler(player);
                break;
            case MAKE_GAME:
                makeGameHandler(player, content);
                break;
            case JOIN_GAME:
                joinGameHandler(player, content);
                break;
            case START_GAME:
                startGameHandler(player);
                break;
            case GAME_ACTION:
                gameActionHandler(player, content);
                break;
            case EXIT_GAME:
                exitGameHandler(player);
                break;
            default:
                // invalid cmd, do nothing
                break;
        }
    }

    /**
     * Sends chat from player to his room.  Routed to either lobby or game room.
     * Sends:
     *   - user_to_lobby or user_to_game, whichever relevant, to room
     * @param player
     * @param content {message: chat string}
     */
    private void chatHandler(Player player, JSONObject content) {
        try {
            player.chat(content.getString("message"));
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out a player. Removes player's credentials from the global context.
     * If player is in game room, notifies room that player left.  If game is in
     * progress, resigns the game.
     * Sends:
     *   - leave_game_ack or leave_lobby_ack, whichever relevant, to player
     *   - logout_ack to player
     *   If user in lobby:
     *     - present_lobby_users
     *   If user in game room:
     *     If game in progress:
     *       - game_content to game room
     *       - game_end to game room
     *     - server_to_game
     *     - present_game_users
     *
     * @param player
     */
    private void logoutHandler(Player player) {
        try {
            player.logout();
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO not functional yet
    /**
     * Creates a new game room and moves player to that room.  Only valid when
     * player currently in lobby.  Notifies lobby that new game created.
     * Sends:
     *   - leave_lobby_ack, to player
     *   - server_to_lobby, to lobby
     *   - present_lobby_users, to lobby
     *   - open_games, to lobby
     *   - join_game_ack, to player
     *   - server_to_game, to game room
     *   - present_game_users, new game room
     * @param player
     * @param content {n_players: game size int}
     */
    private void makeGameHandler(Player player, JSONObject content) {
        if (! player.isInLobby()) { return; }
        ServletContext context = this.getServletContext();
        Lobby lobby = Config.getLobby(context);
        try {
            lobby.createGameRoom(player, content.getInt("n_players"));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Joins an existing game room.  Only valid when player currently
     * in lobby, and joined game is waiting and under capacity.
     * Notifies room that player joined.
     * Sends:
     *   - leave_lobby_ack, to player
     *   - present_lobby_users, to lobby
     *   - open_games, to lobby
     *   - join_game_ack, to player
     *   - server_to_game, to game room
     *   - present_game_users, to game room
     * @param player
     * @param content {game_id: game id int}
     */
    private void joinGameHandler(Player player, JSONObject content) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Starts a game.  Only valid when player is in game, and game is
     * at capacity and not started.
     * Sends:
     *   - game_start, to game room
     *   - game_state, to game room
     *   - open_games, to lobby
     * @param player
     */
    private void startGameHandler(Player player) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Takes a game action.  Only valid when player is in game.  Valid
     * only on player's turn, except for resignations which are always
     * valid any time.
     * Sends:
     *   - game_state, to game room
     *   If game over:
     *     - game_end, to game room
     *     - open games, to lobby
     * @param player
     * @param content TODO
     */
    private void gameActionHandler(Player player, JSONObject content) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Leaves a game room to return to lobby.  Only valid when player is
     * in game.  Notifies game and lobby that player left and joined.  If
     * game is in progress, resigns game.
     * Sends:
     *   - leave_game_ack, to player
     *   If game in progress:
     *     - game_content to game room
     *     - game_end to game room
     *   - server_to_game
     *   - present_game_users
     *
     *   - join_lobby_ack, to player
     *   - present_lobby_users, to lobby
     *   - open_games, to lobby
     *
     * @param player
     */
    private void exitGameHandler(Player player) {
        throw new RuntimeException("Unimplemented");
    }
}
