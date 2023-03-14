package kr.co.swiftER.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.swiftER.dao.CSDAO;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.FileVO;

@Service
public class CSService {

	@Autowired
	private CSDAO dao;
	
	public int insertArticle(CSQuestionsVO article) {
		// 글 등록
		int result = dao.insertArticle(article);
		
		// 파일 업로드위한 FileVO 생성
		FileVO fvo = uploadFile(article);
		
		// FileVO를 file 테이블에 업로드
		if(fvo != null)
			dao.insertFile(fvo);
		
		return result;
	}
	
	
	// 파일 업로드
	
	// applicaton.properties에서 설정한 파일 저장 경로 주입받기
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	
	public FileVO uploadFile(CSQuestionsVO article) {
		// 첨부 파일 정보 가져오기
		MultipartFile file = article.getFname();
		FileVO fvo = null;
		
		if(!file.isEmpty()) {
			// application.properties에서 설정한 파일 저장 경로의 시스템 경로 구하기
			String path = new File(uploadPath).getAbsolutePath();
			
			// 새 파일명 생성
			String oriName = file.getOriginalFilename();
			String ext = oriName.substring(oriName.lastIndexOf(".")); // 확장자
			String newName = UUID.randomUUID().toString() + ext;
			
			// 파일 저장
			try {
				file.transferTo(new File(path, newName));
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
			fvo = new FileVO().builder().parent(article.getNo()).oriName(oriName).newName(newName).build();
		}
		return fvo;
	}
}
