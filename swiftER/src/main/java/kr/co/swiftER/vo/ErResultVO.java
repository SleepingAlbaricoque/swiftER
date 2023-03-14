package kr.co.swiftER.vo;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@XmlRootElement(name = "response")
public class ErResultVO {
 
    @XmlElement(name = "header")
    public Header header;
 
    @XmlElement(name = "body")
    public Body body;
    
    @Setter
    @Getter
    @ToString
    @XmlRootElement(name = "header")
    public static class Header {
 
        @XmlElement(name = "resultCode")
        public String resultCode;
 
        @XmlElement(name = "resultMsg")
        public String resultMsg;
    }
    
    @Setter
    @Getter
    @ToString
    @XmlRootElement(name = "body")
    public static class Body {
            
        @XmlElement(name = "items")
        public BasisItems basisItems;
            
        @XmlElement(name = "numOfRows")
        public Integer numOfRows;
                
        @XmlElement(name = "pageNo")
        public Integer pageNo;
                
        @XmlElement(name = "totalCount")
        public Integer totalCount;
    }
 
    @Setter
    @Getter
    @ToString
    @XmlRootElement(name = "items")
    public static class BasisItems {
 
        @XmlElement(name = "item")
		public List<ErItemVO> basis;
    }
    
}
