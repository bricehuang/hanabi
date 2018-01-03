$(function() {
    pollLoop();
});

var pollLoop = function() {
    $.ajax(
        'server/play', {
        cache: false,
        type: 'GET',
        processData: false,
        contentType: 'application/json',
        data: '',
        timeout: 90000
        }
    )
    .done(function(response){
        if (!response.authorized){
            // reject, not authorized
            alert(response.error_msg);
            window.location = 'index.html';
        } else if (response.is_null){
            // sentinel message; resend request
            pollLoop();
        } else {
            // pass content to appropriate handler
            getHandler(response.type)(response.content);
        }
    }).fail(function(xmlHttpRequest, code, error){
        // TODO
        alert('something went wrong!');
//        pollLoop();
    });
}

var getHandler = function(responseType) {
    switch (responseType) {
        case "join_lobby_ack":
            return joinLobbyHandler;
        case "present_lobby_users":
            return lobbyUsersHandler;
        case "open_games":
            return openGamesHandler;
        case "user_to_lobby":
            return userLobbyChatHandler;
        case "server_to_lobby":
            return serverLobbyChatHandler;
        case "leave_lobby_ack":
            return leaveLobbyHandler;

        case "join_game_ack":
            return joinGameHandler;
        case "present_game_users":
            return gameUsersHandler;
        case "user_to_game":
            return userGameChatHandler;
        case "server_to_game":
            return serverGameChatHandler;
        case "game_start":
            return gameStartHandler;
        case "game_state":
            return gameStateHandler;
        case "game_end":
            return gameEndHandler;
        case "leave_game_ack":
            return leaveGameHandler;

        case "logout_ack":
            return logoutHandler;

        default:
            return doNothingHandler;
    }
}

var joinLobbyHandler = function(content) {
    $('#lobby_visibility').text("visible");
    pollLoop();
}
var lobbyUsersHandler = function(content) {
    $('#lobby_users').empty();
    var users = content.users;
    for (var i=0; i<users.length; i++) {
        var user = users[i];
        $('#lobby_users').append($('<li>').text(user));
    }
    pollLoop();
}
var openGamesHandler = function(content) {
    $('#games_list').empty();
    var game_list = content.games;
    for (var i=0; i<game_list.length; i++) {
        game = game_list[i]
        $('#games_list').append($('<li>').html(
            "" + game.id + " " + game.capacity + " " + game.players + " " + game.state
        ));
    }
    pollLoop();
}
var userLobbyChatHandler = function(content) {
    $('#lobby_messages').append($('<li>').html(content.from.bold() + ": " + content.message));
    pollLoop();
}
var serverLobbyChatHandler = function(content) {
    $('#lobby_messages').append($('<li>').html(content.message.italics()));
    pollLoop();
}
var leaveLobbyHandler = function(content) {
    $('#lobby_messages').empty();
    $('#lobby_users').empty();
    $('#games_list').empty();
    $('#lobby_visibility').text("hidden");
    pollLoop();
}

var joinGameHandler = function(content) {
    $('#game_visibility').text("visible");
    $('#game_status').text("Waiting");
    pollLoop();
}
var gameUsersHandler = function(content) {
    $('#game_users').empty();
    var users = content.users;
    for (var i=0; i<users.length; i++) {
        var user = users[i];
        $('#game_users').append($('<li>').text(user));
    }
    pollLoop();
}
var userGameChatHandler = function(content) {
    $('#game_messages').append($('<li>').html(content.from.bold() + ": " + content.message));
    pollLoop();
}
var serverGameChatHandler = function(content) {
    $('#game_messages').append($('<li>').html(content.message.italics()));
    pollLoop();
}
var gameStartHandler = function(content) {
    $('#game_status').text("In Progress");
    pollLoop();
}
var gameStateHandler = function(content) {
    $('#game_state').text(JSON.stringify(content.state));
    pollLoop();
}
var gameStateHandler = function(content) {
    $('#game_status').text("Finished.  Score: " + content.score);
    pollLoop();
}
var leaveGameHandler = function(content) {
    $('#game_messages').empty();
    $('#game_users').empty();
    $('#game_status').text("");
    $('#game_state').text("");
    $('#game_visibility').text("hidden");
    pollLoop();
}

var doNothingHandler = function(content) {
    pollLoop();
}
var logoutHandler = function(content) {
    window.location = 'index.html';
}
