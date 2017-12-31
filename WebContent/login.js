var submit = function() {
    $.post(
        "server/login",
        {name: $('#name').val()}
    )
    .done(function (jsonResponse) {
        if (jsonResponse.success) {
                window.location = 'main.html';
        } else {
                window.alert(jsonResponse.error_msg);
        }
    })
    .fail(function (xmlHttpRequest, code, error) {
        window.alert(xmlHttpRequest.responseText);
    });
}

$('#name').keypress(function(event) {
    if (event.keyCode === 13) {
        submit();
    }
});

$('#login').click(submit);
