/**
 * 
 */
$(document).ready(function(){
	let isEmailOk = false;
	$('#findPwEmailCheck').click(function(){
		let email = $('input[name=email]').val();
		
		$.ajax({
			url : '/swiftER/member/checkEmail',
			method : 'get',
			data : {"email":email},
			dataType : 'json',
			success : function(data){
				console.log(email);
			}
		});
	});
	
	$('#findPwCheckCode').click(function(){
		let code = $('input[name=code]').val();
		
		$.ajax({
			url : '/swiftER/member/checkCode',
			method : 'get',
			data : {"code":code},
			dataType : 'json',
			success : function(data){
				
				console.log('data : ' + data);
				
				if(data.result != 0){
					$('.resultCode').css('color', 'red').text('잘못된 인증번호입니다');
					console.log(data.result);
				}else{
					$('.resultCode').css('color', 'green').text('인증 되었습니다');
					$('input[name=code]').attr('readonly', true);
					isEmailOk = true;
					console.log(data.result);
				}
			}
			
		})
	});
	
	$('#findPwSubmit').click(function(){
		// 이메일 검증
		if(!isEmailOk){
			alert('이메일 인증을 진행해야합니다.');
			return false;
		}else{
			location.href="/swiftER/member/changePw";
		}
	});
	
});