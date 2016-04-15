window.onload = init;
var socket = new WebSocket("ws://localhost:8080/WebsocketT/chat");
socket.onmessage = onMessage;

function onMessage(event) {
	var received_msg =JSON.parse(event.data);;
	var comments = $("#comment").val();
	var response = JSON.parse(received_msg);
	$("#comment").val(response.userName +" : "+response.message+"\n"+comments);
	$("#infoBarMessage").html("Message is received...");
	$("#infoBar").attr("style", "display:block");
	$("#successBar").attr("style", "display:none");
	$("#warningBar").attr("style", "display:none");
}

function sendMessage(user, message) {
	var messageJson = {
			userName: user,
			message: message,
	};
	socket.send(JSON.stringify(messageJson));

	$("#successBarMessage").html("Message Sent Successfully");
	$("#successBar").attr("style", "display:block");
	$("#warningBar").attr("style", "none");
}


function init() {
	//hideForm();
}