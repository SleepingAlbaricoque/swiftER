package kr.co.swiftER.autocomplete;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import kr.co.swiftER.vo.PageVO;

@Component
public class PageRankCalculator { 
	/**
	 * admin header에서 글 검색 시 검색 결과 출력 순위를 결정하는 알고리즘
	 * 사용자가 입력한 키워드가 글과 제목에서 얼마나 자주 등장하는지(frequency), 얼마나 최신 게시물인지(recency)를 기준으로 각 페이지에 점수 부여
	 */

	private Map<PageVO, Double> scores; // 각 페이지의 점수를 저장할 map 객체
	
	public Map<PageVO, Double> calculatePageRank(List<PageVO> pages, String keyword, int numIterations){
		scores = new HashMap<>();
		for(PageVO page : pages) {
			scores.put(page, 1.0);
		}
		
		for(int i =0; i < numIterations; i++) { // chatGPT에 따르면 100개 이하의 페이지에 대해서는 10번의 반복 정도도 충분하다고 함
			for(PageVO page : pages) {
				double score = 0.0;
				
				double keywordFrequency = calculateKeywordFrequency(page, keyword); // keyword frequency 계산
				double recencyScore = calculateRecencyScore(page); // recency 계산
				score = keywordFrequency * recencyScore;
				
				scores.put(page, score); // frequency, recency에 따라 계산한 점수를 map 객체에 저장
			}
			
			// 정규화 과정 - chatGPT 참조
			double sum = 0.0;
			for(double score : scores.values()) {
				sum += score;
			}
			
			for(PageVO page : pages) {
				double normalizedScore = scores.get(page) / sum;
				scores.put(page, normalizedScore);
			}
		}
		
		return scores;
	}
	
	// 각 페이지 제목과 본문에 키워드가 몇 번 등장하는지 계산하기 위한 메서드
	private double calculateKeywordFrequency(PageVO page, String keyword) { 
		int count = 0;
		String titleAndContent = page.getContent().toLowerCase() + " " + page.getTitle().toLowerCase();
		int index = titleAndContent.indexOf(keyword.toLowerCase());
		while(index != -1) {
			count++;
			index = titleAndContent.indexOf(keyword.toLowerCase(), index + 1);
		}
		return count;
	}
	
	// 각 페이지가 얼마나 최신글인지 계산하기 위한 메서드
	private double calculateRecencyScore(PageVO page) {
		LocalDate now = LocalDate.now();
		LocalDate rdate = LocalDate.parse(page.getRdate());
		
		long diffInDays = ChronoUnit.DAYS.between(rdate, now);
		return 1.0 / (diffInDays + 1);
	}
}
