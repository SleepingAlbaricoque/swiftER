$(function(){
    // 상품평 작성 팝업 띄우기
    $('.reviewbtn').click(function(e){
        e.preventDefault();
        let uid = $('input[name=uid]').val();
        console.log("uid",uid);
        if (uid != '') {
			$('#popReview').addClass('on');
		} else {
		    // uid가 널이면 알림창 띄우기 등 처리
		    if(confirm('로그인이 필요합니다. 로그인 화면으로 이동하시겠습니까?')){
		    	location.href = "/swiftER/member/login";
			} else {
				location.reload();
			}
		}
        
    });
            
    // 팝업 닫기
    $('#btnClose').click(function(e){
		e.preventDefault();
        $(this).closest('.popup').removeClass('on');                
    });

    // 상품평 작성 레이팅바 기능
    $(".my-rating").starRating({
        starSize: 20,
        useFullStars: true,
        strokeWidth: 0,
        useGradient: false,
        minRating: 1,
        ratedColors: ['#ffa400', '#ffa400', '#ffa400', '#ffa400', '#ffa400'],
        callback: function(currentRating, $el){
            alert('별점을 입력하시겠습니까? 선택하신 별점 수 ' + currentRating);
            let hide = document.getElementById('hide');
            var input = document.createElement('input'); 
			input.type = 'hidden'; 
			input.name = 'rating'; 
			input.value = currentRating;
			hide.appendChild(input);
			console.log('hide', hide);
        }
    });

});