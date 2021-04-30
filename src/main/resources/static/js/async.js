var stompMainCounterClient = null;

function connectWs() {
	connectMainCounter();
}

function connectMainCounter() {
    var socket = new SockJS('/mainCounterData');
    stompMainCounterClient = Stomp.over(socket);
    stompMainCounterClient.connect({}, function (frame) {
        console.log('Main counter connected: ' + frame);
        stompMainCounterClient.subscribe('/topic/mainCounter', function (mainCounter) {
            showMainCounter(JSON.parse(mainCounter.body));
        });
    });
}


function disconnect() {
    if (stompMainCounterClient != null) {
    	stompMainCounterClient.disconnect();
    	stompMainCounterClient.log('Main counter disconnected.');
    }
}

function showMainCounter(mainCounter) {
    document.getElementById("main-counter").innerHTML = mainCounter.data + " db. támadás.";
}
