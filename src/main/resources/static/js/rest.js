async function start() {
	let response = await fetch('/request');
	if(!response.ok) {
		alert("Sikerült. A szerver túl lett terhelve.");
	}
}
