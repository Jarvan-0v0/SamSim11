package blkjweb.model;

public class query 
{
	private String moduleId;//模块id,实际上就是表名
	private String fieldName;//对应库表的字段
	//以下字段需要依据不同情况，由程序赋值	
	private int controlType; //选择事件控制类别
	private String controlSource;
	private String controlDefault;
	private String fullName; //框组件上显示的名字
	
	
	public int getControlType() {
		return controlType;
	}
	public void setControlType(int controlType) {
		this.controlType = controlType;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	public String getControlDefault() {
		return controlDefault;
	}
	public void setControlDefault(String controlDefault) {
		this.controlDefault = controlDefault;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getControlSource() {
		return controlSource;
	}
	public void setControlSource(String controlSource) {
		this.controlSource = controlSource;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
