package kr.co.swiftER.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.swiftER.controller.MemberController;
import kr.co.swiftER.dao.MemberDAO;
import kr.co.swiftER.exceptions.CustomErrorCode;
import kr.co.swiftER.exceptions.CustomException;
import kr.co.swiftER.repo.MemberRepo;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.MemberDoctorVO;
import kr.co.swiftER.vo.MemberHistoryVO;
import kr.co.swiftER.vo.MemberTermsVO;
import kr.co.swiftER.vo.MemberVO;

@Service
public class MemberService {
	
	@Autowired MemberDAO dao;
	@Autowired MemberRepo repo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	/* 회원 약관 불러오기 */
	public MemberTermsVO selectTerms() {
		return dao.selectTerms();
	}
	
	/* 회원가입 유효성 검사 */
	public int countUid(String uid) {
		return repo.countByUid(uid);
	}
	
	/* 회원가입 유효성 검사 nickname */
	public int countNick(String nickname) {
		return repo.countByNickname(nickname);
	}

	/* 회원가입 */
	public int insertMember(MemberVO vo) {
		vo.setPass(passwordEncoder.encode(vo.getPass2()));
		int result = dao.insertMember(vo);
		return result;
	}
	
	/* 비밀번호 수정 */
	public int updatePass(String pass2, String uid) {
		String pass = passwordEncoder.encode(pass2);
		int result = dao.updatePass(pass, uid);
		return result;
	}

	/* 마이페이지 회원정보 */
	public MemberVO selectMember(String uid) {
		return dao.selectMember(uid);
	}
	
	/* 마이페이지 게시판 리스트 불러오기 */
	public List<CommunityArticleVO> selectCaList(String uid) {
		return dao.selectCaList(uid);
	}
	
	/* 마이페이지 게시판 리스트 전체 불러오기 */
	public List<CommunityArticleVO> selectCaListAll(String uid, int start) {
		
		return dao.selectCaListAll(uid, start);
	}

	/* 마이페이지 리뷰 리스트 불러오기 */
	public List<ERReviewVO> selectErReviewList(String uid) {

		return dao.selectErReviewList(uid);
	}

	public List<ERReviewVO> selectErListAll(String uid, int start) {

		return dao.selectErListAll(uid, start);
	}

	/* 내가 작성한 글 갯수 */
	public int countCa(String uid) {
		return dao.countCa(uid);
	}

	public int deleteMember(String uid) {
		int result = dao.deleteMember(uid);
		
		return result;
	}

	public int checkGrade(String uid) {
		int grade = dao.checkGrade(uid);
		return grade;
	}

	public void deleteDoctor(String uid) {
		dao.deleteDoctor(uid);
	}
	
	// 회원 확인
	public int checkMember(String uid) {
		int result = dao.checkMember(uid);
		return result;
	}

	// 아이디 찾기 정보 출력
	public MemberVO findId(String name, String email) {
		return dao.findId(name, email);
	}

	// 마이페이지 의사회원 정보
	public MemberDoctorVO selectDoctor(String uid) {
		return dao.selectDoctor(uid);
	}

	// 일반회원 수정
	public int changeNor(MemberVO vo) {
		vo.setPass(passwordEncoder.encode(vo.getPass()));
		return dao.changeNor(vo);
	}

	public int changeDoc(MemberDoctorVO dvo) {
		return dao.changeDoc(dvo);
	}

