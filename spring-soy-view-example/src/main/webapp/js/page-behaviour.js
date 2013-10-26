var APP = {}

APP.words = ['hi!']

APP.pushNewWord = function() {
	var newWord = $('#newWordTextField').val();
	APP.words.push(newWord);
	$('#clientWords').html(soy.example.clientWords({ words: APP.words }));
}

$(document).ready(function() {
	$('#getServerTimeLink').click(function() {
        var templateName = "templates/server-time.soy";
        var url = '/spring-soy-view-example/app/soy/hashes?file=' + templateName;
        $.get(url, function(hash) {
            //we could use hash files from a window object, imagine a vector with a key as a filename and value as a hash
            var url = '/spring-soy-view-example/app/soy/compileJs?hash=' + hash['templates/server-time.soy'] + '&file=templates/server-time.soy'
            $.ajaxSetup({
                cache: true
            });
            $.getScript(url, function(data, textStatus, jqxhr) {
                if (jqxhr.status === 200) {
                    $.ajax({
                        url: "/spring-soy-view-example/app/server-time",
                        context: document.body,
                        success: function(data) {
                            $('#serverTime').html(data);
                        }
                    });
                }
            })
        });
	});
	
	$('#submitButton').click(APP.pushNewWord);
	
	$('#wordsForm').submit(function() {
		APP.pushNewWord();
		return false;
	});
});