package kr.co.swiftER.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.AdminDAO;
import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.FileVO;
import kr.co.swiftER.vo.MemberVO;

@Service
public class AdminService {

	@Autowired
	private AdminDAO dao;
	
	// 전체 회원의 수
	public List<AdminMemberSearchVO> selectMembers(int start, int isDoc){
		return dao.selectMembers(start, isDoc);
	}
	
	// 인증 승인 요청한 의사의 수
	public int selectDocCountTotal() {
		return dao.selectDocCountTotal();
	}
	
	// 회원 정보 조회(한 명)
	public AdminMemberSearchVO selectMember(String uid) {
		return dao.selectMember(uid);
	}
	
	// 회원 영구 정지
	public int banMember(String uid) {
		if(dao.banMember(uid) == 1)
			return 1;
		
		return 0;
	}
	
	// 의사 회원 uid값으로 첨부한 면허증 사진 정보 가져오기
	public FileVO selectDocCert(String uid) {
		return dao.selectDocCert(uid);
	}
	
	
	// 페이징
	// 글 총 갯수(total)
	public int selectCountTotal() {
		return dao.selectCountTotal();
	}
	
	// 현재 페이지 버튼 번호
	public int getCurrentPage(String pg) {
		int currentPage = 1;
		
		if(pg != null)
			currentPage = Integer.parseInt(pg);
		
		return currentPage;
	}
	
	// 현재 페이지 버튼의 페이지(article) 시작값
	public int getLimitStart(int currentPage, int articlesPerPage) {
		return (currentPage - 1) * articlesPerPage;
	}
	
	// 마지막 버튼 페이지 번호
	public int getLastPageNum(int total, int articlesPerPage) {
		int lastPageNum = 0;
		
		if(total % articlesPerPage == 0)
			lastPageNum = total /articlesPerPage;
		else
			lastPageNum = total / articlesPerPage + 1;
		
		return lastPageNum;
	}
	
	// 페이지 시작 번호
	public int getPageStartNum(int total, int start) {
		return total - start;
	}
	
	// 페이지 그룹
	public int[] getPageGroup(int currentPage, int lastPageNum, int articlesPerPage) {
		int groupCurrent = (int) Math.ceil(currentPage/Double.valueOf(articlesPerPage)); // 현재 페이지 번호 그룹
		int groupStart = (groupCurrent - 1)*articlesPerPage + 1; // 현재 그룹의 시작 버튼 번호
		int groupEnd = groupCurrent * articlesPerPage; // 현재 그룹의 마지막 버튼 번호
		
		if(groupEnd > lastPageNum)
			groupEnd = lastPageNum;
		
		int[] groups = {groupStart, groupEnd};
		return groups;
	}
}
