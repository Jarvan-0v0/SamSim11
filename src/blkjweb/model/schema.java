package blkjweb.model;

public class schema 
{
	private String table_name;//表名
	private String engine;//所使用的存储引擎
	private String table_comment;//注释
	private String pk;//主键

	/*方法二
	private BigInteger data_length;//数据大小
	private BigInteger index_length;//索引大小
	private BigInteger table_rows;//记录数
	*/
	//方法一
	private long data_length;//数据大小
	private long index_length;//索引大小
	private long table_rows;//记录数
	
	
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	
	public long getData_length() {
		return data_length;
	}
	public void setData_length(long data_length) {
		this.data_length = data_length;
	}
	public long getIndex_length() {
		return index_length;
	}
	public void setIndex_length(long index_length) {
		this.index_length = index_length;
	}
	public long getTable_rows() {
		return table_rows;
	}
	public void setTable_rows(long table_rows) {
		this.table_rows = table_rows;
	}
	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}
	public String getTable_comment() {
		return table_comment;
	}
	public void setTable_comment(String table_comment) {
		this.table_comment = table_comment;
	}
	
}
