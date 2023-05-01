/**
 * 
 */
$(document).ready(function(){
	$('#btnEmailCheck').click(function(){
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