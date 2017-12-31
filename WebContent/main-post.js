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

var chat = function() {
    $.post(
        "server/play",{
            data: JSON.stringify({
                cmd: "chat",
                content: {
                    message: $('#chat').val()
                }
            })
        }
    )
    $('#chat').val('');
}

$('#chat').keypress(function(event) {
    if (event.keyCode === 13) {
        chat();
    }
});

$('#chat-send').click(chat);
