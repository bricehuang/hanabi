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
        pollLoop();
    });
}

var getHandler = function(responseType) {
    switch (responseType) {
        case "logout_ack":
            return logoutHandler;
        case "server_to_lobby":
            return serverLobbyChatHandler;
        case "user_to_lobby":
            return userLobbyChatHandler;
        case "game_list": 
            return gameListHandler;
        default:
            return doNothingHandler;
    }
}

var doNothingHandler = function(content) {
    pollLoop();
}
var logoutHandler = function(content) {
    window.location = 'index.html';
}
var serverLobbyChatHandler = function(content) {
    $('#messages').append($('<li>').html(content.message.italics()));
    pollLoop();
}
var userLobbyChatHandler = function(content) {
    $('#messages').append($('<li>').html(content.from.bold() + ": " + content.message));
    pollLoop();
}
var gameListHandler = function(content) {
    $('#games').empty();
    var game_list = content.games;
    for (var i=0; i<game_list.length; i++) {
        game = game_list[i]
        $('#games').append($('<li>').html(
            "" + game.id + " " + game.capacity + " " + game.players + " " + game.state
        ));
    }
    pollLoop();
}
