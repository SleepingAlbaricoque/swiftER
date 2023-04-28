package kr.co.swiftER.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	public List<CommunityArticleVO> selectCaListAll(String uid) {
		
		return dao.selectCaListAll(uid);
	}

	/* 마이페이지 리뷰 리스트 불러오기 */
	public List<ERReviewVO> selectErReviewList(String uid) {

		return dao.selectErReviewList(uid);
	}

	public List<ERReviewVO> selectErListAll(String uid) {

		return dao.selectErListAll(uid);
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
		return dao.insertNote(hvo);
	}

	public List<MemberHistoryVO> selectHistories(MemberHistoryVO hvo) {
		return dao.selectHistories(hvo);
		
	}


}
