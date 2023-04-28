package kr.co.swiftER.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="member")
public class MemberEntity {
	@Id
	private String uid;
	private String pass;
	private String name;
	private String nickname;
	private String birth;
	private String email;
	private String contact;
	private String zip;
	private String addr1;
	private String addr2;
	private int grade;
	private String regip;
	private String rdate;
	private String wdate;
	
	@OneToMany(mappedBy = "receiver")
	private List<MessageEntity> receivedMessages;
	
	@OneToMany(mappedBy = "sender")
	private List<MessageEntity> sentMessages;
}
