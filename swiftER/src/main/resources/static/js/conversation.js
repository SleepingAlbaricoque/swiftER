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

// 유저 아이디 검색 및 자동 완성 기능을 위한 검색창
const searchBar = document.querySelector('.contact-search-bar');

/**********************************************************************************************************/

// 웹소켓 연결하기
function connect() {
    const socket = new SockJS('/swiftER/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        
        // aside 목록에 있는 모든 유저 subscribe
        otherUsers.forEach((otherUser)=>{
			stompClient.subscribe('/user/topic/' + otherUser.value, function(message) {
            	handleIncomingMessage(JSON.parse(message.body));
        	});
        	subscriptions.push(otherUser.value);
		});
        
        
        // 목록에 없는 유저가 메세지 보냈을 때 받기 위해 messages 채널에 subscribe
        
        stompClient.subscribe('/user/queue/messages', function(message) {
			if(!subscriptions.includes(JSON.parse(message.body).receiver)){ // subscriptions에 없는지 확인
				receiveMsgFromUnsubscribedUser(JSON.parse(message.body));
			}
        });
        
    });
}

function handleIncomingMessage(message){
	let currentChat = document.querySelector('.message-conversation-contact').innerText;
	let currentChatUsernameIndex = currentChat.indexOf('님');
	let currentChatUsername = currentChat.substring(0, currentChatUsernameIndex);
	
	// 메세지를 보낸 유저가 이미 구독한 유저이고, 현재 열려있는 창의 대화 상대와 일치할 때만 현재 창에 새 메세지 출력
	for(const i in subscriptions){
		if(message.sender === subscriptions[i] && message.sender === currentChatUsername){
			showMessage(message);
		}
	}
	
	// aside의 마지막 메세지도 방금 수신한 메세지로 바꿔주기
	//let currentConvo = document.querySelector('.on');
	let convoList = document.querySelectorAll('#otherUser');
	for(const i in convoList){
		let destination = convoList[i];
		
		if(destination.value === message.sender){
			// aside에 새로운 메세지 알림 점 있는지 체크해서 메세지 내용 출력
			if(destination.nextElementSibling.nextElementSibling.tagName === 'I'){ // 알림 점이 있는 경우
				let asideMessageContent = destination.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling;
			    asideMessageContent.innerText = message.message;
				
			}else{ // 알림 점이 없는 경우
				let asideMessageContent = destination.nextElementSibling.nextElementSibling.nextElementSibling;
			    asideMessageContent.innerText = message.message;
			    
			    // aside에 새로운 메세지가 왔음을 알리는 점 표시(fontawesome)
				const alertCircle = document.createElement('i');
				alertCircle.classList.add('fa-solid', 'fa-circle', 'fa-2xs', 'new-message-alert');
				destination.nextElementSibling.insertAdjacentElement('afterend', alertCircle);
			}
		}
	}
}

// 새로 들어온 메세지 출력하기(이미 구독한 유저가 메세지를 보낸 경우)
function showMessage(message){
	let messageLi = document.createElement('li');
	let messageContent = document.createElement('div');
	
	messageLi.classList.add('otherUser-chat-bubble-li');
	messageContent.innerText = message.message;
	messageContent.classList.add('otherUser-chat-bubble');
	
	// 시간 출력
	let receiveTime = message.rdate.substring(11, 16);
	let date = document.createElement('span');
	date.innerText = receiveTime;
	
	messageList.appendChild(messageLi);
	messageLi.appendChild(messageContent);
	messageLi.appendChild(date);
}

// 구독하지 않은 유저가 메세지를 보냈을 때 처리하기
function receiveMsgFromUnsubscribedUser(message){
	
	// 메세지를 송신한 유저 구독하기
	stompClient.subscribe('/user/topic/' + message.sender, function(message){
		
	});
	
	// 새로 들어온 메세지 및 송신자를 aside에 출력하기
	let div = document.querySelector('.contact-list-wrapper');
	
	let ul = document.createElement('ul');
	ul.classList.add('message-list');
	div.appendChild(ul);
	
	let li = document.createElement('li');
	li.classList.add('message');
	li.addEventListener('click', function(){
		loadConversation(event, message.sender);
	});
	ul.appendChild(li);
	
	li.innerHTML = `<input type="hidden" id="otherUser" value="` + message.sender + `">`
				+ `<a href="#" class="contact">` + message.sender + `</a>`
				+ `<i class="fa-solid fa-circle fa-2xs new-message-alert"></i>`
				+ `<br>`
				+ `<a href="#" class="message-content">` + message.message + `</a>`;
}



