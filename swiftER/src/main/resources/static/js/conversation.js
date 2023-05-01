/**
 * 
 */
let stompClient = null;

function connect() {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/' + username, function(message) {
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
    stompClient.send("/swiftER/app/chat", {}, JSON.stringify(message));
    messageInput.value = '';
}

document.getElementById('message-form').addEventListener('submit', function(event) {
    event.preventDefault();
    sendMessage();
});

connect();