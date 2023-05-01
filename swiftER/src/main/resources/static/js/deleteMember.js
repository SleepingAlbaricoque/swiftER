/**
 * 
 */
$(document).ready(function(){
	let regPass  = /^(?=.*[a-zA-Z])((?=.*\d)(?=.*\W)).{8,16}$/;
	let isPassOk  = false;
	
    $('input[name=pass2]').focusout(function(){
		let pass1 = $('input[name=pass1]').val();
		let pass2 = $(this).val();

		if(pass1 == pass2){

			if(pass2.match(regPass)){
				isPassOk = true;
				$('.resultPass').css('color', 'green').text('비밀번호가 일치합니다.');
			}else{
				isPassOk = false;
				$('.resultPass').css('color', 'red').text('영문, 숫자, 특수문자 조합하여 8~16자이어야 합니다.');
			}
		}else{
			isPassOk = false;
			$('.resultPass').css('color', 'red').text('비밀번호가 일치하지 않습니다.');
		}
	});
    
    $('#delete').click(function(){
		let checkAgree = $('input[type=checkbox]').is(':checked');
    	
		if(checkAgree == false){
			alert('동의를 체크하셔야 회원탈퇴를 진행할 수 있습니다');
		} else {
			// 비밀번호 검증
	    	
    		if(!isPassOk){
    			alert('비밀번호를 확인하십시오.');
    			return false;
    		}else{
    			let pass2 = $('input[name=pass2]').val();
				
				$.ajax({
					url : '/swiftER/member/deleteMember',
					method : 'post',
					data : {
						"pass2":pass2,
						},
					dataType : 'json',
					success : function(data){
						if(data.result == 1){
							alert('회원탈퇴 처리가 완료되었습니다');
							location.href="/swiftER/";
						}else{
							alert('오류 발생!');
						}
					}
				});
    		}
		}
    	
    });
});	