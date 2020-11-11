package jdbc;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import java.sql.PreparedStatement;
import com.sun.star.beans.GetDirectPropertyTolerantResult;
//import com.sun.star.beans.GetDirectPropertyTolerantResult;


public class UserDao extends BaseDao{
	public User dologin(String name,String pass){ 
		User u=null;
		try {
			super.connect();
			String sql="select * from user where name=? and pass=?";//这一段用于连接数据库登录
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2,pass);
			rs=pstmt.executeQuery();
			/*
			while(rs.next()){
				u=new User();
				u.setId(rs.getInt(4));
				u.setName(rs.getString(2));
				u.setPass(rs.getString(1));
				u.setAge(rs.getInt(3));
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
		return u;
	}
	public int insert(User u){
		int row=0;
		System.out.println("111");
		try {
				super.connect();
			
			
				String sql="insert into bjh_lcddjh(ZJ,STNUM,SYNUM,FX,CC,DDSK,DD401,DD402,DD403,DD404,DD405,DD406,DD407,DD408,DD409,DD410,DD411,KC,HJ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//++
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.valueOf(u.getZJ()));//++
				pstmt.setString(2, "1");
				pstmt.setString(3, "1");
				pstmt.setString(4, u.getFX());
				pstmt.setString(5, u.getCC());
				pstmt.setString(6, u.getDDSK());
				pstmt.setString(7, u.getD1());
				pstmt.setString(8, u.getD2());
				pstmt.setString(9, u.getD3());
				pstmt.setString(10, u.getD4());
				pstmt.setString(11, u.getD5());
				pstmt.setString(12, u.getD6());
				pstmt.setString(13, u.getD7());
				pstmt.setString(14, u.getD8());
				pstmt.setString(15, u.getD9());
				pstmt.setString(16, u.getD10());
				pstmt.setString(17, u.getD11());
				pstmt.setString(18, u.getKC());
				pstmt.setString(19, u.getHJ());
				row=pstmt.executeUpdate();
			
			
				String sql1="insert into bjh_lccfjh(ZJ,FX,CC,CFSK,BNCL,HJ) values(?,?,?,?,?,?)";
				pstmt1=conn.prepareStatement(sql1);
				pstmt1.setInt(1, Integer.valueOf(u.getZJ()));
				pstmt1.setString(2, u.getFX1());
				pstmt1.setString(3, u.getCC1());
				pstmt1.setString(4, u.getCFSK1());
				pstmt1.setString(5, u.getBNCL1());
				pstmt1.setString(6, u.getHJ1());
				pstmt1.executeUpdate();
				
				String sql2="insert into bjh_tsztsj(ZJ,SJ,ZSDD,ZSCF,ZSJC,ZTDD,ZTCF,ZTJC) values(?,?,?,?,?,?,?,?)";
				pstmt2=conn.prepareStatement(sql2);
				pstmt2.setInt(1, Integer.valueOf(u.getZJ()));
				pstmt2.setString(2, u.getSJ());
				pstmt2.setString(3, u.getZSDD());
				pstmt2.setString(4, u.getZSCF());
				pstmt2.setString(5, u.getZSJC());
				pstmt2.setString(6, u.getZTDD());
				pstmt2.setString(7, u.getZTCF());
				pstmt2.setString(8, u.getZTJC());
				pstmt2.executeUpdate();
				
				String sql3="insert into bjh_zts(ZJ,ZZCS,ZS,ZYCS,TS,ZTS) values(?,?,?,?,?,?)";
				pstmt3=conn.prepareStatement(sql3);
				pstmt3.setInt(1, Integer.valueOf(u.getZJ()));
				pstmt3.setString(2, u.getZZCS());
				pstmt3.setString(3, u.getZS());
				pstmt3.setString(4, u.getZYCS());
				pstmt3.setString(5, u.getTS());
				pstmt3.setString(6, u.getZTS());
				pstmt3.executeUpdate();

				String sql4="insert into bjh_bgzzrw(ZJ,DDLS,CFLS,JTLS,BZLS,XCS,ZCS,PKS,BLCS,ZDZS) values(?,?,?,?,?,?,?,?,?,?)";
				pstmt4=conn.prepareStatement(sql4);
				pstmt4.setInt(1, Integer.valueOf(u.getZJ()));
				pstmt4.setString(2, u.getDDLS());
				pstmt4.setString(3, u.getCFLS());
				pstmt4.setString(4, u.getJTLS());
				pstmt4.setString(5, u.getBZLS());
				pstmt4.setString(6, u.getXCS());
				pstmt4.setString(7, u.getZCS());
				pstmt4.setString(8, u.getPKS());
				pstmt4.setString(9, u.getBLCS());
				pstmt4.setString(10, u.getZDZS());
				pstmt4.executeUpdate();
				
				String sql5="insert into bjh_pkjh(PK,CZ,CS,PKCC,KCLY,STNUM,SYNUM) values(?,?,?,?,?,?,?)";
				pstmt5=conn.prepareStatement(sql5);
				pstmt5.setInt(1, Integer.valueOf(u.getZJ()));
				pstmt5.setString(2, u.getCZ());
				pstmt5.setString(3, u.getCS());
				pstmt5.setString(4, u.getPKCC());
				pstmt5.setString(5, u.getKCLY());
				pstmt5.setString(6, "1");
				pstmt5.setString(7, "1");
				pstmt5.executeUpdate();
				
				String sql6="insert into bjh_xcjh(ZJ,XCDD,CZ,CS,DDCC,XHAP,ZCDD,QX,KCLY,GYCC) values(?,?,?,?,?,?,?,?,?,?)";
				pstmt6=conn.prepareStatement(sql6);
				pstmt6.setInt(1, Integer.valueOf(u.getZJ()));
				pstmt6.setString(2, u.getXCDD());
				pstmt6.setString(3, u.getCZ1());
				pstmt6.setString(4, u.getCS1());
				pstmt6.setString(5, u.getDDCC());
				pstmt6.setString(6, u.getXHAP());
				pstmt6.setString(7, u.getZCDD());
				pstmt6.setString(8, u.getQX());
				pstmt6.setString(9, u.getKCLY1());
				pstmt6.setString(10, u.getGYCC());
				pstmt6.executeUpdate();
				System.out.println("222");
				System.out.println(u.getXCDD());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
		return row;
	}
	/*public int insert1(User u){
		int row=0;
		try {
			super.connect();
			String sql="insert into tdcs_sendtrain(TRAINID,RAILID,NBSTNAME,ST_START,ACTUAL,TRAINTYPE,TASKSID,TRAINRSTYPE,HUO,LIE,HAO,WEI,FENG,QIN,SYNUMBER,STNUM) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//++
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, u.getCheci());//++
			pstmt.setString(2, u.getGudao());
			pstmt.setString(3, u.getTy());
			pstmt.setString(4, u.getGdsj());
			pstmt.setString(5, u.getActualsj());
			pstmt.setString(6, u.getXh());
			pstmt.setString(7, u.getTasksid());
			pstmt.setString(8, u.getTrainrstype());
			pstmt.setString(9, u.getHuo());
			pstmt.setString(10, u.getLie());
			pstmt.setString(11, u.getHao());
			pstmt.setString(12, u.getWei());
			pstmt.setString(13, u.getFeng());
			pstmt.setString(14, u.getQin());
			pstmt.setString(15, u.getSYN());
			pstmt.setString(16, u.getSTN());
			row=pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
		return row;
	}*/
	public void delete(String num){
		try{
			super.connect();
			String sql="delete from tdcs_recvtrain where ZJ='"+num+"'";
			String sql1="delete from tdcs_sendtrain where ZJ='"+num+"'";
			pstmt=conn.prepareStatement(sql);
			pstmt.executeUpdate();
			pstmt=conn.prepareStatement(sql1);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			super.closeAll();
		}
	}
}