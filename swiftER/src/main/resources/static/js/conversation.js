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
const otherUsers = document.querySelectorAll('.otherUser');

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
			if(!subscriptions.includes(JSON.parse(message.body).sender)){ // subscriptions에 없는지 확인
				receiveMsgFromUnsubscribedUser(JSON.parse(message.body));
			}
        });
        
    });
}

function handleIncomingMessage(message){
	console.log('aqui!');
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
	let convoList = document.querySelectorAll('.otherUser');
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
	if(!subscriptions.includes(message.sender)){
		stompClient.subscribe('/user/topic/' + message.sender, function(message){});
	}
	
	// 구독 후 송신자 유저를 구독 목록에 추가하기
	subscriptions.push(message.sender);
	
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
	
	li.innerHTML = `<input type="hidden" class="otherUser" value="` + message.sender + `">`
				+ `<a href="#" class="contact">` + message.sender + `</a>`
				+ `<i class="fa-solid fa-circle fa-2xs new-message-alert"></i>`
				+ `<br>`
				+ `<a href="#" class="message-content">` + message.message + `</a>`;
}



// 작성한 메세지 전송하고 화면에 출력하기
function sendMessage() {
   
   let receiverInfo = document.querySelector('.message-conversation-contact').innerText;
   let receiver = receiverInfo.substring(0, receiverInfo.indexOf('님'));
   
    const message = {
        sender: currentUser.value,
        receiver: receiver,
        message: messageInput.value
    };
    
    if(!subscriptions.includes(receiver)){ // 해당 유저와의 최초 메세지인 경우 - 보안 문제가 중요한 이슈가 되거나 대화 갯수가 많아지면 DB에서 직접 조회하는 게 나을 수도 있음
    	
    	// 해당 유저에게 먼저 구독하기
    	subscriptions.push(receiver);
    	
    	stompClient.subscribe('/user/topic/' + receiver, function(message){
			handleIncomingMessage(JSON.parse(message.body));
		});
    	
    	// aside에 해당 유저와의 대화 만들기
		
	}else{ // 기존 대화 목록이 존재하는 경우
	
		// aside의 마지막 메세지도 방금 전송한 메세지로 바꿔주기
		
	}
	
	
    //let asideMessageContent = document.querySelector('.message-content');
    //asideMessageContent.innerText = messageInput.value;
    
    stompClient.send("/app/chat", {}, JSON.stringify(message));
    
    // 방금 전송한 메세지를 화면에 출력하기 => 데이터베이스 저장 시간과 화면 출력 시간 불일치 문제 -> ajax로 해결?
	let messageLi = document.createElement('li');
	let messageContent = document.createElement('div');
	let date = document.createElement('span');
	messageContent.innerText = messageInput.value;
	date.innerText = getCurrentTime();
	messageLi.classList.add('my-chat-bubble-li');
	messageContent.classList.add('my-chat-bubble');
	
	const currentDate = new Date(); // 현재 날짜 구하기
	const year = currentDate.getFullYear();
	const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // Pad month with leading zero if necessary
	const day = String(currentDate.getDate()).padStart(2, '0'); // Pad day with leading zero if necessary
	const formattedDate = `${year}-${month}-${day}`;
	
	let dateDividerLi = document.createElement('li'); // 첫 메세지라 날짜 divider가 아예 없거나 마지막 메세지 이후 날짜가 변경되었다면 날짜 divider 삽입하기 위해 li 요소 생성
	let dateDivider = document.createElement('div'); // li 안에 값을 넣을 div 요소 생성
	dateDividerLi.appendChild(dateDivider); // li 안에 div를 append하기
	dateDividerLi.classList.add('message-date-divider');
	dateDivider.classList.add('message-date');
	dateDivider.innerText = formattedDate;

	// 이미 존재하는 날짜 divider 중 가장 마지막 divider 날짜와 현재 날짜가 다를 경우 날짜 divider 삽입
	let dateDividers = document.querySelectorAll('.message-date');
	
	if(dateDividers.length > 0){ // 다른 날짜 divider가 이미 존재하는 경우
		let lastDivider = dateDividers[dateDividers.length - 1].innerText;
		let currentConvertedDate = new Date(formattedDate);
		let lastDividerConvertedDate = new Date(lastDivider);
		
		if(currentConvertedDate > lastDividerConvertedDate){
			messageList.appendChild(dateDividerLi);
		}
		
	}else{ // 다른 날짜 divider가 없는 경우
		messageList.appendChild(dateDividerLi);
	}
	
	// 메세지 내용 출력
	messageList.appendChild(messageLi);
	messageLi.appendChild(date);
	messageLi.appendChild(messageContent);
    
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
	otherUsers.forEach((item)=>{
		item.classList.remove('on');
		console.log(item.classList.contains('on'));
	});
	
	console.log('event: ' + event.target.classList);
	console.log('event target: ' + event.target.classList.contains('autocomplete-result'));
	
	// 현재 대화 상대에게 on 클래스 적용하기
	if(!event.target.classList.contains('autocomplete-result')){ // 자동 완성 리스트의 유저를 누른 게 아닌 경우에만 적용하기
		console.log('if runs. event target contains ' + event.target.classList.contains('autocomplete-result'));
		event.target.querySelector('.otherUser').classList.add('on');
	
		// aside에서 현재 대화 상대에 표시된 새로운 메세지 표시 점이 있다면 없애기
		if(event.target.querySelector('.fa-circle')){
			event.target.querySelector('.fa-circle').remove();
		}
	}else{ // aside의 대화 목록을 직접 누른 경우
		for(let i=0; i < otherUsers.length; i++){ // aside의 대화 목록 중에서
			if(otherUsers[i].value === otherUser){ // 검색창에서 선택한 유저 아이디와 같은 아이디가 있다면
				otherUsers[i].classList.add('on'); // 그 유저 ul의 input[type=hidden] 요소에 on 클래스 부여
				
				if(otherUsers[i].nextElementSibling.nextElementSibling.tagName === 'I'){ // 해당 유저 대화 ul에 i 태그(새로운 메세지 알림 점)가 있다면
					otherUsers[i].nextElementSibling.nextElementSibling.remove(); // 알림 점 remove하기
				}
			}
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
	
	// 자동완성 결과 출력 후 사용자가 결과창 밖의 지점을 클릭하면 결과창 숨기기
  	document.addEventListener('click', function(event){
	   if(!searchResultWrapper.contains(event.target)){
		   searchResults.style.display='none';
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
		
		// 대화 상대 리스트에 적용된 on 클래스 지우기(이후 메세지를 보내면 aside에 대화 상대 등록 후 그 상대에게 on 클래스 주기)
		otherUsers.forEach((item)=>{
			item.classList.remove('on');
			console.log(item.classList.contains('on'));
		});
		
		// 기존에 로드된 대화 삭제
		let messageList = document.querySelector('.message-chat-list');
		messageList.innerHTML = '';
	}
}

connect();