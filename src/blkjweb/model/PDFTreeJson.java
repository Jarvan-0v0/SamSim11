package blkjweb.model;

public class PDFTreeJson {

	private String id;//文档编号
	private String text;//文件名（含扩展名）filename
	private String img;//图标

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
}
