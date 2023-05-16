/**
 * 
 */
$(document).ready(function(){
	$('#btnEmailCheck').click(function(){
		let email = $('input[name=email]').val();
		$(this).hide(); // 버튼 숨기기
  		$(".resultEmail").text('전송중...');
		$.ajax({
			url : '/swiftER/member/checkEmail',
			method : 'get',
			data : {"email":email},
			dataType : 'json',
			success : function(data){
				console.log(email);
				$(".resultEmail").css('color', 'green').text('전송완료');
			}
		});
	});
	
	$('#btncheckCode').click(function(){
		let code = $('input[name=code]').val();
		
		$.ajax({
			url : '/swiftER/member/checkCode',
			method : 'get',
			data : {"code":code},
			dataType : 'json',
			success : function(data){
				
				console.log('data : ' + data);
				
				if(data.result != 0){
					$('.resultCode').css('color', 'red').text('사용할수 없는 이메일입니다');
					console.log(data.result);
				}else{
					$('.resultCode').css('color', 'green').text('사용 가능한 이메일입니다');
					$('input[name=code]').attr('readonly', true);
					isEmailAuthOk = true;
					console.log(data.result);
				}
			}
			
		})
	});
	
});