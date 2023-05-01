/**
 * 
 */
function checkAgreeDoc() {
	let checkbox = document.querySelector("#agree > input[type=checkbox]");
	let checkAgree = checkbox.checked;
	
	//console.log(checkAgree);
	
	if(checkAgree == false){
		alert('모두 동의를 체크하셔야 가입을 진행할 수 있습니다!');
	} else {
		location.href="/swiftER/member/registerDoc";
	}
}

function checkAgreeNor() {
	let checkbox = document.querySelector("#agree > input[type=checkbox]");
	let checkAgree = checkbox.checked;
	
	//console.log(checkAgree);
	
	if(checkAgree == false){
		alert('모두 동의를 체크하셔야 가입을 진행할 수 있습니다!');
	} else {
		location.href="/swiftER/member/registerNor";
	}
}