$(function(){
    // 상품평 작성 팝업 띄우기
    $('.reviewbtn').click(function(e){
        e.preventDefault();
        $('#popReview').addClass('on');
    });
            
    // 팝업 닫기
    $('.btnClose').click(function(){                
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
            console.log('DOM element ', $el);
        }
    });

});