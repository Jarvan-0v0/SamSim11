package cargo.tao.dao;

import cargo.tao.entity.User;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.sun.star.beans.GetDirectPropertyTolerantResult;
//import com.sun.star.beans.GetDirectPropertyTolerantResult;

public class UserDao extends BaseDao{
	public void srchAndDelete(String tableName, String where, String content){
		try{
			super.connect();
			String sql="delete from "+tableName+" where "+where+" = '"+content+"'";
			pstmt=conn.prepareStatement(sql);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
	}
	public int cargo_insert(User u,int count){
		int row=0;
		String[] data = u.getData();
		String sql;
		int count2 = count;
		try {
			super.connect();
			if(count2 == 15){
				sql="insert into cargo_inspection_log(xh,cc,ch,cz,dzm,fzm,pm,zz,hc,jsl,kcbz,kcyy,xjsj,czr,czsj) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			}else{
				sql="insert into cargo_inspection_book(xh,cc,dzm,fzm,pm,zz,hc,jsl,kssj,jssj,kcbz,czr,czsj) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			}
			pstmt=conn.prepareStatement(sql);
			for(int i=0;i<count2;i++){
				pstmt.setString((i+1), data[i]);
			}
			row=pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
		return row;
	}
	public int trainInspection_insert(User u,int count){
		int row=0;
		String[] data = u.getData();
		String sql;
		int count2 = count;
		try {
			super.connect();
			if(count2 == 16){
				sql="insert into train_inspection_log(xh,cc,cz,dzm,fzm,pm,zz,hc,jsl,kcbz,gmbz,kcyy,ch,xjsj,czr,czsj) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			}else{
				sql="insert into train_inspection_book(xh,cc,dzm,fzm,pm,zz,hc,jsl,kssj,jssj,kcbz,gmbz,czr,czsj) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			}
			pstmt=conn.prepareStatement(sql);
			for(int i=0;i<count2;i++){
				pstmt.setString((i+1), data[i]);
			}
			row=pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
		return row;
	}
	public void table_delete(String tableName){
		try{
			super.connect();
			String sql="TRUNCATE TABLE "+tableName;
			pstmt=conn.prepareStatement(sql);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
	}
	public void table_copyFromTo(String tableName1, String tableName2){
		try{
			super.connect();
			String sql="insert into "+tableName2+" select  * from "+tableName1;
			pstmt=conn.prepareStatement(sql);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
	}
}