package kr.co.swiftER.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.entity.MemberEntity;
import kr.co.swiftER.security.MyUserDetails;
import kr.co.swiftER.service.CommunityService;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.CommunityCateVO;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Log4j2
@Controller
public class CommunityController {

	@Autowired
	private CommunityService service;
	
	/* Free */
    @GetMapping(value = {"community/freeList"})
    public String FreeList(Model model, String pg, @RequestParam(value="cateCode", defaultValue = "10") String cateCode,@RequestParam(value="regionCode", defaultValue = "0") String regionCode, String code,
    		@RequestParam(value="keyword", defaultValue = "") String keyword, String title, String no){
    	
    	 pg = (pg == null) ? "1" : pg;

    	 int currentPage = service.getCurrentPage(pg);
         int start = service.getLimitStart(currentPage);
         long total = service.getTotalCount(cateCode);
         int lastPage = service.getLastPageNum(total);
         int pageStartNum = service.getPageStartNum(total, start);
         int groups[] = service.getPageGroup(currentPage, lastPage);
         
         CommunityCateVO ccv = service.selectCate(cateCode);
         
         List<CommunityArticleVO> cates = null;
         
         if(keyword.equals("")) {
        	 cates = service.selectFreeArticles(start, cateCode, regionCode);        	 
         }else {
        	 cates = service.selectFindTitleSearch(start, title, cateCode, keyword, regionCode);
         }
         
         //List<CommunityArticleVO> cates = service.selectFreeArticles(start, cateCode);
         //List<CommunityArticleVO> serchfree = service.selectFindTitleSearch(title, cateCode, keyword);
         
         //log.info("currentPage : " + currentPage);
         //log.info("lastPage : " + lastPage);
         //log.info("pageStartNum : " + pageStartNum);
         //log.info("groups : " + groups);
         //log.info("ccv : " + ccv);
         //log.info("cates : " + cates);
         //log.info("cateCode : " + cateCode);
         
         model.addAttribute("currentPage", currentPage);
         model.addAttribute("lastPage", lastPage);
         model.addAttribute("pageStartNum", pageStartNum);
         model.addAttribute("groups", groups);
         model.addAttribute("ccv", ccv);
         model.addAttribute("cates", cates);
         model.addAttribute("cateCode", cateCode);
         model.addAttribute("regionCode", regionCode);
         model.addAttribute("keyword", keyword);
         
        return "community/freeList";
    }

    @GetMapping(value = {"community/freeView"})
    public String FreeView(Model model, String pg, String no, String cateCode, String parent, String comments){
    	
    	 pg = (pg == null) ? "1" : pg;

    	 int currentPage = service.getCurrentPage(pg);
         int start = service.getLimitStart(currentPage);
         long total = service.getCommentTotalCount(parent);
         int lastPage = service.getLastPageNum(total);
         int pageStartNum = service.getPageStartNum(total, start);
         int groups[] = service.getPageGroup(currentPage, lastPage);
    	
    	 CommunityCateVO ccv = service.selectCate(cateCode);
         CommunityArticleVO vo = service.selectFreeArticle(no);
         List<CommunityArticleVO> cm = service.selectComments(start, parent);
         service.updateArticleView(no);
         
         model.addAttribute("currentPage", currentPage);
         model.addAttribute("lastPage", lastPage);
         model.addAttribute("pageStartNum", pageStartNum);
         model.addAttribute("groups", groups);
         model.addAttribute("ccv", ccv);
         model.addAttribute("vo", vo);
         model.addAttribute("cm", cm);
         model.addAttribute("cateCode", cateCode);
         model.addAttribute("parent", parent);
         model.addAttribute("no", no);
         
         return "community/freeView";
    }
    
    @PostMapping(value = {"community/freeView"})
    public String FreeComment(Model model, HttpServletRequest req, @AuthenticationPrincipal MyUserDetails myUser, CommunityArticleVO vo,
    		Integer cateCode, Integer regionCode, Integer parent, String no , String comment) {
    	
    	MemberEntity member = myUser.getMember();
    	
    	 //log.info("no : "+ no);
    	 
    	 vo.setMember_uid(member.getUid());
         vo.setCateCode(String.valueOf(cateCode));
         vo.setRegionCode(String.valueOf(regionCode));
         vo.setParent(String.valueOf(parent));
         vo.setRegip(req.getRemoteAddr());
         
         service.insertComment(vo);
         service.updateComments(parent);
         
         return "redirect:/community/freeView?cateCode="+cateCode+"&no="+no+"&parent="+parent+"&comment="+comment;
    }
    
