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
    $('#start_game').removeClass("disabled");
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
    $('#start_game').addClass("disabled");
    $('#plays-helptext').removeClass("hidden");
    $('#discards-helptext').removeClass("hidden");
    pollLoop();
}
var gameStateHandler = function(content) {
    $('#game-field').html('');
    var state = content.state;
    var users = content.users;
    drawLives(state.lives);
    drawHints(state.hints);
    drawToMove(users[state.to_move]);
    drawCardsLeft(state.cards_left);
    drawLastMove(users, state.last_move);
    drawPlays(state.plays);
    drawDiscards(state.discards);
    drawHands(state.hands, users);
    pollLoop();
}
var gameEndHandler = function(content) {
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
    $('#game-field').html('');
    $('#lives-info').text('');
    $('#hints-info').text('');
    $('#cards-left-info').text('');
    $('#to-move-info').text('');
    $('#plays-helptext').addClass("hidden");
    $('#discards-helptext').addClass("hidden");
    $('#plays-display').html('');
    $('#discards-display').html('');
    $("#game-container").addClass("hidden");
    pollLoop();
}

var doNothingHandler = function(content) {
    pollLoop();
}
var logoutHandler = function(content) {
    window.location = 'index.html';
}

var SMALLUNIT = 6.5;
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
    $('#lives-info').text('Lives: ' + Array(lives+1).join('L'));
}
var drawHints = function(hints) {
    $('#hints-info').text('Hints: ' + Array(hints+1).join('H'));
}
var drawToMove = function(name) {
    $('#to-move-info').text('It is ' + name + "'" + 's turn to move.');
}
var drawCardsLeft = function(cardsLeft) {
    $('#cards-left-info').text('There are ' + cardsLeft + ' cards left in the deck.');
}

var parseList = function(list) {
    result = "" + (list[0]+1);
    for (var i=1; i<list.length; i++) {
        result += ", " + (list[i]+1);
    }
    return result;
}
var parseColor = function(color) {
    switch (color) {
        case "B": return "Blue";
        case "G": return "Green";
        case "R": return "Red";
        case "W": return "White";
        case "Y": return "Yellow";
        default: return "?????";
    }
}
var parseCard = function(card) {
    return parseColor(card.color) + " " + card.number
}

var parseColorHint = function(users, lastMove) {
    if (lastMove.positions.length === 1) {
        return (
            users[lastMove.actor] + " hinted that " + users[lastMove.hintee] + "'s position " +
            (lastMove.positions[0]+1) + " is " + parseColor(lastMove.color) + "."
        )
    } else {
        return (
            users[lastMove.actor] + " hinted that " + users[lastMove.hintee] + "'s positions " +
            parseList(lastMove.positions) + " are " + parseColor(lastMove.color) + "."
        )
    }
}
var parseNumberHint = function(users, lastMove) {
    if (lastMove.positions.length === 1) {
        return (
            users[lastMove.actor] + " hinted that " + users[lastMove.hintee] + "'s position " +
            (lastMove.positions[0]+1) + " is a " + lastMove.number + "."
        )
    } else {
        return (
            users[lastMove.actor] + " hinted that " + users[lastMove.hintee] + "'s positions " +
            parseList(lastMove.positions) + " are " + lastMove.number + "s."
        )
    }
}
var parsePlay = function(users, lastMove) {
    return (
        users[lastMove.actor] + " " + (lastMove.correct ? "correctly" : "incorrectly") +
        " played a " + parseCard(lastMove.card) + " from position " + (lastMove.position+1) + "."
    )
}
var parseDiscard = function(users, lastMove) {
    return (
        users[lastMove.actor] + " " + (lastMove.safe ? "safely" : "unsafely") +
        " discarded a " + parseCard(lastMove.card) + " from position " + (lastMove.position+1) + "."
    )
}
var parseResign = function(users, lastMove) {
    return users[lastMove.actor] + " resigned.";
}
var parseInvalid = function(users, lastMove) {
    return "";
}
var getParser = function(move_type) {
    switch (move_type) {
        case "color_hint":
            return parseColorHint;
        case "number_hint":
            return parseNumberHint;
        case "play":
            return parsePlay;
        case "discard":
            return parseDiscard;
        case "resign":
            return parseResign;
        default:
            return parseInvalid;
    }
}

