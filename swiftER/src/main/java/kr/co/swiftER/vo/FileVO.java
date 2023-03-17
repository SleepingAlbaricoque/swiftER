package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileVO {
	private int fno;
	private int board;
	private int cate;
	private int parent;
	private String newName;
	private String oriName;
	private String rdate;
}
