package kr.co.swiftER.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.swiftER.entity.MemberEntity;

public interface MemberRepo extends JpaRepository<MemberEntity, String> {
	public int countByUid(String uid);
}

