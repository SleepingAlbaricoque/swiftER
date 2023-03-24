package kr.co.swiftER.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co.swiftER.dao.MemberDAO;
import kr.co.swiftER.exceptions.CustomErrorCode;
import kr.co.swiftER.exceptions.CustomException;
import kr.co.swiftER.repo.MemberRepo;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.FileDoctorVO;
import kr.co.swiftER.vo.FileVO;
import kr.co.swiftER.vo.MemberDoctorVO;
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

	/* 회원가입 */
	public int insertMember(MemberVO vo) {
		vo.setPass(passwordEncoder.encode(vo.getPass()));
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

	/* 내가 작성한 글 갯수 */
	public int countCa(String uid) {
		return dao.countCa(uid);
	}
	
	/* 의사 회원가입(이미지 제출 제외) */
	public int insertMemberDoctor(MemberDoctorVO vo) {
		int result = dao.insertMemberDoctor(vo);
			
		return result;
		
	}
}
