/**
 * 
 */
// 데이터 검증에 사용할 정규표현식

    let regUid   = /^[a-z0-9]+[a-z0-9]{4,12}$/g;
    let regName  = /^[가-힣]{2,6}$/;
    let regEmail = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
    let regContact 	 = /^\d{3}-\d{3,4}-\d{4}$/;
    let regPass  = /^(?=.*[a-zA-Z])((?=.*\d)(?=.*\W)).{8,16}$/;
    let regZip = /\d{5}/;

// 폼 데이터 검증 결과 상태변수
    let isUidOk   = false;
    let isPassOk  = false;
    let isNameOk  = false;
    let isEmailOk = false;
    let isEmailAuthOk = false;
    let isContactOk 	  = false;
    let isZipOk = false;

    $(function(){

        $('input[name=uid]').keydown(function() {
        	isUidOk=false;
        });

    	// 아이디 검증
    	$('input[name=uid]').focusout(function(){
    		let uid = $('input[name=uid]').val();

    		if(isUidOk){
    			return;
    		}
    		
    		if(!uid.match(regUid)){
    			isUidOk = false;
    			$('.resultUid').css('color', 'red').text('아이디가 유효하지 않습니다.');
    			return;
    		}
    		
    		$('.resultUid').css('color', 'black').text('...');
    			
    		setTimeout(()=>{
    			
    			$.ajax({
    				url : '/swiftER/member/checkUid',
    				method : 'get',
    				data : {"uid":uid},
    				dataType : 'json',
    				success : function(data){
    					if(data.result == 0){
    						console.log('1');
    						isUidOk = true;
    						$('.resultUid').css('color', 'green').text('사용 가능한 아이디입니다.')
    					}else{
    						isUidOk = false;
    						console.log('2');
    						$('.resultUid').css('color', 'red').text('이미 사용중인 아이디입니다.')
    					}
    				}
    			});
    			
    		}, 500);
    	});

    	// 비밀번호 일치여부 확인
    	
    	$('input[name=pass2]').focusout(function(){
    		let pass1 = $('input[name=pass1]').val();
    		let pass2 = $(this).val();

    		if(pass1 == pass2){

    			if(pass2.match(regPass)){
    				isPassOk = true;
    				$('.resultPass').css('color', 'green').text('비밀번호가 일치합니다.');
    			}else{
    				isPassOk = false;
    				$('.resultPass').css('color', 'red').text('영문, 숫자, 특수문자 조합하여 8~12자이어야 합니다.');
    			}
    		}else{
    			isPassOk = false;
    			$('.resultPass').css('color', 'red').text('비밀번호가 일치하지 않습니다.');
    		}
    	});

    	// 이름 유효성 검증
    	$('input[name=name]').focusout(function(){
    		let name = $(this).val();

    		if(!name.match(regName)){
    			isNameOk  = false;
    			$('.resultName').css('color', 'red').text('이름은 한글 2자 이상이어야 합니다.');
    		}else{
    			isNameOk  = true;
    			$('.resultName').css('color', 'green').text('O');
    		}
    	});
    	
    	// 닉네임 검증
    	$('input[name=nickname]').focusout(function(){
    		let nickname = $('input[name=nickname]').val();
			isNickOk = false;
    		$('.resultNick').css('color', 'black').text('...');
    			
    		setTimeout(()=>{
    			
    			$.ajax({
    				url : '/swiftER/member/checkNick',
    				method : 'get',
    				data : {"nickname":nickname},
    				dataType : 'json',
    				success : function(data){
    					if(data.result == 0){
    						isNickOk = true;
    						$('.resultNick').css('color', 'green').text('사용 가능한 별명입니다.')
    					}else{
    						isNickOk = false;
    						$('.resultNick').css('color', 'red').text('이미 사용중인 별명입니다.')
    					}
    				}
    			});
    			
    		}, 500);
    	});

    	// 이메일 유효성 검사
    	$('input[name=email]').focusout(function(){
    		let email = $(this).val();

    		if(!email.match(regEmail)){
    			isEmailOk = false;
    			$('.resultEmail').css('color', 'red').text('이메일이 유효하지 않습니다.');
    		}else{
    			isEmailOk = true;
    			$('.resultEmail').text('');
    		}

    	});

    	// 휴대폰 유효성 검사
    	$('input[name=contact]').focusout(function(){
    		let contact = $(this).val();

    		if(!contact.match(regContact)){
    			isContactOk = false;
    			$('.resultContact').css('color', 'red').text('휴대폰이 유효하지 않습니다.');
    		}else{
    			isContactOk = true;
    			$('.resultContact').css('color', 'green').text('O');
    		}
    	});
    	
    	// 우편번호 유효성 검사
    	$('input[name=addr2]').focusout(function(){
    		let zip = $('input[name=zip]').val();
    		console.log(zip);

    		if(!zip.match(regZip)){
    			isZipOk = false;
    			$('.resultZip').css('color', 'red').text('우편번호가 유효하지 않습니다.');
    		}else{
    			isZipOk = true;
    			$('.resultZip').css('color', 'green').text('O');
    		}
    	});

    	// 폼 전송이 시작될 때 실행되는 폼 이벤트(폼 전송 버튼을 클릭했을 때)
    	
    	$('.register > form').submit(function(e){

    		////////////////////////////////////
    		// 폼 데이터 유효성 검증(Vaildation)
    		////////////////////////////////////
    		// 아이디 검증
    		console.log('아이디 검증 실행')
    		if(!isUidOk){
    			alert('아이디를 확인하십시오.');
    			return false;
    		}
    		// 비밀번호 검증
    		if(!isPassOk){
    			alert('비밀번호를 확인하십시오.');
    			return false;
    		}
    		// 이름 검증
    		if(!isNameOk){
    			alert('이름을 확인하십시오.');
    			return false;
    		}
    		// 별명 검증
    		if(!isNickOk){
    			alert('별명을 확인하십시오.');
    			return false;
    		}
    		// 이메일 검증
    		if(!isEmailOk){
    			alert('이메일을 확인하십시오.');
    			return false;
    		}
    		// 휴대폰 검증
    		if(!isContactOk){
    			alert('휴대폰을 확인하십시오.');
    			return false;
    		}
    		// 우편번호 검증
    		if(!isZipOk){
    			alert('우편번호를 확인하십시오.');
    			return false;
    		}
    		
    		// 최종 전송
    		return true;
    		
    	});
    });