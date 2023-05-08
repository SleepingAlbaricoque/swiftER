package kr.co.swiftER.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.entity.MemberEntity;
import kr.co.swiftER.entity.MessageEntity;

@Repository
public interface MessageRepo extends JpaRepository<MessageEntity, Long>{
	List<MessageEntity> findByReceiverOrSender(String receiver, String sender);
	List<MessageEntity> findByReceiverAndSenderOrSenderAndReceiverOrderByRdateAsc(String receiver, String sender, String recevier2, String sender2);
}