// 작성한 메세지 전송하고 화면에 출력하기
function sendMessage() {
   let receiver = document.querySelector('.on');
   
    const message = {
        sender: currentUser.value,
        receiver: receiver.value,
        message: messageInput.value
    };
    
    // 이전 대화 기록이 없는 유저에게 보내는 경우 해당 유저에게 구독 먼저 하기
    
    // aside에 해당 유저 이름과 방금 보낸 메세지를 출력하기(ul 객체로)
    
    
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
	
	console.log('event: ' + event.target);
	console.log('event target: ' + event.target.classList.contains('autocomplete-result'));
	
	// 현재 대화 상대에게 on 클래스 적용하기
	if(!event.target.classList.contains('autocomplete-result')){ // 자동 완성 리스트의 유저를 누른 게 아닌 경우에만 적용하기
		event.target.querySelector('#otherUser').classList.add('on');
	
		// aside에서 현재 대화 상대에 표시된 새로운 메세지 표시 점이 있다면 없애기
		if(event.target.querySelector('.fa-circle')){
			event.target.querySelector('.fa-circle').remove();
		}
	}
	
	
	/*
	// 이미 웹소켓 연결되어있는 경우 웹소켓 연결 끊고 새로 연결, 아닌 경우 바로 웹소켓 연결 => 페이지 최초 로드시 대화리스트의 모든 유저를 구독하는 방식으로 바꿈(실시간 알림때문에)
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
	
	// 기존에 메세지 전송 날짜 저장한 리스트의 값들 지우기
	dateList.length = 0;
	
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
	console.log('dates: ' + dates.length);
	console.log('rdate: ' + rdate);
	console.log(dateList.includes(rdate));
	
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

// 검색창에서 검색 시 유저 이름 자동 완성 기능(admin_header 로직 가져옴)
function usernameAutoComplete(){
	let query = searchBar.value;
	
	const searchResults = document.getElementById('contact-search-results');
	const searchResultWrapper = document.querySelector('.contact-search-result-wrapper');
	
	fetch('/swiftER/message/search?query=' + query)
	.then((response) => response.json())
	.then((data) => {
		searchResults.innerHTML = "";
		data.forEach((result) => {
			const li = document.createElement('li');
			li.textContent = result;
			li.setAttribute('onclick', 'createNewConversation(this.innerText)');
			li.classList.add('autocomplete-result');
			searchResults.appendChild(li);
		});
		searchResults.style.display= 'block';
		
			// 백스페이스로 query 내용 모두 지우는 경우 검색 결과 창이 안뜨게 하기
			if(!query){
			searchResults.style.display= 'none';
		}
	});
}

// 검색창에 입력할 때마다 자동 완성 기능 실행
searchBar.addEventListener('input', () => {
	usernameAutoComplete();
});


// 자동 완성 리스트에서 유저 선택시, 이미 대화 목록에 있다면 해당 대화를 로드하고 대화 목록에 없다면 해당 유저에게 메세지 보낼 수 있도록 준비(aside에는 메세지를 보내고나야 표시하기)
function createNewConversation(value){
	
	// 제목에 클릭한 유저의 이름 출력하기
	document.querySelector('.message-conversation-contact').innerText = value + "님과의 대화";
	
	if(subscriptions.includes(value)){ // 이미 대화 목록에 있는 유저인 경우
		loadConversation(event, value);
	}else{ // 대화 목록에 없는 유저인 경우
		
		// 기존에 로드된 대화 삭제
		let messageList = document.querySelector('.message-chat-list');
		messageList.innerHTML = '';
	}
}

connect();