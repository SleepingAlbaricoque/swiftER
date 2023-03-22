package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.MemberVO;

@Mapper
@Repository
public interface AdminDAO {

	public List<AdminMemberSearchVO> selectMembers(int start);
}
