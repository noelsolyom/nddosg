async function attack() {
	if(stompMainCounterClient == undefined ||stompMainCounterClient == null || stompMainCounterClient.connected == false) {
		console.log("Reconnecting...");
		connectWs();
	}
    let response = await fetch('/request');
    if (!response.ok) {
        console(response.message);
    }
}
