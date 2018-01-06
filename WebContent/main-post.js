var post_formatted = function(cmd, content) {
    return function() {
        $.post(
            "server/play",{
                data: JSON.stringify({
                    cmd: cmd,
                    content: content
                })
            }
        )
    }
}

var lobby_chat = function() {
    post_formatted("chat",{message: $('#lobby_chat_field').val()})();
    $('#lobby_chat_field').val('');
}
$('#lobby_chat_field').keypress(function(event) {
    if (event.keyCode === 13) {
        lobby_chat();
    }
});
$('#lobby_chat').click(lobby_chat);

var make_game = post_formatted("make_game", {});
$('#make_game').click(make_game);

var join_game = function(game_id) {
    post_formatted("join_game", {game_id: game_id})();
}

var game_chat = function() {
    post_formatted("chat",{message: $('#game_chat_field').val()})();
    $('#game_chat_field').val('');
}
$('#game_chat_field').keypress(function(event) {
    if (event.keyCode === 13) {
        game_chat();
    }
});
$('#game_chat').click(game_chat);

var start_game = post_formatted("start_game", {});
$('#start_game').click(start_game);

var exit_game = post_formatted("exit_game", {});
$('#exit_game').click(exit_game);

var logout = post_formatted("logout", {});
$('#logout').click(logout);

var getAndClearAllHighlights = function() {
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

var game_action_formatted = function(move) {
    return function() {
        var cards = getAndClearAllHighlights();
        post_formatted("game_action", {move: move, cards: cards})();
    }
}
var color_hint = game_action_formatted("color_hint");
var number_hint = game_action_formatted("number_hint");
var play = game_action_formatted("play");
var discard = game_action_formatted("discard");
var resign = game_action_formatted("resign");
