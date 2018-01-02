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
                content: {
                    n_players: $('#game_size').val()
                }
            })
        }
    )
    $('#game_size').val('')
}
$('#game_size').keypress(function(event) {
    if (event.keyCode === 13) {
        make_game();
    }
});
$('#make_game').click(make_game);

var join_game = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "join_game",
                content: {
                    game_id: $('#game_id').val()
                }
            })
        }
    )
    $('#game_id').val('')
}
$('#game_id').keypress(function(event) {
    if (event.keyCode === 13) {
        join_game();
    }
});
$('#join_game').click(join_game);





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
                    message: $('#game_action_field').val()
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