	// 파일 업로드
	// applicaton.properties에서 설정한 파일 저장 경로 주입받기
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	// 의사회원 가입
	public MemberDoctorVO insertMemberDoctor(MemberDoctorVO dvo, MultipartFile file) {
		if(!file.isEmpty()) {
			// application.properties에서 설정한 파일 저장 경로의 시스템 경로 구하기
			String path = new File(uploadPath).getAbsolutePath();
			
			// 새 파일명 생성
			String oriName = file.getOriginalFilename();
			String ext = oriName.substring(oriName.lastIndexOf(".")); // 확장자
			String newName = UUID.randomUUID().toString() + ext;
			
			// 업로드 파일 확장자 제한하기; 그렇지 않으면 web shell이나 악성 파일 업로드할 가능성이 있음
			String[] safeExts = {".jpg", ".jpeg", ".bmp", ".png", ".gif"};
			if(!Arrays.asList(safeExts).contains(ext)) {
				throw new CustomException(CustomErrorCode.WRONG_EXT_ERROR);
			}
			
			// 파일 저장
			try {
				file.transferTo(new File(path, newName));
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
			
			String member_uid = dvo.getMember_uid();
			String kind = dvo.getKind();
			String specialty = dvo.getSpecialty();
			dvo = new MemberDoctorVO().builder().member_uid(member_uid).kind(kind).specialty(specialty).cert_oriName(oriName).cert_newName(newName).build();
			dao.insertMemberDoctor(dvo);
		}
		return dvo;
	}

	// 마이페이지 간편 이력 부분(post)
	public int insertNote(MemberHistoryVO hvo) {
		dao.insertNote(hvo);
		return 1;
	}

	public List<MemberHistoryVO> selectHistories(MemberHistoryVO hvo) {
		return dao.selectHistories(hvo);
		
	}

	public int checkHistory(MemberHistoryVO hvo) {
		return dao.checkHistory(hvo);
	}

	public int updateNote(MemberHistoryVO hvo) {
		dao.updateNote(hvo);
		return 2;
	}
	//
	
	/* 페이징을 위해 Qna 카테고리의 게시물 총 갯수 */
	public int selectCountArticleList(String uid) {
		return dao.selectCountArticleList(uid);
	}
	
	
	// 페이징
	/////////////////////////////////////////////////////////
	
	
	
	/* 현재 페이지 번호 */
	public int getCurrentPage(String pg) {
		
		int currentPage = 1;
		
		if(pg != null) {
			currentPage= Integer.parseInt(pg);
		}
		
		return currentPage;
		
	}
	
	/* 페이지 시작값 */
    public int getLimitStart(int currentPage) {
    	
        return (currentPage - 1) * 10;
        
    }
    
    /* 마지막 페이지 번호 */
    public int getLastPageNum(int total) {
    	
    	int lastPageNum = 0;
    	
    	if(total % 10 == 0) {
    		lastPageNum = total / 10;
    	}else {
    		lastPageNum = total / 10 + 1;
    	}
    	
    	return lastPageNum;
    	
    }
    
    /* 페이지 시작 번호 */
    public int getPageStartNum(int total, int start) {
    	
    	return total - start;
    	
    }
    
    /* 페이지 그룹 */
	public int[] getPageGroup(int currentPage, int lastPageNum) {
		int groupCurrent = (int) Math.ceil(currentPage / 10.0);
		int groupStart = (groupCurrent - 1) * 10 + 1;
		int groupEnd = groupCurrent * 10;
		
		if(groupEnd > lastPageNum) {
		
			groupEnd = lastPageNum;
			
		}
		
		int[] groups = {groupStart, groupEnd};
		
		return groups;
	}

	public int selectCountReviewList(String uid) {
		return dao.selectCountReviewList(uid);
	}
	
	/* 카카오 */
	public String getKaKaoAccessToken(String code){
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=b59782950c07b248fe9ef97eacdd98a1"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:8181/swiftER/kakao/kakaoAuth"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

	/* 토큰으로 사용자 정보 조회 */
	public int createKakaoUser(String token) throws Exception {

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        int CBU = 0; 
        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            Long id = element.getAsJsonObject().get("id").getAsLong();
            String uid = Long.toString(id);
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            System.out.println("id : " + id);
            System.out.println("email : " + email);

            br.close();
            System.out.println("countByUid : " + repo.countByUid(uid));
            CBU = repo.countByUid(uid);
            	
        } catch (IOException e) {
            e.printStackTrace();
        }
       return CBU;
    }
	
}
