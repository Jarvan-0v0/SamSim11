package blkjweb.utils;

import java.io.IOException;

import org.blkj.utils.FileTool;

public class MysqlBack
{
	//数据库备份
	public boolean backup(String file){
		boolean result = false;
        String path = Const.getPathDbback();
        
		if(! FileTool.mkdirs(path))//保留备份的数据库文件的目录
			return result;

		//Mysql： 5.6以前 ：D:/programs/mysql/bin/mysqldump --no-defaults -h 127.0.0.1 -u root -p123456 redhat  --default-character-set=gbk --result-file=d:\1.sql
		//Mysql： 5.6以后 cmd /c D:\programs\mysql\bin\mysqldump --login-path=local redhat --result-file=D:\output\dbbackup\2016-08-24_113641.sql
		
		String stmt = Const.getDB_binPath() + "mysqldump --no-defaults " +
		              " -h " + Const.getIP() + 
				      " -u " + Const.getROOT() + 
				      " -p" + Const.getPASSWORD() + " " +  Const.getDATABASE() +
				      " --result-file=" + path + file;
		/*如下语句：5.6不行 （如果）
		String stmt = Const.getDB_binPath() + "mysqldump --login-path=local " +
		              Const.getDATABASE() + " --result-file=" + path + file;*/
		
		if ((Const.getOsType()).equalsIgnoreCase("Linux")){//没有调试
			stmt = Const.getDB_binPath() + "/mysqldump -u " + Const.getROOT() + " -p" + 
		           Const.getPASSWORD() + " " + Const.getDATABASE() + " -r " + file;
			/**String[] command = new String[]{"/usr/local/mysql/bin/mysqldump", "-u" + ContextListener.getROOT(), "-p" + ContextListener.getPASSWORD(),ContextListener.getDATABASE(), "-r"+fileName }; 
					Runtime.getRuntime().exec(command)*/
		}
		else
			stmt = "cmd /c " + stmt;//无cmd /c时，在windows service 2003下会出错
		
		try 
		{
			Runtime.getRuntime().exec(stmt); //行string指定的命令，然后停止
			result = true;
		}catch (Exception e) {
			Console.showMessage(MysqlBack.class.getSimpleName(), e.getMessage(), e);
			result = false;
		}
				
		return result;
	}
	
    //数据库恢复
	public boolean restore(String fileFullName)
	{
		boolean result = true;
		//mysql -u root -p123456 dbmshlg < D:\HLGLogs\DBback_2010-04-18(10).sql
		
		String stmt = Const.getDB_binPath() + "mysql -u " + Const.getROOT() + " -p" + Const.getPASSWORD() + " " + Const.getDATABASE() + " < \"" + fileFullName+"\"";
		//String stmt = Const.getDB_binPath() + "mysql --login-path=local " + Const.getDATABASE() + " < \"" + fileFullName+"\"";		
		
		String[] cmd = { "cmd", "/c", stmt};
		if ((Const.getOsType()).equalsIgnoreCase("Linux")){//没有调试
			String mysqldumpPath = Const.getDB_binPath();
			cmd = new String[]{mysqldumpPath+"/mysql", 
					Const.getDATABASE(),
					"-u" + Const.getROOT(),
					"-p" + Const.getPASSWORD(), 
					"-e", 
					" source "+fileFullName };  
			//仅是为调试输出用
			stmt = mysqldumpPath+"/mysql " +Const.getDATABASE()+
					" -u" + Const.getROOT()+
					" -p" + Const.getPASSWORD()+" -e"+" source "+fileFullName;
		}
		
		try	{
			Runtime.getRuntime().exec(cmd);
		}catch (IOException e) {
			Console.showMessage(MysqlBack.class.getSimpleName(), e.getMessage(), e);
			result = false;
		}
		return result;
	}
	

}  

