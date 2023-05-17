$(document).ready(function () {
	let pharmacyList = [];
	
	var container = document.getElementById('search_map'); //지도를 담을 영역의 DOM 레퍼런스
	var options = { //지도를 생성할 때 필요한 기본 옵션
		center: new kakao.maps.LatLng(33.450701, 126.570667), //지도의 중심좌표.
		level: 3 //지도의 레벨(확대, 축소 정도)
	};
	
	var map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴

	$('.btnClose').click(function(e){
	    e.preventDefault();
	    $(this).toggleClass('on');
	    $('.selectBox').toggleClass('on');
	});
	
	$('.resetBtn').click(function(e){
		e.preventDefault;
		location.reload(true);
	});

	$('select[name=city]').on('change', function(){
		let city =$(this).val();
		subregion(city);
	});
	
	$('.subBtn').click(function(e){
		e.preventDefault();

		let hday = "";
		
		let city = $('select[name=city]').val();
		let town = $('select[name=town]').val();

		let chkHday = $('input[name=holyday]').prop("checked");
		console.log('chkHday', chkHday);
		if(chkHday){
			hday = $('input[name=holyday]').val();		
		}
			
		
		let jsonData = {
				"city": city,
				"town": town,
				"hday": hday
		};
		
		console.log('data', jsonData);
		
		$.ajax({
			url: '/swiftER/pharmacy/pharmacySearch',
			method: 'POST',
			data: jsonData,
			success: function(data){
				let json = JSON.parse(data);
				let check = json.response.body.totalCount;
				if(check == 0){
					alert("검색한 결과가 없습니다, 다른 조건으로 검색하시길 바랍니다.");
					location.reload();
				}
				
				let items = json.response.body.items.item;
				pharmacyList = items;
				console.log('pharmacyList', pharmacyList);
				
				alert('선택하신 지역을 기반으로 한 결과가 총 '+ check + '건 검색되었습니다.');
				
				// 마커 이미지의 이미지 주소입니다
				var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png"; 
			    
			    // 마커 이미지의 이미지 크기 입니다
			    var imageSize = new kakao.maps.Size(24, 35); 
			    var LatLng = new kakao.maps.LatLng(pharmacyList[0].wgs84Lat, pharmacyList[0].wgs84Lon);
			    pharmacyList.forEach(function (item) {
				    // 마커 이미지를 생성합니다    
				    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize); 
				    
				    // 마커를 생성합니다
				    var marker = new kakao.maps.Marker({
				        map: map, // 마커를 표시할 지도
				        position: new kakao.maps.LatLng(item.wgs84Lat, item.wgs84Lon),
				        image : markerImage // 마커 이미지 
				    });
				    
				 	//이동할 위도 경도 위치를 생성합니다 
				    var moveLatLon = LatLng;
				    // 지도 중심을 부드럽게 이동시킵니다
				    // 만약 이동할 거리가 지도 화면보다 크면 부드러운 효과 없이 이동합니다
				    map.panTo(moveLatLon);
				    
					// 마커를 중심으로 커스텀 오버레이를 표시하기위해 CSS를 이용해 위치를 설정했습니다
					var overlay = new kakao.maps.CustomOverlay({
					    position: new kakao.maps.LatLng(item.wgs84Lat, item.wgs84Lon)
					});
				    
				    // widowinfo 태그 생성
		            var content = document.createElement('div');
		            content.className = 'wrap';

		            var info = document.createElement('div');
		            info.className = 'info';

		            var title = document.createElement('div');
		            title.className = 'title';
		            title.innerHTML = item.dutyName;
		            
		            var close = document.createElement('div');
		            close.className = 'close';
		            close.onclick = function() {overlay.setMap(null);};
		            close.title = '닫기';
		            
		            var body = document.createElement('div');
		            body.className = 'body';
		            
		            var desc = document.createElement('div');
		            desc.className = 'desc';
		            
		            var ellipsis = document.createElement('div');
		            ellipsis.className = 'ellipsis';
		            ellipsis.innerHTML = item.dutyAddr;
		            
		            var jibun = document.createElement('div');
		            jibun.className = 'jibun';
		            jibun.innerHTML = '연락처1 : '+item.dutyTel1;
		            
		            var timediv = document.createElement('div');
		            
		            var timet = document.createElement('div');
		            timet.className = 'ellipsis';
		            timet.innerHTML = '운영시간';

					var time = document.createElement('div');
					time.className = 'time';
					time.innerHTML = '월 '+ item.dutyTime1s.substr(0, 2) + '시 ~ '+ item.dutyTime1c.toString().substr(0, 2) + '시';
					
					
					var time2 = document.createElement('div');
					time2.className = 'time';
					time2.innerHTML = '화 '+ item.dutyTime2s.substr(0, 2) + '시 ~ '+ item.dutyTime2c.toString().substr(0, 2) + '시';
					
					var time3 = document.createElement('div');
					time3.className = 'time';
					time3.innerHTML = '수 '+ item.dutyTime3s.substr(0, 2) + '시 ~ '+ item.dutyTime3c.toString().substr(0, 2) + '시';
					
					var time4 = document.createElement('div');
					time4.className = 'time';
					time4.innerHTML = '목 '+ item.dutyTime4s.substr(0, 2) + '시 ~ '+ item.dutyTime4c.toString().substr(0, 2) + '시';
					
					var time5 = document.createElement('div');
					time5.className = 'time';
					time5.innerHTML = '금 '+ item.dutyTime5s.substr(0, 2)+ '시 ~ '+ item.dutyTime5c.toString().substr(0, 2) + '시';
		            
		            timediv.appendChild(timet);
		            timediv.appendChild(time);
		            timediv.appendChild(time2);
		            timediv.appendChild(time3);
		            timediv.appendChild(time4);
		            timediv.appendChild(time5);
		            desc.appendChild(ellipsis);
		            desc.appendChild(jibun);
		            desc.appendChild(timediv);
		            body.appendChild(desc);
		            title.appendChild(close);
		            info.appendChild(title);
		            info.appendChild(body);
		            content.appendChild(info);
				
		            console.log(content)
		            // widowinfo 태그 생성 끝
		            
					overlay.setContent(content);
		            //overlay.setMap(map);
		            
					// 마커를 클릭했을 때 커스텀 오버레이를 표시합니다
					kakao.maps.event.addListener(marker, 'click', function() {
					    overlay.setMap(map);
					    
					});
			    });
			}
		});
	});
});	
function subregion(city){
	let jsonData = {"city":city}
	$.ajax({
		url:'/swiftER/er/subregion',
		method:'POST',
		dataType:'json',
		data:jsonData,
		success:(data)=>{
	    	let subregion = $('select[name=town]');
	    	subregion.children().remove();
	    	let tag = `<option value="">전체</option>`;
	    	data.forEach(async data => {
	        	tag += `<option th:value="${data.subregion}" th:text="${data.subregion}">${data.subregion}</option>`;
	    	});
	    subregion.append(tag);
		}
	});
}