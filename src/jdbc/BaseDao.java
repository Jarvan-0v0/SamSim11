package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BaseDao {
	protected Connection conn = null;
	protected Statement stmt = null;
	protected ResultSet rs = null;
	protected String url = "jdbc:mysql://39.106.115.236:3306/xianche?characterEncoding=utf-8";
	protected String name = "user";
	protected String password = "123";
	protected PreparedStatement pstmt=null;
	protected PreparedStatement pstmt1=null;
	protected PreparedStatement pstmt2=null;
	protected PreparedStatement pstmt3=null;
	protected PreparedStatement pstmt4=null;
	protected PreparedStatement pstmt5=null;
	protected PreparedStatement pstmt6=null;
	public void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, name, password);
			stmt = conn.createStatement();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeAll(){
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
			if(pstmt!=null){
				pstmt.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}