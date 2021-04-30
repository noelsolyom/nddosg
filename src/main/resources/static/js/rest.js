async function start() {
	let response = await fetch('/request');
	if(!response.ok) {
		console.log(response);
	}
	
}
