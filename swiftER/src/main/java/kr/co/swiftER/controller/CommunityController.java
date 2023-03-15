package kr.co.swiftER.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.swiftER.service.CommunityService;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.CommunityCateVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CommunityController {

	@Autowired
	private CommunityService service;
	
	 /* Free */
    @GetMapping(value = {"community/freeList"})
    public String FreeList(Model model, String pg, @RequestParam(value="cateCode", defaultValue = "10") String cateCode, String code){
    	
    	 pg = (pg == null) ? "1" : pg;

    	 int currentPage = service.getCurrentPage(pg);
         int start = service.getLimitStart(currentPage);
         long total = service.getTotalCount(cateCode);
         int lastPage = service.getLastPageNum(total);
         int pageStartNum = service.getPageStartNum(total, start);
         int groups[] = service.getPageGroup(currentPage, lastPage);

         CommunityCateVO ccv = service.selectCate(cateCode);
         List<CommunityArticleVO> cates = service.selectFreeArticles(start, cateCode);
         
         
         /*log.info("currentPage : " + currentPage);
         log.info("lastPage : " + lastPage);
         log.info("pageStartNum : " + pageStartNum);
         log.info("groups : " + groups);
         log.info("ccv : " + ccv);
         log.info("cates : " + cates);
         log.info("cateCode : " + cateCode);
         */
         
         model.addAttribute("currentPage", currentPage);
         model.addAttribute("lastPage", lastPage);
         model.addAttribute("pageStartNum", pageStartNum);
         model.addAttribute("groups", groups);
         model.addAttribute("ccv", ccv);
         model.addAttribute("cates", cates);
         model.addAttribute("cateCode", cateCode);
         
        return "community/freeList";
    }
    
    @GetMapping("community/freeView")
	public String FreeView() {
		return "community/freeView";
	}
    
    @GetMapping("community/freeWrite")
	public String FreeWrite() {
		return "community/freeWrite";
	}
    
    /* Qna */
    @GetMapping("community/qnaList")
	public String QnaList() {
		return "community/qnaList";
	}
    
    @GetMapping("community/qnaView")
	public String QnaView() {
		return "community/qnaView";
	}
    
    @GetMapping("community/qnaWrite")
	public String QnaWrite() {
		return "community/qnaWrite";
	}
    
    @GetMapping("community/qnaComment")
	public String QnaComment() {
		return "community/qnaComment";
	}
    
    /* Mytown */
    @GetMapping("community/mytownList")
	public String MytownList() {
		return "community/mytownList";
	}
    
    @GetMapping("community/mytownView")
	public String MytownView() {
		return "community/mytownView";
	}
    
    @GetMapping("community/mytownWrite")
	public String MytownWrite() {
		return "community/mytownWrite";
	}
}
