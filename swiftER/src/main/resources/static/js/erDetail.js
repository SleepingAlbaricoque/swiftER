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
  			
  			tel.appendChild = '<i class="fas fa-home"></i>';
  			tel.innerText = '응급실 전화 : '+detail.dutyTel3;
  			etc1.appendChild = '<i class="fas fa-home"></i>';
  			etc1.innerText = '내용이 없습니다.';
  			
  			var input   = document.createElement('input'); 
			input.type   = 'hidden'; 
			input.name  = 'hosName'; 
			input.value  = detail.dutyName;
			console.log(input);
			hide.appendChild = input;
			console.log(hide);
				
		}
		
	});



});