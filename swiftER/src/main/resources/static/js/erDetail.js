$(document).ready(function () {
	let code = $('input[name=hpid]').val();
	let detail = [];

	let jsonData = {
			"code": code
	};

	console.log('jsonData', jsonData);
	$.ajax({
		url:"/swiftER/er/erDetailInfo",
		method:'POST',
		data:jsonData,
		success:function(data){
			console.log('data : ', data);
			let json = JSON.parse(data);
			let check = json.response.body.totalCount;
			if(check == 0){
				alert("검색한 결과가 없습니다.");
				location.href="/swiftER/er/er";
			}
			
			let items = json.response.body.items.item;
			detail = items;
			console.log('detail',detail);
			
			let name = document.getElementById('hospi_name');
			let addr = document.getElementById('addr');
			let dep = document.getElementById('dep');
			let info = document.getElementById('etc');
			let tel = document.getElementById('tel');
			let etc1 = document.getElementById('etc1');
			let hide = document.getElementById('hide');
			
  			name.innerText = ''+detail.dutyName;
  			addr.innerText = '• '+detail.dutyAddr;
  			dep.innerText = '• '+detail.dgidIdName;
  			info.innerText = '• '+detail.dutyMapimg;
  			
  			tel.innerHTML = '<i class="fas fa-home"></i> ';
  			tel.innerHTML += ' 응급실 전화 : '+detail.dutyTel3;
			etc1.innerHTML = '<i class="fas fa-home"></i> ';
  			etc1.innerHTML += ' 내용이 없습니다.(진료시간 출력)';
  			
  			var input = document.createElement('input'); 
			input.type = 'hidden'; 
			input.name = 'hosName'; 
			input.value = detail.dutyName;
			console.log(input);
			hide.appendChild(input);
			console.log(hide);
			
			$('.btnPositive').click(function(e){
				e.preventDefault();
				const form = document.querySelector('form');
				console.log(form);
				  let uid = $('input[name=uid]');
				  
				  // memberentity에 uid가 널이 아닌지 체크하고 필요한 처리 수행
				  if (uid !== null) {
				    // 폼 전송 처리 등 수행
				    form.submit();
				  } else {
				    // uid가 널이면 알림창 띄우기 등 처리
				    if(alert('로그인이 필요합니다. 로그인 화면으로 이동하시겠습니까?')){
					    //location.href('/swiftER/member/login');
					}else{
						location.reload();
					}
				    
				  }
			});
				
		}
		
	});
	
	

});