/**
 * 
 */
            // 반려동물 정보 체크박스 둘중하나 선택
            function handleCheckboxClick(event, containerId) {
                var clickedCheckbox = event.target;
                if (clickedCheckbox.checked) {
                    var checkboxes = document.querySelectorAll('#' + containerId + ' input[type="checkbox"]');
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
            
            function checkboxresult() {
          	  const div1Checkbox1 = document.getElementById('div1-checkbox1');
          	  const div1Checkbox2 = document.getElementById('div1-checkbox2');
          	  
          	  const div2Checkbox1 = document.getElementById('div2-checkbox1');
          	  const div2Checkbox2 = document.getElementById('div2-checkbox2');
          	  
          	  const div3Checkbox1 = document.getElementById('div3-checkbox1');
          	  const div3Checkbox2 = document.getElementById('div3-checkbox2');

          	  if (!div1Checkbox1.checked && !div1Checkbox2.checked && !div2Checkbox1.checked && !div2Checkbox2.checked && !div3Checkbox1.checked && !div3Checkbox2.checked) {
          	    alert('동물 접촉 여부란에 체크하셔야 합니다!');
          	    return false;
          	  }

          	  return true;
          	}
            
            
            // 검색 버튼 클릭시 Jquery 전송
 			function checkboxresult(){
            	
            	const checkboxes = document.querySelectorAll('input[type="checkbox"]');
                const checkedCheckboxes = Array.from(checkboxes).filter((checkbox) => checkbox.checked);
                const checkedValues = checkedCheckboxes.map((checkbox) => checkbox.name);
                const delimiter = "& ";
                console.log(checkedValues);
                

                if(checkedValues.length == 0) {
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
                

                let symptom = checkedValues.join(delimiter);
         		
         		let ischk = confirm('검색하시겠습니까?');
         		
         		if(ischk){
         			location.href="/swiftER/symptoms/resultsymptoms?symptom="+symptom;
         		}else{
         			return false;
         		}
            }
            