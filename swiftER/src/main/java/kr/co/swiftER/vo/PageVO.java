package kr.co.swiftER.vo;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVO { // admin search 결과 출력하기 위해 만든 vo
	private int no;
	private int cate; // qna(3), community(1), er review(2) 세 개의 다른 종류의 글들을 같은 vo에 넣고 cate값으로 구별하기
	private String title;
	private String content;
	private double rank;
	private String rdate;
}
