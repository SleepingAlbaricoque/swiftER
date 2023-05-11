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
  			
  			let maping = detail.dutyMapimg;
  			let empty = "해당 내용을 제공받지 못했습니다.";
  			
  			if(maping != null && maping !== ''){
  				info.innerText = '• '+maping;
			}else{
  				info.innerText = '• '+empty;
			}
  			
  			
  			tel.innerHTML = '<i class="fas fa-home"></i> ';
  			tel.innerHTML += ' 응급실 전화1 : '+detail.dutyTel3;
			etc1.innerHTML = '<i class="fas fa-home"></i> ';
  			etc1.innerHTML += ' 응급실 전화2 : '+detail.dutyTel1;
  			
  			var input = document.createElement('input'); 
			input.type = 'hidden'; 
			input.name = 'hosName'; 
			input.value = detail.dutyName;
			console.log(input);
			hide.appendChild(input);
			console.log(hide);
			
			$(document).on('click', '.btnPositive', function(e){
				e.preventDefault();
				const formtag = document.getElementById('form');
	       	    let rating = $('input[name=rating]').val();
				
				// 폼 전송 처리 등 수행
				if(rating != ''){
				 	formtag.submit();
				}else{
				 	alert('별점을 입력해 주십시오.');
				}
				
			});
			
		}
		
	});

});


