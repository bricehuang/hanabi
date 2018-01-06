// TODO dedupe all this code

var lobby_chat = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "chat",
                content: {
                    message: $('#lobby_chat_field').val()
                }
            })
        }
    )
    $('#lobby_chat_field').val('');
}
$('#lobby_chat_field').keypress(function(event) {
    if (event.keyCode === 13) {
        lobby_chat();
    }
});
$('#lobby_chat').click(lobby_chat);

var make_game = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "make_game",
                content: {}
            })
        }
    )
}
$('#make_game').click(make_game);

var join_game = function(game_id) {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "join_game",
                content: {
                    game_id: game_id
                }
            })
        }
    )
}

var game_chat = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "chat",
                content: {
                    message: $('#game_chat_field').val()
                }
            })
        }
    )
    $('#game_chat_field').val('');
}
$('#game_chat_field').keypress(function(event) {
    if (event.keyCode === 13) {
        game_chat();
    }
});
$('#game_chat').click(game_chat);

var start_game = function() {
    $.post(
        "server/play", {
            data: JSON.stringify({
                cmd: "start_game",
                content: {}
            })
        }
    )
}
$('#start_game').click(start_game);

var game_action = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "game_action",
                content: {
                    action: $('#game_action_field').val()
                }
            })
        }
    )
    $('#game_action_field').val('');
}
$('#game_action_field').keypress(function(event) {
    if (event.keyCode === 13) {
        game_action();
    }
});
$('#game_action').click(game_action);

var exit_game = function() {
    $.post(
        "server/play", {
            data: JSON.stringify({
                cmd: "exit_game",
                content: {}
            })
        }
    )
}
$('#exit_game').click(exit_game);

var logout = function() {
    $.post(
        "server/play", {
            data: JSON.stringify({
                cmd: "logout",
                content: {}
            })
        }
    )
}
$('#logout').click(logout);

var getAndClearAllHighlights = function() {
    var canvasses = document.getElementsByTagName("canvas");
    var highlights = [];
    $( "canvas" ).each(function() {
        if ($(this).data('highlight')) {
            var id = $(this).attr('id');
            highlights[highlights.length] = {
                player: parseInt(id.charAt(4)),
                position: parseInt(id.charAt(5)),
            };
            $(this).data('highlight', false);
            drawCard($(this));
        }
    })
    console.log(highlights);
    return highlights;
}

var color_hint = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "game_action",
                content: {
                    move: "color_hint",
                    cards: getAndClearAllHighlights(),
                }
            })
        }
    )
}

var number_hint = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "game_action",
                content: {
                    move: "number_hint",
                    cards: getAndClearAllHighlights(),
                }
            })
        }
    )
}

var play = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "game_action",
                content: {
                    move: "play",
                    cards: getAndClearAllHighlights(),
                }
            })
        }
    )
}

var discard = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "game_action",
                content: {
                    move: "discard",
                    cards: getAndClearAllHighlights(),
                }
            })
        }
    )
}

var resign = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "game_action",
                content: {
                    move: "resign",
                    cards: getAndClearAllHighlights(),
                }
            })
        }
    )
}