    @GetMapping(value = {"community/freeWrite"})
    public String FreeWrite(Model model, @RequestParam("cateCode") String cateCode){

        model.addAttribute("cateCode", cateCode);

        return "community/freeWrite";
    }
    @PostMapping(value = {"community/freeWrite"})
    public String FreeWrite(Model model,HttpServletRequest req, @AuthenticationPrincipal MyUserDetails myUser, CommunityArticleVO vo,
    		Integer cateCode,Integer regionCode){
    	
        MemberEntity member = myUser.getMember();
        
        vo.setMember_uid(member.getUid());
        vo.setCateCode(String.valueOf(cateCode));
        vo.setRegionCode(String.valueOf(regionCode));
        vo.setRegip(req.getRemoteAddr());
        service.insertFreeArticle(vo);
        
        return "redirect:/community/freeList?cateCode="+cateCode;
    }
    
    /* Qna */
    @GetMapping(value = {"community/qnaView"})
    public String QnaView(Model model, String no, String cateCode , String parent, String comments){
    	
    	 CommunityCateVO ccv = service.selectCate(cateCode);
         CommunityArticleVO vo = service.selectFreeArticle(no);
         List<CommunityArticleVO> cm = service.selectQnaComments(parent);
         service.updateArticleView(no);
         
         model.addAttribute("ccv", ccv);
         model.addAttribute("vo", vo);
         model.addAttribute("cateCode", cateCode);
         model.addAttribute("parent", parent);
         model.addAttribute("no", no);
         model.addAttribute("cm", cm);
         
         
         return "community/qnaView";
    }
  
    @GetMapping(value = {"community/qnaComment"})
    public String QnaComment(Model model, @RequestParam("cateCode") String cateCode, String no, String parent){

    	CommunityArticleVO vo = service.selectFreeArticle(no);
    	
        model.addAttribute("cateCode", cateCode);
        model.addAttribute("parent", parent);
        model.addAttribute("no", no);
        model.addAttribute("vo", vo);

        return "community/qnaComment";
    }
    @PostMapping(value = {"community/qnaComment"})
    public String QnaComment(Model model,HttpServletRequest req, @AuthenticationPrincipal MyUserDetails myUser, CommunityArticleVO vo,
    		Integer cateCode,Integer regionCode, String no, String parent){
    	
        MemberEntity member = myUser.getMember();
        
        vo.setMember_uid(member.getUid());
        vo.setCateCode(String.valueOf(cateCode));
        vo.setRegionCode(String.valueOf(regionCode));
        vo.setParent(String.valueOf(parent));
        vo.setRegip(req.getRemoteAddr());
        
        service.insertComment(vo);
        
        
        return "redirect:/community/qnaView?cateCode="+cateCode+"&no="+no+"&parent="+parent;
        
    }
    
    /* Mytown */
    @GetMapping(value = {"community/mytownWrite"})
    public String MytownWrite(Model model, @RequestParam("cateCode") String cateCode){

        model.addAttribute("cateCode", cateCode);

        return "community/mytownWrite";
    }
    @PostMapping(value = {"community/mytownWrite"})
    public String MytownWrite(Model model,HttpServletRequest req, @AuthenticationPrincipal MyUserDetails myUser, CommunityArticleVO vo,
    		Integer cateCode,Integer regionCode){
    	
        MemberEntity member = myUser.getMember();
        
        vo.setMember_uid(member.getUid());
        vo.setCateCode(String.valueOf(cateCode));
        vo.setRegionCode(String.valueOf(regionCode));
        vo.setRegip(req.getRemoteAddr());
        service.insertFreeArticle(vo);
        
        
        return "redirect:/community/freeList?cateCode="+cateCode;
        
    }
}
