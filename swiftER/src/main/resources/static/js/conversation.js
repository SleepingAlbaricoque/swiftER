/**
 * 
 */
let stompClient = null;
const messageList = document.querySelector('.message-chat-list'); // 메세지 리스트(ul)의 parent element
let dateList = []; // 날짜 출력시 중복 막기위한 배열

// 사용자 입력 메세지, 현재 로그인 된 사용자, 대화 상대
const messageInput = document.getElementById('message-input');
const currentUser = document.getElementById('currentUser');

// 현재 사용자가 메세지를 주고 받은 사용자 리스트(현재 aside에 떠있는 사용자들)
const otherUsers = document.querySelectorAll('#otherUser');

// stomp 구독 정보를 저장하기 위한 리스트 객체
const subscriptions = [];

// 웹소켓 연결하기
function connect() {
	console.log('log2');
	
    const socket = new SockJS('/swiftER/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        
        // aside 목록에 있는 모든 유저 subscribe
        otherUsers.forEach((otherUser)=>{
			stompClient.subscribe('/user/topic/' + otherUser.value, function(message) {
				console.log('Received message:', message);
				console.log('Received message body:', JSON.parse(message.body));
            	handleIncomingMessage(JSON.parse(message.body));
        	});
        	subscriptions.push(otherUser.value);
		});
		console.log('subscriptions: ' + subscriptions.length);
        
        // 목록에 없는 유저가 메세지 보냈을 때 받기 위해 messages 채널에 subscribe
        stompClient.subscribe('/user/queue/messages', function(message) {
			alert(message);
        });
    });
}

function handleIncomingMessage(message){
	for(const i in subscriptions){
		if(message.sender == subscriptions[i]){
			showMessage(message);
		}
	}
}

// 새로 들어온 메세지 출력하기
function showMessage(message){
	console.log('제발');
	let messageLi = document.createElement('li');
	let messageContent = document.createElement('div');
	
	messageLi.classList.add('otherUser-chat-bubble-li');
	messageContent.innerText = message.message;
	messageContent.classList.add('otherUser-chat-bubble');
	
	// 시간 출력
	let receiveTime = message.rdate.substring(11, 16);
	let date = document.createElement('span');
	date.innerText = receiveTime;
	
	// aside의 마지막 메세지도 방금 수신한 메세지로 바꿔주기
	let currentConvo = document.querySelector('.on');
	let asideMessageContent = currentConvo.nextElementSibling.nextElementSibling.nextElementSibling;
	console.log('asideMessageContent: ' + asideMessageContent);
    asideMessageContent.innerText = message.message;
	
	messageList.appendChild(messageLi);
	messageLi.appendChild(messageContent);
	messageLi.appendChild(date);
}

// 작성한 메세지 전송하고 화면에 출력하기
function sendMessage() {
   let receiver = document.querySelector('.on');
   
    const message = {
        sender: currentUser.value,
        receiver: receiver.value,
        message: messageInput.value
    };
    stompClient.send("/app/chat", {}, JSON.stringify(message));
    
    // 방금 전송한 메세지를 화면에 출력하기 => 데이터베이스 저장 시간과 화면 출력 시간 불일치 문제 -> ajax로 해결?
    console.log(messageInput.value);
	let messageLi = document.createElement('li');
	let messageContent = document.createElement('div');
	let date = document.createElement('span');
	messageContent.innerText = messageInput.value;
	date.innerText = getCurrentTime();
	
	messageLi.classList.add('my-chat-bubble-li');
	messageContent.classList.add('my-chat-bubble');
	
	messageList.appendChild(messageLi);
	messageLi.appendChild(date);
	messageLi.appendChild(messageContent);
    
    // aside의 마지막 메세지도 방금 전송한 메세지로 바꿔주기
    let asideMessageContent = document.querySelector('.message-content');
    asideMessageContent.innerText = messageInput.value;
    
    // 메세지 입력 창 초기화하기
    messageInput.value = '';
}

// 전송 버튼 누를 시 메세지 전송하기
document.getElementById('message-send-button').addEventListener('click', function() {
    // 메세지 전송하고 출력하기
    sendMessage();
});

