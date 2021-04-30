function start() {
	let response = fetch('/request').then(function(serverPromise){ 
	      serverPromise.json()
	        .then(function(j) { 
	          showMainCounter(j);
	        })
	        .catch(function(e){
	          console.log(e);
	        });
	    })
	    .catch(function(e){
	        console.log(e);
	      });
	
}

function showMainCounter(mainCounter) {
    document.getElementById("main-counter").innerHTML = mainCounter.data + " db. támadás.";
}
