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
    $("#lobby-container").removeClass("hidden");
    pollLoop();
}
var lobbyUsersHandler = function(content) {
    $('#lobby_players').empty();
    var users = content.users;
    for (var i=0; i<users.length; i++) {
        var user = users[i];
        $('#lobby_players').append('<tr><td>' + user + '</td></tr>');
    }
    pollLoop();
}
var openGamesHandler = function(content) {
    $('#games_list').empty();
    var game_list = content.games;
    for (var i=0; i<game_list.length; i++) {
        game = game_list[i]
        $('#games_list').append(
            '<tr onclick="join_game(' + game.id + ')">' +
                '<td>' + game.id + '</td>' +
                '<td>' + game.players + '</td>' +
                '<td>' + game.status + '</td>' +
            '</tr>'
        );
    }
    pollLoop();
}
var userLobbyChatHandler = function(content) {
    $('#lobby_messages').append($('<li>').html(content.from.bold() + ": " + content.message));
    scrollChat($('#lobby_chat_text'));
    pollLoop();
}
var serverLobbyChatHandler = function(content) {
    $('#lobby_messages').append($('<li>').html(content.message.italics()));
    scrollChat($('#lobby_chat_text'));
    pollLoop();
}
var scrollChat = function(chat_window) {
    if (chat_window.scrollTop() + chat_window.height() + 20 >= chat_window[0].scrollHeight) {
        chat_window.scrollTop(chat_window[0].scrollHeight);
    }
}
var leaveLobbyHandler = function(content) {
    $('#lobby_messages').empty();
    $('#lobby_users').empty();
    $('#games_list').empty();
    $("#lobby-container").addClass("hidden");
    pollLoop();
}

var joinGameHandler = function(content) {
    $("#game-container").removeClass("hidden");
    $('#game_status').text("Waiting");
    pollLoop();
}
var gameUsersHandler = function(content) {
    $('#game_players').empty();
    var users = content.users;
    for (var i=0; i<users.length; i++) {
        var user = users[i];
        $('#game_players').append('<tr><td>' + user + '</td></tr>');
    }
    pollLoop();
}
var userGameChatHandler = function(content) {
    $('#game_messages').append($('<li>').html(content.from.bold() + ": " + content.message));
    scrollChat($('#game_chat_text'));
    pollLoop();
}
var serverGameChatHandler = function(content) {
    $('#game_messages').append($('<li>').html(content.message.italics()));
    scrollChat($('#game_chat_text'));
    pollLoop();
}
var gameStartHandler = function(content) {
    $('#game_status').text("In Progress");
    // $('#start_game').addClass("disabled");
    pollLoop();
}
var gameStateHandler = function(content) {
    // $('#game_state').text(JSON.stringify(content.state) + JSON.stringify(content.users));
    drawCardArray(content.state.hands);
    pollLoop();
}
var gameEndHandler = function(content) {
    $('#game_status').text("Finished.  Score: " + content.score);
    pollLoop();
}
var leaveGameHandler = function(content) {
    $('#game_messages').empty();
    $('#game_players').empty();
    $('#game_status').text("");
    $('#game_state').text("");
    $("#game-container").addClass("hidden");
    pollLoop();
}

var doNothingHandler = function(content) {
    pollLoop();
}
var logoutHandler = function(content) {
    window.location = 'index.html';
}




var UNIT = 15;
var BLUE = "#87CEFA";
var GREEN = "#90EE90";
var RED = "#FFB6C1";
var WHITE = "#F8F8FF";
var YELLOW = "#FFFF80";
var GRAY = "#C3C3C3";
var DARKGRAY = "#808080";
var BLACK = "#000000";
var COLORS = [BLUE, GREEN, RED, WHITE, YELLOW];
var NUMBERS = [1,2,3,4,5];

var getColor = function(color) {
    switch(color) {
        case "B": return BLUE;
        case "G": return GREEN;
        case "R": return RED;
        case "W": return WHITE;
        case "Y": return YELLOW;
        default: return GRAY;
    }
}

function appendHtml(targetC, htmldata) {
    var theDiv = document.getElementById(targetC);
    var newNode = document.createElement('div');
    newNode.innerHTML = htmldata;
    theDiv.appendChild(newNode)
}

var drawCard = function(canvas, spec) {
    var ctx = canvas.getContext("2d");
    ctx.fillStyle = getColor(spec.color);
    ctx.font = (3*UNIT)+"px Arial";
    if (spec.hinted) {
        ctx.fillRect(0,0,6*UNIT,5*UNIT);
        ctx.fillStyle = BLACK;
        ctx.fillText(spec.number, 2.167*UNIT, 3.5*UNIT);
    } else {
        ctx.fillRect(0,0,5*UNIT,6*UNIT);
        ctx.fillStyle = BLACK;
        ctx.fillText(spec.number, 1.667*UNIT, 4*UNIT);
    }
}
var drawCardGivenId = function(id, spec) {
    var canvas = document.getElementById(id);
    drawCard(canvas, spec);
}

var drawCardArray = function(hands) {
    $('game-field').empty();
    var players = hands.length;
    var handSize = hands[0].length;

    var html = "";
    for (var i=0; i<players; i++) {
        for (var j=0; j<handSize; j++) {
            var width;
            var height;
            if (hands[i][j].hinted) {
                width = 6*UNIT;
                height = 5*UNIT;
            } else {
                width = 5*UNIT;
                height = 6*UNIT;
            }
            html +=(
                "<canvas"+
                " id=card"+i+j+
                " width="+width +
                " height="+height +
                " style='border:1px solid "+DARKGRAY+";'></canvas>"
            );
        }
        html += "<br><p></p>"
    }
    appendHtml('game-field', html);

    for (var i=0; i<players; i++) {
        for (var j=0; j<handSize; j++) {
            drawCardGivenId('card'+i+j, hands[i][j]);
        }
    }
}
