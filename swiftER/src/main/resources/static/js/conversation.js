/**
 * 
 */
let stompClient = null;

function connect() {
	console.log('log2');
	let username = document.getElementById('currentUser').value;
	let otherUser = document.getElementById('otherUser').value;
	
    const socket = new SockJS('/swiftER/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic', function(message) {
			alert(message);
            showMessage(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const currentUser = document.getElementById('currentUser');
    const otherUser = document.getElementById('otherUser');
    const message = {
        sender: currentUser.value,
        receiver: otherUser.value,
        message: messageInput.value
    };
    stompClient.send("/app/chat", {}, JSON.stringify(message));
    messageInput.value = '';
}

document.getElementById('message-form').addEventListener('submit', function(event) {
    event.preventDefault();
    sendMessage();
});

function showMessage(message) {
	console.log('log4');
    const messageDiv = document.createElement('div');
    messageDiv.innerHTML = `<strong>${message.sender}</strong>: ${message.message}`;
    document.getElementById('messages').appendChild(messageDiv);
}

console.log('log1');
connect();