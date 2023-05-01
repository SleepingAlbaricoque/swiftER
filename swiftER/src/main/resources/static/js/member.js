/**
 * 
 */
$(function(){
	$('#note-submit').click(function(){
		let uid = $('#uid').val();
		let udate = $('#udate').val();
		let hos = $('#hos').val();
		let symptom = $('#symptom').val();
		
		let jsonData = {
				"uid" : uid,
				"rdate" : udate,
				"hospital" : hos,
				"symptom" : symptom
		}
		
		console.log(uid);
		console.log(udate);
		console.log(hos);
		console.log(symptom);
		
		$.ajax({
			url : '/swiftER/member/note',
			method : 'post',
			data : jsonData,
			dataType : 'json',
			success : function(data){
				if(data.result == 1){
					alert('저장되었습니다!');
					location.href="/swiftER/member/myPage"
				}else if(data.result == 2){
					alert('수정되었습니다!');
					location.href="/swiftER/member/myPage"
				}else{
					alert('저장 실패!');
				}
					}
		});
		
		
	});
	
	$('#calendar').change(function(){
		let uid = $('#huid').val();
		let rdate = $('#calendar').val();
		// console.log(rdate);
		let jsonData = {
				"uid": uid,
				"rdate": rdate
		}
		// console.log(jsonData);
		$.ajax({
			url : '/swiftER/member/findHistory',
			method : 'post',
			data : jsonData,
			dataType : 'json',
			success : function(data){
				if(data.hs != null){
					
					document.getElementById("date1").innerHTML = data.hs.rdate;
					document.getElementById("hos1").innerHTML = data.hs.hospital;
					document.getElementById("sym1").innerHTML = data.hs.symptom;
				}else{
					
				}
			}
		});
	});
	
});