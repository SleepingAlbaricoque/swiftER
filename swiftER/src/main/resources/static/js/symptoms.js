/**
 * 
 */
	
	$( window ).scroll( function() {
		if ( $( this ).scrollTop() > 200 ) {
			$( '.topscroll' ).fadeIn();
		} else {
			$( '.topscroll' ).fadeOut();
		}
	
	});
	$(document).ready(function() {
	  // 스크롤 버튼 클릭 이벤트 처리
	  $("a.topscroll").click(function() {
	    // 부드러운 스크롤 애니메이션
	    $("html, body").animate({ scrollTop: 0 }, "slow");
	    return false;
	  });
	});


    // 반려동물 정보 체크박스 둘중하나 선택
    function handleCheckboxClick(event, containerId) {
        var clickedCheckbox = event.target;
        if (clickedCheckbox.checked) {
            var checkboxes = document.querySelectorAll('#' + containerId + ' input[type="radio"]');
            for (var i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i] !== clickedCheckbox) {
                    checkboxes[i].checked = false;
                }
            }
        }
    }

    // 체크박스 해제 (초기화 버튼)
    function uncheckAllCheckboxes() {
        var checkboxes = document.querySelectorAll('input[type="checkbox"]');
        var radioboxes = document.querySelectorAll('input[type="radio"]');
        for (var i = 0; i < checkboxes.length; i++) {
            checkboxes[i].checked = false;
        }
        for (var i = 0; i < radioboxes.length; i++) {
        	radioboxes[i].checked = false;
        }
    }
    

    
    // 검색 버튼 클릭시 Jquery 전송
	function checkboxresult(){
    	
    	// 체크박스와 라디오박스 값 받아와서 배열에 쌓기
    	const checkboxes = document.querySelectorAll('input[type="checkbox"], input[type="radio"]');
        const checkedCheckboxes = Array.from(checkboxes).filter((checkbox) => checkbox.checked);
        const checkedValues = checkedCheckboxes.map((checkbox) => checkbox.id);
        const delimiter = "&";
        console.log(checkedValues);
        
        // 유효성 검사를 위한 선언
        const chks = document.querySelectorAll('input[type="checkbox"]');
        const chkarr = Array.from(chks).filter((checkbox) => checkbox.checked);
        const chkarrValues = chkarr.map((checkbox) => checkbox.id);
		
		// 유효성 검사
        if(chkarrValues.length == 0) {
        	alert("증상을 체크해주세요!");
        	return false;	
        }
        
        if($('input[name="chk1"]').is(":checked") == false){
        	alert('동물 접촉 여부 체크해주세요!');
        	return false;
        }
        if($('input[name="chk2"]').is(":checked") == false){
        	alert('동물 접촉 여부 체크해주세요!');
        	return false;
        }
        if($('input[name="chk3"]').is(":checked") == false){
        	alert('동물 접촉 여부 체크해주세요!');
        	return false;
        }
        

        let symptoms = checkedValues.join(delimiter);
 		
 		let ischk = confirm('검색하시겠습니까?');
 		
 		if(ischk){
 			location.href="/swiftER/symptoms/resultsymptoms?symptom="+symptoms;
 		}else{
 			return false;
 		}
    }
    