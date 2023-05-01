/**
 * 
 */
$(document).ready(function(){
	let regPass  = /^(?=.*[a-zA-Z])((?=.*\d)(?=.*\W)).{8,16}$/;
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
    
    $('#changePwSubmit').click(function(){
		let pass2 = $('input[name=pass2]').val();
		let uid = $('input[name=id]').val();
		$.ajax({
			url : '/swiftER/member/changePw',
			method : 'post',
			data : {
					"pass2":pass2,
					"uid":uid
					},
			dataType : 'json',
			success : function(data){
				if(data.result == 1){
					alert('비밀번호가 변경되었습니다!');
					location.href="/swiftER/";
				}else{
					alert('오류 발생!');
				}
			}
		});
    });
});