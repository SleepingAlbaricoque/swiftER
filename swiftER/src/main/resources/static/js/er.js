/**
 * 
 */
$(document).ready(function () {
	let er = [];
	let erChk = [];
	let erChk2 = [];
	let combined = [];
	let combinedFiltered = [];
	
	var container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
	var options = { //지도를 생성할 때 필요한 기본 옵션
		center: new kakao.maps.LatLng(33.450701, 126.570667), //지도의 중심좌표.
		level: 3 //지도의 레벨(확대, 축소 정도)
	};
	
	var map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴

	$('select[name=city]').on('change', function(){
		let city =$(this).val();
		subregion(city);
	});
	
	
	$('.subBtn').click(function(e){
		e.preventDefault();
		
		let city = $('select[name=city]').val();
		let town = $('select[name=town]').val();
		
		let jsonData = {
				"city": city,
				"town": town
		};
		
		console.log('data', jsonData);
		
		$.ajax({
			url: '/swiftER/er/erSearch',
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
				let er = items;
				let chk = false;
				
				$.ajax({
					url:"/swiftER/er/erSearch2",
					async:false,
					method:'POST',
					data:jsonData,
					success:function(data){
						let json = JSON.parse(data);
						let check = json.response.body.totalCount;
						if(check == 0){
							alert("검색한 결과가 없습니다, 다른 조건으로 검색하시길 바랍니다.");
							location.reload();
						}
						
						let items = json.response.body.items.item;
						erChk = items;
						
					}
					
				});
				
				$.ajax({
					url:"/swiftER/er/erSearch3",
					async:false,
					method:'POST',
					data:jsonData,
					success:function(data){
						let json = JSON.parse(data);
						let check = json.response.body.totalCount;
						if(check == 0){
							alert("검색한 결과가 없습니다, 다른 조건으로 검색하시길 바랍니다.");
							location.reload();
						}
						
						let items = json.response.body.items.item;
						erChk2 = items;
						
					}
					
				});

				let common = $('input[name=일반]').prop("checked");
				let medicine = $('input[name=내과]').prop("checked");
				let surgery = $('input[name=외과]').prop("checked");
				let neurosurgery = $('input[name=신경외과]').prop("checked");
				console.log('common', common);
				console.log('medicine', medicine);
				console.log('surgery', surgery);
				console.log('neurosurgery', neurosurgery);
				
				let ct = $('input[name=ct]').prop("checked");
				let mri = $('input[name=mri]').prop("checked");
				let camera = $('input[name=조영촬영기]').prop("checked");
				let respirator = $('input[name=인공호흡기]').prop("checked");
				let incubator = $('input[name=인큐베이터]').prop("checked");
				console.log('ct', ct);
				console.log('mri', mri);
				console.log('camera', camera);
				console.log('respirator', respirator);
				console.log('incubator', incubator);

				// 배열 합치기
				erChk.sort((a, b) => a.hpid.localeCompare(b.hpid));
				er.sort((a, b) => a.hpid.localeCompare(b.hpid));

				let mergedArray = erChk.map((obj, index) => {
					  return obj.hpid === er[index].hpid
					    ? Object.assign({}, obj, er[index])
					    : obj;
					});
				
				er.slice(erChk.length).forEach(obj => mergedArray.push(obj));
	
				let erMerged = mergedArray.map(obj => {
					  return {
					    dutyAddr: obj.dutyAddr,
					    dutyEmcls: obj.dutyEmcls,
					    dutyEmclsName: obj.dutyEmclsName,
					    dutyName: obj.dutyName,
					    dutyTel1: obj.dutyTel1,
					    dutyTel3: obj.dutyTel3,
					    hpid: obj.hpid,
					    hv11: obj.hv11,
					    hvctayn: obj.hvctayn,
					    hvmriayn: obj.hvmriayn,
					    hvangioayn: obj.hvangioayn,
					    hvventiayn: obj.hvventiayn,
					    wgs84Lat: obj.wgs84Lat,
					    wgs84Lon: obj.wgs84Lon
					  };
					});

				console.log('erMerged', erMerged);
				console.log('erChk2', erChk2);
				
				// 검색 옵션을 선택한 경우
				if(ct || mri || camera || respirator || incubator || 
						common || medicine || surgery || neurosurgery){
					
					combined = erMerged.map((erItem) => {
					  let matchingChk = erChk2.find((chkItem) => chkItem.dutyName === erItem.hpid);
					  if (matchingChk) {
					    return {
					      ...erItem,
					      ...matchingChk,
					      dutyName: erItem.dutyName
					    };
					  }
					  return null;
					}).filter(item => item !== null); // null 값을 제외하는 필터링 추가
					
					console.log('combined', combined);
					
					let filters = [];
					
					if (common) {
					  filters.push((arr) => {
					    console.log('mkioskTy1 filter arr:', arr); // arr 출력
					    return arr.filter(item => item['MKioskTy1'] == "Y");
					  });
					}
			        if (medicine) {
			          filters.push((arr) => arr.filter(item => item['MKioskTy2'] == "Y"));
			        }
			        if (surgery) {
			          filters.push((arr) => {
					    console.log('mkioskTy3 filter arr:', arr); // arr 출력
					    return arr.filter(item => item['MKioskTy3'] == "Y");
					  });
			        }
			        if (neurosurgery) {
			          filters.push((arr) => arr.filter(item => item['MKioskTy8'] == "Y"));
			        }
			        if (ct) {
			          filters.push((arr) => {
					    console.log('hvCtayn filter arr:', arr); // arr 출력
					    return arr.filter(item => item['hvctayn'] == "Y");
					  });
			        }
			        if (mri) {
			          filters.push((arr) => arr.filter(item => item['hvmriayn'] == "Y"));
			        }
			        if (camera) {
			          filters.push((arr) => arr.filter(item => item['hvangioayn'] == "Y"));
			        }
			        if (respirator) {
			          filters.push((arr) => arr.filter(item => item['hvventilayn'] == 'Y'));
			        }
			        if (incubator) {
			          filters.push((arr) => arr.filter(item => item['hv11'] > 0));
			        }
					
					let filtered = combined;


					if (filters.length > 0) {
					  filters.forEach(filterFn => {
					    filtered = filterFn(filtered);
					  });
					}
					
					if(filtered.length > 0){
						alert('선택하신 조건을 모두 충족하는 결과가 없습니다, 선택하신 지역을 기반으로 한 검색 결과 입니다.');
						combinedFiltered = combined;
					}else{
						combinedFiltered = filtered;	
					}
					
					console.log('filters', filters);
					console.log('filtered', filtered);
					
					
					
					console.log('combinedFiltered', combinedFiltered);

				}else{
					// 지역만 선택한 경우
					combinedFiltered = erMerged;
				}
				
				
				
				
				// 마커 이미지의 이미지 주소입니다
				var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png"; 
			    
			    // 마커 이미지의 이미지 크기 입니다
			    var imageSize = new kakao.maps.Size(24, 35); 
			    var LatLng = new kakao.maps.LatLng(combinedFiltered[0].wgs84Lat, combinedFiltered[0].wgs84Lon);
			    combinedFiltered.forEach(function (item) {
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
		            jibun.innerHTML = '연락처1 : '+item.dutyTel1+' 연락처2 : '+item.dutyTel3;
		            
		            var linkdiv = document.createElement('div');

		            var code = document.createElement('input');
		            code.type = 'hidden';
		            code.name = 'code';
		            code.value = item.hpid;
		            linkdiv.appendChild(code);
		            
		            var link = document.createElement('a');
		            link.className = 'link';
		            link.href = '/swiftER/er/erDetail';
		            link.innerHTML = '자세히 보기';
		            
		            linkdiv.appendChild(link);
		            linkdiv.appendChild(code);
		            desc.appendChild(ellipsis);
		            desc.appendChild(jibun);
		            desc.appendChild(linkdiv);
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
						console.log("marker : %o", marker);
					    overlay.setMap(map);
					    var hpid = $('input[name=code]').val();
						var hf = '/swiftER/er/erDetail?code='+hpid;
			            
						$('.link').attr("href", hf);
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