// 클릭한 유저와의 대화 전부 불러오기
function loadConversation(event, otherUser){
	// 대화 상대 리스트에서 내가 선택한 상대에게만 on 클래스 적용하고, 이전에 적용된 경우가 있으면 지우기
	const otherUsersList = document.querySelectorAll('input[type="hidden"]');
	otherUsersList.forEach((item)=>{
		item.classList.remove('on');
		console.log(item);
	});
	
	// 현재 대화 상대에게 on 클래스 적용하기
	event.target.querySelector('input[type="hidden"]').classList.add('on');
	
	/*
	// 이미 웹소켓 연결되어있는 경우 웹소켓 연결 끊고 새로 연결, 아닌 경우 바로 웹소켓 연결
	if (stompClient !== null) {
        stompClient.disconnect();
        connect(otherUser);
    }else{
		connect(otherUser);
	}
	*/
	
	// 로드된 기존 대화 지우기
	let messageList = document.querySelector('.message-chat-list');
	messageList.innerHTML = '';
	
	// 메세지 작성 창 내용 지우기
	messageInput.value = '';
	
	// 대화 불러와서 출력하기
	let headers = new Headers();
	headers.append("Content-type", "application/json; charset=utf-8");
	fetch("/swiftER/conversation/" + otherUser, {headers:headers, method:"GET"})
	.then(response => response.json())
	.then(function(json){
		
		
		json.forEach(element => {
			if(element.sender == otherUser){
				// 날짜 확인해서 날짜 구분선 추가하기
				checkDateAndDivide(element.rdate.substring(0,10));
				
				let messageLi = document.createElement('li');
				let message = document.createElement('div');
				let date = document.createElement('span');
				message.innerText = element.message;
				date.innerText = element.rdate.substring(11, 16); // 메세지 전송 시간 표시
				
				messageLi.classList.add('otherUser-chat-bubble-li');
				message.classList.add('otherUser-chat-bubble');
				
				messageList.appendChild(messageLi);
				messageLi.appendChild(message);
				messageLi.appendChild(date);
			}else{
				// 날짜 확인해서 날짜 구분선 추가하기
				checkDateAndDivide(element.rdate.substring(0,10));
				
				let messageLi = document.createElement('li');
				let message = document.createElement('div');
				let date = document.createElement('span');
				message.innerText = element.message;
				date.innerText = element.rdate.substring(11, 16); // 메세지 전송 시간 표시
				
				messageLi.classList.add('my-chat-bubble-li');
				message.classList.add('my-chat-bubble');
				
				messageList.appendChild(messageLi);
				messageLi.appendChild(date);
				messageLi.appendChild(message);
			}
		});
	})
	.catch(err => console.log(err));
	
	// 제목에 클릭한 유저의 이름 출력하기
	document.querySelector('.message-conversation-contact').innerText = otherUser + "님과의 대화";
}

// 날짜 구분선 중 불러온 메세지와 같은 날짜(년-월-일)가 있는지 확인 -> 없으면 해당 날짜 구분선 추가
function checkDateAndDivide(rdate){
	let dates = document.querySelectorAll('.message-date');
	
	
	if(dates.length > 0){
			dates.forEach((date)=>{
				
			if(!dateList.includes(rdate)){
				let dividerLi = document.createElement('li');
				let divider = document.createElement('div');
				divider.innerText = rdate;
				
				dividerLi.classList.add('message-date-divider');
				divider.classList.add('message-date');
				
				messageList.appendChild(dividerLi);
				dividerLi.appendChild(divider);
				
				dateList.push(rdate);
			}
		});
	}else{
		dateList.push(rdate);
		
		let dividerLi = document.createElement('li');
		let divider = document.createElement('div');
		divider.innerText = rdate;
		
		dividerLi.classList.add('message-date-divider');
		divider.classList.add('message-date');
		
		messageList.appendChild(dividerLi);
		dividerLi.appendChild(divider);
	}
}

// 현재 날짜 및 시간 'yyyy-MM-dd hh:mm:ss'에 맞게 구하기
function getCurrentTime(){
	const now = new Date();
	const hours = String(now.getHours()).padStart(2, '0');
	const minutes = String(now.getMinutes()).padStart(2, '0');
	
	const formattedTime = `${hours}:${minutes}`;
	return formattedTime;
}

connect();