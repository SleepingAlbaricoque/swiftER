package kr.co.swiftER.dto;

import java.util.List;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class ErDTO {
	
	private Header header;
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}

	private Body body;
	public Body getBody() {
		return body;
	}
	public void setBody(Body body) {
		this.body = body;
	}

	
	
    @XmlRootElement(name = "header")
    public static class Header {
    	private String resultCode;
    	private String resultMsg;

    	public String getResultCode() {
			return resultCode;
		}
		public void setResultCode(String resultCode) {
			this.resultCode = resultCode;
		}
		public String getResultMsg() {
			return resultMsg;
		}
		public void setResultMsg(String resultMsg) {
			this.resultMsg = resultMsg;
		}

    }

    @XmlRootElement(name = "body")
    public static class Body {
    	
    	private List<ItemDTO> item;
        private Integer numOfRows;
        private Integer pageNo;
        private Integer totalCount;
    	
        @XmlRootElement(name = "item")
        public static class Items{
        	private List<ItemDTO> items;
        	
			public List<ItemDTO> getItem() {
				return items;
			}

			public void setItem(List<ItemDTO> items) {
				this.items = items;
			}
        }

        public List<ItemDTO> getItem() {
			return item;
		}

		public void setItem(List<ItemDTO> item) {
			this.item = item;
		}

		public Integer getNumOfRows() {
			return numOfRows;
		}

		public void setNumOfRows(Integer numOfRows) {
			this.numOfRows = numOfRows;
		}

		public Integer getPageNo() {
			return pageNo;
		}

		public void setPageNo(Integer pageNo) {
			this.pageNo = pageNo;
		}

		public Integer getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(Integer totalCount) {
			this.totalCount = totalCount;
		}
        
    }

}