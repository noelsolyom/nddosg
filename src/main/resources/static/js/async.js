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

var stompFailureCallback = function (error) {
    console.log('STOMP: ' + error);
    setTimeout(stompConnect, 10000);
    console.log('STOMP: Reconecting in 10 seconds');
};


function disconnect() {
    if (stompMainCounterClient != null) {
    	stompMainCounterClient.disconnect();
    	stompMainCounterClient.log('Main counter disconnected.');
    }
}

function showMainCounter(mainCounter) {
    document.getElementById("main-counter").innerHTML = `${mainCounter.data} db. "támadás"`;
}

function checkComm() {
    setInterval(function() {
        if(stompMainCounterClient == null || stompMainCounterClient == undefined || !stompMainCounterClient.connected) {
        	console.log("Reconnecting...");
        	connectMainCounter();
        } 
    }, 10000);

}

checkComm();