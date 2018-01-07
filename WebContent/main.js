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
    $('#start_game').addClass("disabled");
    pollLoop();
}
var gameStateHandler = function(content) {
    $('#game-field').html('');
    var state = content.state;
    var users = content.users;
    drawLives(state.lives);
    drawHints(state.hints);
    drawToMove(users[state.to_move]);
    drawPlaysNew(state.plays);
    drawDiscardsNew(state.discards);
    drawHands(state.hands, users);
    pollLoop();
}
var gameEndHandler = function(content) {
    $('#game_status').text("Finished.  Score: " + content.score);
    $('#color_hint').addClass("disabled");
    $('#number_hint').addClass("disabled");
    $('#play').addClass("disabled");
    $('#discard').addClass("disabled");
    $('#resign').addClass("disabled");
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
var drawLives = function(lives) {
    $('#game-field').append('<p>' + Array(lives+1).join('L') + '</p>');
}
var drawHints = function(hints) {
    $('#game-field').append('<p>' + Array(hints+1).join('H') + '</p>');
}
var drawToMove = function(name) {
    $('#game-field').append('<p>' + name + ' to move.</p>');
}

var drawCard = function(canvas, spec, highlight) {
    var unit = 0.2 * Math.min(canvas.height, canvas.width);
    var ctx = canvas.getContext("2d");
    ctx.fillStyle = getColor(spec.color);
    ctx.font = (3*unit)+"px Arial";
    ctx.fillRect(0,0,canvas.width,canvas.height);
    ctx.fillStyle = BLACK;
    ctx.fillText(spec.number, canvas.width/2 - 0.833*unit, canvas.height/2 + unit);
    if (highlight) {
        ctx.lineWidth = 2;
        ctx.rect(0,0,canvas.width,canvas.height);
        ctx.stroke();
    }
}

var cardCanvas = function(id, sideways, toggleHighlightOnClick, unit) {
    var width;
    var height;
    if (sideways) {
        width = 6*unit;
        height = 5*unit;
    } else {
        width = 5*unit;
        height = 6*unit;
    }
    var onclick = "'toggleHighlight("+'"'+id+'"'+")'";
    return (
        "<canvas"+
        " id="+id +
        " width="+width +
        " height="+height +
        (toggleHighlightOnClick ? " onclick="+onclick : "") +
        " style='vertical-align: bottom; border:1px solid "+DARKGRAY+";'></canvas>"
    );
}

var drawCardInHand = function(jqCanvas) {
    var canvas = jqCanvas[0];
    var spec = jqCanvas.data('spec');
    var highlight = jqCanvas.data('highlight');
    drawCard(canvas, spec, highlight);
}

var drawHands = function(hands, names) {
    var players = hands.length;
    var html = "";
    for (var i=0; i<players; i++) {
        html += names[i] + "<br>";
        for (var j=0; j<hands[i].length; j++) {
            html += cardCanvas('card'+i+j, hands[i][j].hinted, true, UNIT);
        }
        html += "<br><p></p>"
    }
    // TODO put these somewhere more reasonable
    html += "<button id=color_hint onclick='color_hint()' class='btn btn-standard'>Color Hint</button>";
    html += "<button id=number_hint onclick='number_hint()' class='btn btn-standard'>Number Hint</button>";
    html += "<button id=play onclick='play()' class='btn btn-standard'>Play</button>";
    html += "<button id=discard onclick='discard()' class='btn btn-standard'>Discard</button>";
    html += "<button id=resign onclick='resign()' class='btn btn-standard'>Resign</button>";
    $('#game-field').append(html);

    for (var i=0; i<players; i++) {
        for (var j=0; j<hands[i].length; j++) {
            var jqCanvas = $('#card'+i+j);
            jqCanvas.data('spec', hands[i][j]);
            jqCanvas.data('highlight', false);
            drawCardInHand(jqCanvas);
        }
    }
}

var drawCardOnTable = function(jqCanvas) {
    var canvas = jqCanvas[0];
    var spec = jqCanvas.data('spec');
    drawCard(canvas, spec, false);
}

var drawCardArray = function(cardsByColor, idPrefix) {
    var colors = cardsByColor.length;
    var html = "";
    for (var i=0; i<colors; i++) {
        for (var j=0; j<cardsByColor[i].length; j++) {
            html += cardCanvas(idPrefix+i+j, false, false, UNIT/2);
        }
        html += "<br>";
    }
    $('#game-field').append(html);
    for (var i=0; i<colors; i++) {
        for (var j=0; j<cardsByColor[i].length; j++) {
            var jqCanvas = $('#'+idPrefix+i+j);
            jqCanvas.data('spec', cardsByColor[i][j]);
            drawCardOnTable(jqCanvas);
        }
    }
}

var drawPlaysNew = function(plays) {
    var playArray = [];
    for (var i=0; i<plays.length; i++) {
        var colorRow = [];
        var color = plays[i].color;
        var count = plays[i].count;
        for (var j=1; j<=count; j++) {
            colorRow[colorRow.length] = {color: color, number: j};
        }
        if (colorRow.length > 0) {
            playArray[playArray.length] = colorRow;
        }
    }
    drawCardArray(playArray, 'play');
}

var drawDiscardsNew = function(discards) {
    // first, make an array with all discarded cards.
    var discardedCards = [];
    for (var i=0; i<discards.length; i++) {
        for (var j=0; j<discards[i].count; j++) {
            discardedCards[discardedCards.length] = discards[i].card;
        }
    }
    // then, splice it by color
    var discardArray = [];
    var currentRow = [];
    for (var i=0; i<discardedCards.length; i++) {
        if (currentRow.length === 0 || currentRow[0].color === discardedCards[i].color) {
            currentRow[currentRow.length] = discardedCards[i];
        } else {
            discardArray[discardArray.length] = currentRow;
            currentRow = [];
            currentRow[currentRow.length] = discardedCards[i];
        }
    }
    if (currentRow.length > 0) {
        discardArray[discardArray.length] = currentRow;
    }
    drawCardArray(discardArray, 'discard');
}

var toggleHighlight = function(id) {
    var jqCanvas = $('#'+id);
    jqCanvas.data('highlight', !jqCanvas.data('highlight'));
    drawCardInHand(jqCanvas);
}
