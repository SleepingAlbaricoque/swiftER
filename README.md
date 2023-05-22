# swiftER
![swifter_logo_horizontal](https://github.com/SleepingAlbaricoque/swiftER/assets/111475431/84814950-3949-41ba-a856-4621e8359afe)

앉은 자리에서 바로 질병/병원 정보를 확인하고 다른 유저들과 정보를 교환하는 건강 포털

## 프로젝트 한 눈에 보기
[프로젝트 시연 영상 모음](https://www.youtube.com/playlist?list=PLxK0fGN5LM-VbruXG3z3G9Nr3PblKivOu)


[배포 주소](http://52.79.139.8:8181/swiftER/)  (관리자 아이디: et009153, 비밀번호: !q2w3e4r)


[프로젝트 발표 보고서](https://drive.google.com/file/d/1sTO4pviG9ELNFaxlFz8cJtF_9sRG20bJ/view?usp=share_link)

## 프로젝트 소개
swiftER은 `swift`와 `ER`를 합성한 말로, 신속한 응급실 찾기 서비스를 제공하겠다는 목표를 형상화하고 있습니다. swiftER 프로젝트는 아플 때 증상을 하나씩 구글에 검색하여 직접 질병을 추측해야 하는 불편함을 개선하겠다는 작은 아이디어에서 출발하였습니다. 자신이 가진 여러 증상을 선택해 관련 질병 및 진료과를 볼 수 있는 검색 서비스를 넘어, 사용자들이 서로 다대다 및 일대일 소통을 할 수 있는 커뮤니티를 제공하여 swiftER에서 정보의 공유 및 재생산이 가능토록 하였습니다. 

### 사용한 주요 기술들
Java17, Spring Boot 3.0.4, Apache Tomcat 9.0, MySQL 8.0, MariaDB 5.5.68, EC2


### 사용한 주요 API/라이브러리
[응급실 공공 데이터](https://www.data.go.kr/data/15000563/openapi.do) - 전국 응급실 현황 조회 

[약국 공공 데이터](https://www.data.go.kr/data/15000576/openapi.do) - 전국 약국 위치 및 공휴일 오픈 여부 조회

[Kakao Map API](https://apis.map.kakao.com/web/) - 검색 결과 맵에 마커 표시

[jsoup](https://jsoup.org/) - 사용자 input sanitize

[CKEditor](https://ckeditor.com/) - 게시글 작성(사진 첨부 기능 미구현)



### 정보 구조
![image](https://github.com/SleepingAlbaricoque/swiftER/assets/111475431/81d4f5ed-0978-4b37-84b5-a636f953ad03)

### ERD
![image](https://github.com/SleepingAlbaricoque/swiftER/assets/111475431/4c9c094a-f618-4bce-81f1-062d5d38325e)


## 역할
### 회원 : [김현준](https://github.com/hyun7494)
  
  프로젝트 기획 및 자료조사
  
  회원가입 및 정보 화면 구현
  
  일반/의사 회원 가입 기능 구현
  
  회원 가입 정보 인증 기능 구현
  
  회원 정보 찾기, 마이페이지 기능 구현
  
  Spring Security 설정 및 적용
  
  이메일 인증

### 커뮤니티 : [최아영](https://github.com/Chocokano)

  커뮤니티 화면 구현
  
  커뮤니티 게시글 CRUD 기능 구현
  
  게시글 검색 기능 구현

### 증상 검색 : [공민혁](https://github.com/KongMinHyeok)
  증상 검색, 결과 화면 구현
  
  증상 검색 기능 구현

  증상 검색 결과 기능 구현


### 응급실 검색/약국 검색 : [홍민준](https://github.com/Hongscoding)
  응급실 검색 및 결과, 관리자 화면 구현
  
  공공 데이터 실시간 검색 및 파싱
  
  카카오 맵 API: 응급실 위치/상세 정보
  
  응급실 상세 페이지 기능 구현
  
  응급실 리뷰 작성/수정 기능 구현
  
  약국 검색 화면 구현
  
  약국 검색 기능 구현


### 고객센터/관리자/메세징 : [조수빈](https://github.com/SleepingAlbaricoque)
  메인 / 고객센터 / 관리자 화면 구현
  
  ERD 설계
  
  고객센터 기능 구현
  
  관리자 기능 구현
  
  실시간 메시징 기능 구현
  
  파이썬을 이용해 증상 정보 크롤링
  
  검색 시 자동 완성 기능 구현
  
  관리자 검색 결과 알고리즘 설계
  
  관리자 현황 분석 보드 기능 구현
  
  CKEditor로 글 작성/수정 및 jsoup를 활용해 입력값 보안 처리
  
  발표 PPT 작성 및 