var drawLastMove = function(users, lastMove) {
    var parsed = getParser(lastMove.move_type)(users, lastMove);
    if (parsed != "") {
        $('#game_history').append($('<li>').text(parsed));
    }
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

var cardCanvas = function(id, sideways, toggleHighlightOnClick, unit, alignBottom) {
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
    var style = "'" + (alignBottom ? 'vertical-align: bottom;' : "") + "border: 1px solid " + DARKGRAY + ";'"
    return (
        "<canvas"+
        " id=" + id +
        " width=" + width +
        " height=" + height +
        (toggleHighlightOnClick ? " onclick="+onclick : "") +
        " style=" + style +
        "></canvas>"
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
    var html = "<table style='display: inline'>";
    for (var i=0; i<players; i++) {
        html += "<tr>"
        html += "<td align='right'>" + names[i] + "</td>";
        html += "<td align='left'>";

        html += "<div style='height: "+(6*UNIT+5)+"px; width: " + (6*hands[i].length*UNIT+10) + "px;'>";
        // this forces the row to be full height
        html += "<div style='display: inline-block; height: 100%;'></div>";
        for (var j=0; j<hands[i].length; j++) {
            html += cardCanvas('card'+i+j, hands[i][j].hinted, true, UNIT, false);
        }
        html += "</div>"

        html += "</td>"
        html += "</tr>"
    }
    html += "<tr>"
    html += "<td align='right'></td>"

    html += "<td align='left'>"
    html += "<button id=color_hint onclick='color_hint()' class='btn btn-standard'>Color Hint</button>";
    html += "<button id=number_hint onclick='number_hint()' class='btn btn-standard'>Number Hint</button>";
    html += "<button id=play onclick='play()' class='btn btn-standard'>Play</button>";
    html += "<button id=discard onclick='discard()' class='btn btn-standard'>Discard</button>";
    html += "<button id=resign onclick='resign()' class='btn btn-standard'>Resign</button>";
    html += "</td>"

    html += "</table>"

    html += "<p></p>"
    $('#game-field').html(html);

    for (var i=0; i<players; i++) {
        for (var j=0; j<hands[i].length; j++) {
            var jqCanvas = $('#card'+i+j);
            jqCanvas.data('spec', {color: hands[i][j].color, number: hands[i][j].number});
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

var drawCardArray = function(cardsByColor, idPrefix, divIdToDraw) {
    var colors = cardsByColor.length;
    var html = "";
    for (var i=0; i<colors; i++) {
        for (var j=0; j<cardsByColor[i].length; j++) {
            var unit = SMALLUNIT;
            if (cardsByColor[i].length > 6) {
                unit = SMALLUNIT * 6 / cardsByColor[i].length
            }
            html += cardCanvas(idPrefix+i+j, false, false, unit, true);
        }
        html += "<br>";
    }
    $('#'+divIdToDraw).html(html);
    for (var i=0; i<colors; i++) {
        for (var j=0; j<cardsByColor[i].length; j++) {
            var jqCanvas = $('#'+idPrefix+i+j);
            jqCanvas.data('spec', cardsByColor[i][j]);
            drawCardOnTable(jqCanvas);
        }
    }
}

var drawPlays = function(plays) {
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
    drawCardArray(playArray, 'play', 'plays-display');
}

var drawDiscards = function(discards) {
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
    drawCardArray(discardArray, 'discard', 'discards-display');
}

var toggleHighlight = function(id) {
    var jqCanvas = $('#'+id);
    jqCanvas.data('highlight', !jqCanvas.data('highlight'));
    drawCardInHand(jqCanvas);
}
