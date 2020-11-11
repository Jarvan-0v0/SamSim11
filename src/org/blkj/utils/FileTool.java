package org.blkj.utils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.blkj.utils.base.FileManagerFilter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import blkjweb.utils.Console;


/**
 * Useful file system level utilities.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2007/09/FileUtil.html
 */
public final class FileTool 
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");//行分隔符
	private static final String SEPARATOR = System.getProperty("file.separator");//分隔符
	
	private FileTool() {
	}
	
	//检查文件srStr的扩展名是否为：ext2或ext1。含的话返回true;
	public static boolean extExist(String fileName, String ext1,String ext2)
	{
		if (fileName == null || ! fileName.matches("^.+\\.(?i)(("+ext1+")|("+ext2+"))$"))
			return false;
		else
			return true;
	}

	//进行文件覆盖操作
	public static boolean copy(MultipartFile file, String path, boolean overwrite)
	{
		boolean flag = false;
		File localFile = new File(path); //创建File对象，参数为String类型，
		//文件存在，且不能进行覆盖操作。overwrite=true,表示可以进行文件覆盖操作
		if (localFile.exists() && !overwrite) {
			return flag;
		}
		try {
			file.transferTo(localFile);//写file内容到文件  
			flag = true;
		} catch (Exception e) {
			flag = false;
		}

		if(!localFile.exists())
			flag = false;
		else
			flag = true;

		return flag;
	}
     
	public static boolean copy(MultipartFile mf, String folderId, String fileName)
	{
		boolean flag = true;
		File uploadFile = new File(folderId  + fileName);
		try { 
			FileCopyUtils.copy(mf.getBytes(), uploadFile);  
		} catch (IOException e) { 
			flag = false;
		}
		return flag;
	}
    //判断某目录下是否存在扩展名为ext的文件
    public static boolean fileExist(String dir, String ext)
    {
    	File f = new File(replaceForwardSlash(dir));
    	String[] str = f.list();
    	Pattern p = Pattern.compile("(.+\\."+ ext +")");
    	for(int i=0;i<str.length;i++){
    		Matcher m = p.matcher(str[i]);
    		if(m.find()){
    			return true;
    		}
    	}
    	return false;
    }
   //判断文件是否存在
    public static boolean fileExist(String fullName)
    {
    	File file=new File(fullName);
    	if(! file.exists()){
    		return false;
    	}
    	return true;
    }
    
	 //检查某目录是否为空 空返回true
    public static boolean dirIfEmpty(String dir)
    {
    	File file =new File(replaceForwardSlash(dir));
    	if (file.isDirectory()) {
  	      String[] files = file.list();
  	      if (files.length > 0) 
  	        return false;
  	    }
    	return true;
    }
    
	//删除指定目录以及目录下的所有文件
	public static boolean delDirFile(String path)
	{
		File directory = new File(path);
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			Console.showMessage(FileTool.class.getSimpleName(),e.getMessage(), e);
			return false;
		}
		return true;
	}

	  /**
     * 删除指定的目录及其下所有子目录。
     *
     * @param dir
     * @return
     */
    public boolean delDirFile2(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = delDirFile2(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
    //删除指定目录（含子目录）下全部文件和目录. 删除目录时Flag = true; 删除目录时先要删除目录下的文件或子目录
    public static void delDirFile(String filepath, boolean dirDelFlag)
    {  
    	//Log.debug("filepath:"+filepath);
    	if (filepath == null || filepath.length() ==0)
    		return;
    	File file = new File(filepath);//定义文件路径
    	if(file.exists()){
    		if(dirDelFlag)
    			delAllDir(file);
    		else
    			delAllFile(file);
    	}
    }  

    //删除给定目录下内容，不含此目录
    private static void delAllFile(File dir)
    {
    	delAllDir(dir);
    	dir.mkdirs();//再创建根目录
    }   

    //删除给定目录下内容，含此目录
    private static void delAllDir(File directory)
    {
    	File[] children = directory.listFiles();
    	if(children.length==0){   
    		directory.delete(); //删除目录  
    	}   
    	else{   
    		for(int i=0;i<children.length;i++)
    		{   
    			if(children[i].isFile()){   
    				children[i].delete();//删除文件   
    			}   
    			else 
    				if(children[i].isDirectory()){//递归删除指定子目录   
    					delAllDir(children[i]);   
    				}   
    		}   
    		directory.delete();//删除目录
    	}   
    }   
    //删除所有目录下所有文件,但是目录没有删除
   	public void deletAllFile() {
   	    File[] roots = File.listRoots();
   	    for (int i = 0; i < roots.length; i++) {
   	     if (roots[i].isDirectory()) {
   	      deleteFiles(roots[i].toString(), "*", "*");
   	     }
   	    }
   	}
   	// 删除指定文件或一些文件
   	public void deleteFiles(String path, String inname, String inextension) {
   	    FileManagerFilter fmf = new FileManagerFilter(inname, inextension);
   	    File file = new File(path);
   	    File[] filelist = file.listFiles(fmf);

   	    // Log.debug(filelist.length); 此语句用于测试
   	    if (filelist.length != 0) {
   	     for (File fl : filelist) {
   	      // boolean delfil = fl.delete();
   	    	Console.showMessage(fl + (fl.delete() ? " 成功" : " 没有成功") + "被删除!");
   	     }
   	    } else {
   	    	Console.showMessage("根据您所给的条件,没有找到要删除的文件!");
   	    }
   	}
   	
    //删除指定目录root下给定的子目录subDir中的所有文件和子目录subDir
    public static void delSubDir(String root,String subDir){   
    	//root是根目录的绝对路径   
    	File directory = new File(root);   
    	File[] files = directory.listFiles();   
    	//根目录为空   
    	if(files.length ==0){//根目录为空   
    		return;   
    	}   
    	else{   
    		for(int i=0;i<files.length;i++){   
    			if(files[i].isDirectory()){   
    				//根目录的子文件夹就是要删除的文件夹   
    				if(files[i].getName().equals(subDir)){   
    					delAllDir(files[i]);   
    				}   
    				//根目录的子文件夹不是要删除的文件夹   
    				else{   
    					delSubDir(files[i].getAbsolutePath(),subDir);   
    				}   
    			}                   
    		}   
    	}   
    }

    private static void mkdirs(File file) throws IOException {
		checkFile(file);
		File parentFile = file.getParentFile();
		if (!parentFile.exists() && !parentFile.mkdirs()) {
			throw new IOException("Creating directories " + parentFile.getPath() + " failed.");
		}
	}
   
    /**
     * Check and create missing parent directories for the given file.
     * @param FileList The file to check and create the missing parent directories for.
     * @throws IOException If creating parent directories fails.
     */
    public static boolean mkdirs(String path)
    {
    	boolean resultFlag = true;
    	File dir   =   new   File(path);
    	//存在且为一个目录时isDirectory()返回true
    	if(! dir.isDirectory()) 
    	{
    	    dir.mkdir();
    	    resultFlag = dir.isDirectory();
    	} 
    	return resultFlag;
    }
    
    /**
     * Check if the given file is actually a file.
     * @param file The file object to be checked.
     * @throws IOException If the given file is actually not a file.
     */
    private static void checkFile(File file) throws IOException {
        if (file.exists() && !file.isFile()) {
            throw new IOException("File " + file.getPath() + " is actually not a file.");
        }
    }
    
  //判断某一文件(含路径)是否存在,存在返回true,不存在返回false
  	public static boolean checkFile(String filename){
  		File f = new File(filename);
  		if(f.exists()){
  			return true;
  		}
  		return false;
  	}

  	//蒋srcPathName目录下所有文件进行压缩，并保留在目录下zipFile文件中(含有路径+文件名)
  	public static boolean compress(String srcPathName,File zipFile) 
  	{
  		File srcdir = new File(srcPathName);
  		if (!srcdir.exists())//目录不存在
  			return false;

  		//先删除存在的压缩文件
  		/*对每一个Ant Task，如Mkdir，Delete、Copy、Move、Zip等，都必须设置一个Project对象*/
  		Project prj = new Project();
  		Delete delete = new Delete();
  		delete.setProject(prj);
  		delete.setFile(zipFile);
  		delete.execute(); 

  		//生成新的zip压缩文件
  		Zip zip = new Zip();
  		zip.setProject(prj);
  		zip.setDestFile(zipFile);//要打包的压缩文件对象
  		//压缩设置
  		FileSet fileSet = new FileSet();
  		fileSet.setProject(prj);
  		fileSet.setDir(srcdir);//要打包的目录对象

  		zip.addFileset(fileSet);
  		zip.execute();

  		return true;
  	}

  	public static void writeZipFile(String pathZIPFile, String pathFile){
	    ZipOutputStream book = null;
	    BufferedReader a = null;
		try {
			book = new ZipOutputStream(new FileOutputStream(pathZIPFile));                                                            
			ZipEntry zip1 = new ZipEntry(pathZIPFile);
			zip1.setSize(pathZIPFile.length());
			FileReader file = new FileReader(pathFile);
			a = new BufferedReader(file);
			ZipEntry zip2 = new ZipEntry(pathFile);
			zip2.setSize(1024);
			book.putNextEntry(zip2);
		//	byte[] b = new byte[1024];
			//int readlen = -1;
			String line;
			while ((line = a.readLine()) != null) {
				book.setComment(line);
			}
		} catch (IOException e) {
    		Console.showMessage(FileTool.class.getSimpleName(),e.getMessage(), e);  
		}
		finally{
			try {
				a.close();
				if(book != null)
					book.close();
			} catch (IOException e) {
	    		Console.showMessage(FileTool.class.getSimpleName(),e.getMessage(), e);
			}
		}
	}
   //解压
   public static void readZipFile(String pathFile){
	   ZipInputStream input= null;
	   try{
		   input=new ZipInputStream(new FileInputStream(pathFile));
		   ZipEntry entry = null;
		   while((entry=input.getNextEntry())!=null){
			   File file = new File(entry.getName());
			   if(!file.exists()){
				   file.mkdirs();
				   file.createNewFile();
			   }
			   
		   }
	   }catch(Exception e){
   		Console.showMessage(FileTool.class.getSimpleName(),e.getMessage(), e);
	   }
	   finally{
		   try {
			   if(input != null)
				   input.closeEntry();
		} catch (IOException e) {
    		Console.showMessage(FileTool.class.getSimpleName(),e.getMessage(), e);
		}
	   }
   }
		
  //获取目录下所有文件(按时间排序)
  	public static List<File> getFileSortByTime(String path)
  	{
  		if(path == null)
  			return null;
  		File[] fileArray = new File(path).listFiles();
  		if(fileArray==null || fileArray.length == 0)
  			return null;
  		List<File> list = Arrays.asList(fileArray);
  		if (list != null && list.size() > 0) {
  			Collections.sort(list, new Comparator<File>() {
  				public int compare(File file, File newFile) {
  					if (file.lastModified() < newFile.lastModified()) {
  						return 1;
  					} else if (file.lastModified() == newFile.lastModified()) {
  						return 0;
  					} else {
  						return -1;
  					}
  				}
  			});
  		}
  		return list;
  	}

  	//获取目录下所有文件(按照文件名)
  	public static List<File> getFileSortByName(String fliePath)
  	{  
  		List<File> files = Arrays.asList(new File(fliePath).listFiles()); 
  		if (files != null && files.size() > 0) {
  			Collections.sort(files, new Comparator<File>() {  
  				public int compare(File o1, File o2) {  
  					if (o1.isDirectory() && o2.isFile())  
  						return -1;  
  					if (o1.isFile() && o2.isDirectory())  
  						return 1;  
  					return o1.getName().compareTo(o2.getName());  
  				}  
  			});  
  		}
  		return files;
  	}  
  	//按照文件大小排序    
  	public List<File> getFileSortByLength(String fliePath)
  	{  
  		List< File> files = Arrays.asList(new File(fliePath).listFiles());  
  		Collections.sort(files, new Comparator< File>() {  
  			public int compare(File f1, File f2) {  
  				long diff = f1.length() - f2.length();  
  				if (diff > 0)  
  					return 1;  
  				else if (diff == 0)  
  					return 0;  
  				else  
  					return -1;  
  			}  
  			public boolean equals(Object obj) {  
  				return true;  
  			}  
  		});  
  		return files;
  	}  
  	
  	//获取目录下所有文件
  	public List<File> getFiles(String fliePath) 
  	{
  		List<File> files = Arrays.asList(new File(fliePath).listFiles());
  		File realFile = new File(fliePath);
  		if (realFile.isDirectory()) {
  			File[] subfiles = realFile.listFiles();
  			for (File file : subfiles) {
  				if (!file.isDirectory()) {
  					files.add(file);
  				}
  			}
  		}
  		return files;
  	}

  	 //获取某一目录下的所有文件，返回格式：<路径，文件名>
    public static List<HashMap<String, String>> getAllFiles(String dirPath)
    {
    	if (dirPath == null || dirPath.length() == 0)
    		return null;

    	dirPath = replaceForwardSlash(dirPath);
    	File dir = new File(dirPath);

    	File[] files = dir.listFiles();
    	int len = files.length; 
    	if( len == 0)
    		return null;
    	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap <String, String> map = null;
		for (int i = 0; i < len; i++)//输出文件名及其绝对路径
		{
			map = new HashMap<String, String>();
			map.put("id", files[i].getAbsolutePath());
			map.put("name",files[i].getName());
			list.add(map);
        }
		return list;
    }
    
    //文件重命名
    public static boolean renameFile(String path,String oldName,String newName)
    {
    	if(! oldName.equals(newName))//新的文件名和以前文件名不同时,才有必要进行重命名
    	{
    		File oldfile=new File(path + SEPARATOR +oldName);
    		File newfile=new File(path + SEPARATOR + newName); 
    		//String parent = file.getParent();
    		if(!oldfile.exists()){
    			return false;//重命名文件不存在
    		}
    		if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
    			return false;//重命名文件存在
    		else{ 
    			oldfile.renameTo(newfile); 
    		} 
    	}
    	else{
    		return false;//新文件名和旧文件名相同
    	}
    	return true;
    }
    
    //文件夹重命名
    public static boolean renameDir(String fromDir, String toDir)
    {
        File from = new File(fromDir);

        if (!from.exists() || !from.isDirectory()) {
          return false;
        }
        
        File to = new File(toDir);
        if (!from.renameTo(to))
        	return false;
        
        return true;

      }
    //删除folderId目录下，具有前缀filePre的所有文件
    public static void delMultiFile(String folderId, String filePre)
	{
    	File folder = new File(folderId);    //创建File对象,指向folderId目录
    	if (folder != null && folder.exists()) {
    		File[] names = folder.listFiles(); //folder.list();获取目录所有文件和路径,并以字符串数组返回
    		for (File f1 : names) {//遍历字符串数组
    			if (f1.isFile() && f1.getName().startsWith(filePre))
    				FileTool.delFile(folderId,f1.getName());	
    		}
    	}
	}
	
    /**
	 * delete file
	 * @param path
	 * @param filename
	 */
	public static void delFile(String path,String filename){
		if (!(path.substring(path.length() - 1 )).equals(SEPARATOR)){
			path = path + SEPARATOR;
		}
		path = path +  filename;
		File file=new File(path);
		if(file.exists()&&file.isFile()){
			file.delete();
		}
	}
	public static void delFile(String fullname)
	{
		File file=new File(fullname);
		if(file.exists()&&file.isFile()){
			file.delete();
		}
	}
		
    /**
     * create file
     *  @param full path
     * @param filename
     * @throws IOException
     */
    public static void createFile(String path,String filename)
    {
    	if (!(path.substring(path.length() - 1 )).equals(SEPARATOR)){
    		path = path + SEPARATOR;
    	}
    	File file=new File(path + filename);
    	try {
    		mkdirs(file);
    	} catch (IOException e) {
    		Console.showMessage(FileTool.class.getSimpleName(),e.getMessage(), e);
    	}
    }
    
    /** 
     * 移动整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
    public static boolean moveFolder(String oldPath, String newPath) 
    { 
    	boolean result = true;
    	File srcDir = new File(oldPath); 
    	File destDir = new File(newPath); 
    	try {
    		FileUtils.moveDirectory(srcDir, destDir);
    	} catch (IOException e) {
    		result = false;
    	} 

    	return result;
	   
      /* try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
               } 
               else{ 
                   temp=new File(oldPath+File.separator+file[i]); 
               } 

               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + SEPARATOR + (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath + SEPARATOR + file[i],newPath + SEPARATOR + file[i]); 
               } 
           } 
       } catch (Exception e) { 
   			MyLogger.showMessage(FileTool.class.getSimpleName(),e.getMessage(), e);
   			return false;
       } */
      
   }
   
  
    /** 
     * 移动单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static boolean moveFile(String oldPath, String newPath) 
   { 
	   boolean result = true;
	   File srcFile = new File(oldPath); 
	   File destFile = new File(newPath); 
	   try {
		   FileUtils.moveFile(srcFile,destFile);//Commons IO提供拷贝文件方法在其FileUtils类
	   } catch (IOException e) {
		   result = false;
	   } 
	   
	   return result;
   }
   
    /**
     * 2014-08-04修订<br/> 将字节数组写入二进制文件<br/>
     * 注意文件操作时,文件可以不存在,但文件的路径必须存在,否则将发生异常.<br/>
     *
     * @param file
     * @param buffer
     * @throws java.io.FileNotFoundException
     */
    public void addFile(File file, byte[] buffer) throws FileNotFoundException, IOException {
        InputStream streamIn = new ByteArrayInputStream(buffer);
        FileOutputStream streamOut = new FileOutputStream(file);
        try {
            int bytesRead = 0;
            while ((bytesRead = streamIn.read(buffer)) != -1) {
            	
                streamOut.write(buffer, 0, bytesRead);
            }
            streamOut.flush();
        } finally {
            streamOut.close();
            streamIn.close();
        }
    }

    /**
     * 如在处理事务时，强制将字节数组写入到磁盘文件。来自Java开发者年鉴例27
     *
     * @param file
     * @param buffer
     * @throws java.lang.Exception
     */
    public void addFileSync(File file, byte[] buffer) throws Exception 
    {
    	FileOutputStream os = null;
    	try{
    		os = new FileOutputStream(file);
    		FileDescriptor fd = os.getFD();
    		os.write(buffer);
    		os.flush();
    		fd.sync();
    	}finally{
    		if (os != null)
    			os.close();
    	}
    }
	
	/**
	 * Copy file with the option to overwrite any existing file at the destination.
	 * @param source The file to read the contents from.
	 * @param destination The file to write the contents to.
	 * @param overwrite Set whether to overwrite any existing file at the destination.
	 * @throws IOException If the destination file already exists while <tt>overwrite</tt> is set
	 * to false, or if copying file fails.
	 *  把源文件对象复制成目标文件对象 overwrite=false 表示不能覆盖
	 */
	public static boolean copy(File source, File destination, boolean overwrite)  {
		boolean flag = false;
		//文件存在，且不能进行覆盖操作。overwrite=true,表示可以进行文件覆盖操作
		if (destination.exists() && !overwrite) {
			return flag;
		}

		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			mkdirs(destination);
			input = new BufferedInputStream(new FileInputStream(source));
			output = new BufferedOutputStream(new FileOutputStream(destination));
			int contentLength = input.available();
			while (contentLength-- > 0) {
				output.write(input.read());
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
		}finally {
			close(input, source);
			close(output, destination);
		}
		return flag;
	}
	




	
	
	
	
/////////2017	
	
	
	
	
	
	

    //去除字符串前后的空格
    public static String trimBlank(String str)    
    {
    	if (str == null || str.length() == 0)
    		return str;
    	
    	return replaceBlank(str);//去除空格
    } 
    
    //去除字符串前后的空格、及纯数字字符串前的数字0
    public static String trimBlankZero(String str)    
    {
    	String strT = "";
    	if (str == null || str.length() == 0)
    		return str;
    	
    	strT =  replaceBlank(str);//去除空格
    	
    	if (! isNumeric(strT))
    		return strT;
    	
    	 int intStr = Integer.parseInt(strT);//去除前面的数字0
         return  Integer.toString(intStr);    
    } 
    
    
    //去除字符串中的空格、回车、换行符、制表符
    public static String replaceBlank(String s)    
    {    
         Pattern p = Pattern.compile("\\s*|\t|\r|\n");    
         Matcher m = p.matcher(s);    
         return m.replaceAll("");    
    } 
    
    //除去字符串中的空格
    public static String removeBlank(String retStr)
    {
             return retStr.replaceAll("\\s", "");  
    }
  //检查文件srStr的扩展名是否为：ext。含的话返回true;
    public static boolean extExist(String fileName, String ext)
    { 
    	 if (fileName == null || ! fileName.matches("^.+\\.(?i)("+ext+")$"))
    		 return false;
    	 else
    		 return true;
    } 
  
    
    //将字符串中的"\"替换为"/"后返回
    public static String replaceForwardSlash(String srStr)
    {
    	return srStr.replaceAll("\\\\", "/");
    }
 
   
    //如何判断字符串是否由纯数字组成 
    public static boolean isNumeric(String str)  
    {  
    	boolean flag = true;  
    	if(str.length() == 0)  
    	{  
    		flag = false;  
    	}  
    	else  
    	{  
    		for (int i = str.length();--i>=0;)  
    		{  
    			if (!Character.isDigit(str.charAt(i)))  
    			{   
    				flag = false;   
    			}   
    		}  
    	}  
    	return flag;   
    } 
    
   
    
    // Writers ------------------------------------------------------------------------------------

    /**
     * Write byte array to file. If file already exists, it will be overwritten.
     * @param file The file where the given byte array have to be written to.
     * @param bytes The byte array which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, byte[] bytes) throws IOException {
        write(file, new ByteArrayInputStream(bytes), false);
    }

    /**
     * Write byte array to file with option to append to file or not. If not, then any existing
     * file will be overwritten.
     * @param file The file where the given byte array have to be written to.
     * @param bytes The byte array which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, byte[] bytes, boolean append) throws IOException {
        write(file, new ByteArrayInputStream(bytes), append);
    }

    /**
     * Write byte inputstream to file. If file already exists, it will be overwritten.It's highly
     * recommended to feed the inputstream as BufferedInputStream or ByteArrayInputStream as those
     * are been automatically buffered.
     * @param file The file where the given byte inputstream have to be written to.
     * @param input The byte inputstream which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, InputStream input) throws IOException {
        write(file, input, false);
    }

    /**
     * Write byte inputstream to file with option to append to file or not. If not, then any
     * existing file will be overwritten. It's highly recommended to feed the inputstream as
     * BufferedInputStream or ByteArrayInputStream as those are been automatically buffered.
     * @param file The file where the given byte inputstream have to be written to.
     * @param input The byte inputstream which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, InputStream input, boolean append) throws IOException {
        mkdirs(file);
        BufferedOutputStream output = null;

        try {
            int contentLength = input.available();
            output = new BufferedOutputStream(new FileOutputStream(file, append));
            while (contentLength-- > 0) {
                output.write(input.read());
            }
        } finally {
            close(input, file);
            close(output, file);
        }
    }

    /**
     * Write character array to file. If file already exists, it will be overwritten.
     * @param file The file where the given character array have to be written to.
     * @param chars The character array which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, char[] chars) throws IOException {
        write(file, new CharArrayReader(chars), false);
    }

    /**
     * Write character array to file with option to append to file or not. If not, then any
     * existing file will be overwritten.
     * @param file The file where the given character array have to be written to.
     * @param chars The character array which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, char[] chars, boolean append) throws IOException {
        write(file, new CharArrayReader(chars), append);
    }

    /**
     * Write string value to file. If file already exists, it will be overwritten.
     * @param file The file where the given string value have to be written to.
     * @param string The string value which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, String string) throws IOException {
        write(file, new CharArrayReader(string.toCharArray()), false);
    }

    /**
     * Write string value to file with option to append to file or not. If not, then any existing
     * file will be overwritten.
     * @param file The file where the given string value have to be written to.
     * @param string The string value which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, String string, boolean append) throws IOException {
        write(file, new CharArrayReader(string.toCharArray()), append);
    }

    /**
     * Write character reader to file. If file already exists, it will be overwritten. It's highly
     * recommended to feed the reader as BufferedReader or CharArrayReader as those are been
     * automatically buffered.
     * @param file The file where the given character reader have to be written to.
     * @param reader The character reader which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, Reader reader) throws IOException {
        write(file, reader, false);
    }

    /**
     * Write character reader to file with option to append to file or not. If not, then any
     * existing file will be overwritten. It's highly recommended to feed the reader as
     * BufferedReader or CharArrayReader as those are been automatically buffered.
     * @param file The file where the given character reader have to be written to.
     * @param reader The character reader which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, Reader reader, boolean append) throws IOException {
        mkdirs(file);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file, append));
            int i = -1;
            while ((i = reader.read()) != -1) {
                writer.write(i);
            }
        } finally {
            close(reader, file);
            close(writer, file);
        }
    }

    /**
     * Write list of String records to file. If file already exists, it will be overwritten.
     * @param file The file where the given character reader have to be written to.
     * @param records The list of String records which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, List<String> records) throws IOException {
        write(file, records, false);
    }

    /**
     * Write list of String records to file with option to append to file or not. If not, then any
     * existing file will be overwritten.
     * @param file The file where the given character reader have to be written to.
     * @param records The list of String records which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, List<String> records, boolean append) throws IOException {
        mkdirs(file);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file, append));
            for (String record : records) {
                writer.write(record);
                writer.write(LINE_SEPARATOR);
            }
        } finally {
            close(writer, file);
        }
    }

    // Readers ------------------------------------------------------------------------------------

    /**
     * Read byte array from file. Take care with big files, this would be memory hogging, rather
     * use readStream() instead.
     * @param file The file to read the byte array from.
     * @return The byte array with the file contents.
     * @throws IOException If reading file fails.
     */
    public static byte[] readBytes(File file) throws IOException {
        BufferedInputStream stream = (BufferedInputStream) readStream(file);
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return bytes;
    }
    
    /**
     * Read byte stream from file.
     * @param file The file to read the byte stream from.
     * @return The byte stream with the file contents (actually: BufferedInputStream).
     * @throws IOException If reading file fails.
     */
    public static InputStream readStream(File file) throws IOException {
        //return new BufferedInputStream(new FileInputStream(file));
    	return new FileInputStream(file);
    }

    /**
     * Read character array from file. Take care with big files, this would be memory hogging,
     * rather use readReader() instead.
     * @param file The file to read the character array from.
     * @return The character array with the file contents.
     * @throws IOException If reading file fails.
     */
    public static char[] readChars(File file) throws IOException {
        BufferedReader reader = (BufferedReader) readReader(file);
        char[] chars = new char[(int) file.length()];
        reader.read(chars);
        return chars;
    }

    /**
     * Read string value from file. Take care with big files, this would be memory hogging, rather
     * use readReader() instead.
     * @param file The file to read the string value from.
     * @return The string value with the file contents.
     * @throws IOException If reading file fails.
     */
    public static String readString(File file) throws IOException {
        return new String(readChars(file));
    }

    /**
     * Read character reader from file.
     * @param file The file to read the character reader from.
     * @return The character reader with the file contents (actually: BufferedReader).
     * @throws IOException If reading file fails.
     */
    public static Reader readReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }

    /**
     * Read list of String records from file.
     * @param file The file to read the character writer from.
     * @return A list of String records which represents lines of the file contents.
     * @throws IOException If reading file fails.
     */
    public static List<String> readRecords(File file) throws IOException {
        BufferedReader reader = (BufferedReader) readReader(file);
        List<String> records = new ArrayList<String>();
        String record = null;

        try {
            while ((record = reader.readLine()) != null) {
                records.add(record);
            }
        } finally {
            close(reader, file);
        }

        return records;
    }

    // Copiers ------------------------------------------------------------------------------------

    /**
     * Copy file. Any existing file at the destination will be overwritten.
     * @param source The file to read the contents from.
     * @param destination The file to write the contents to.
     * @throws IOException If copying file fails.
     */
    public static void copy(File source, File destination) {
        copy(source, destination, true);
    }

    
    //把源文件对象复制成目标文件对象
    public static void copyByBuffer(File src, File dst) {
    	final int BUFFER_SIZE = 16 * 1024;
    	InputStream in = null;
    	OutputStream out = null;
    	try {
    		in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
    		out = new BufferedOutputStream(new FileOutputStream(dst),BUFFER_SIZE);
    		byte[] buffer = new byte[BUFFER_SIZE];
    		int len = 0;
    		while ((len = in.read(buffer)) > 0) {
    			out.write(buffer, 0, len);
    		}
    	}catch (Exception e) {
            e.printStackTrace();
        } finally {
    		close(in, src);
    		close(out,dst);
    	}
    }
    // Movers -------------------------------------------------------------------------------------

    /**
     * Move (rename) file. Any existing file at the destination will be overwritten.
     * @param source The file to be moved.
     * @param destination The new destination of the file.
     * @throws IOException If moving file fails.
     */
    public static void move(File source, File destination) {
        try {
			move(source, destination, true);
		} catch (IOException e) {
		}
    }

    /**
     * Move (rename) file with the option to overwrite any existing file at the destination.
     * @param source The file to be moved.
     * @param destination The new destination of the file.
     * @param overwrite Set whether to overwrite any existing file at the destination.
     * @throws IOException If the destination file already exists while <tt>overwrite</tt> is set
     * to false, or if moving file fails.
     */
    public static void move(File source, File destination, boolean overwrite) throws IOException {
        if (destination.exists()) {
            if (overwrite) {
                destination.delete();
            } else {
                throw new IOException(
                    "Moving file " + source.getPath() + " to " + destination.getPath() + " failed."
                        + " The destination file already exists.");
            }
        }

        mkdirs(destination);

        if (!source.renameTo(destination)) {
            throw new IOException(
                "Moving file " + source.getPath() + " to " + destination.getPath() + " failed.");
        }
    }

    // Filenames ----------------------------------------------------------------------------------

    /**
     * Trim the eventual file path from the given file name. Anything before the last occurred "/"
     * and "\" will be trimmed, including the slash.
     * @param fileName The file name to trim the file path from.
     * @return The file name with the file path trimmed.
     */
    public static String trimFilePath(String fileName) {
        return fileName
            .substring(fileName.lastIndexOf("/") + 1)
            .substring(fileName.lastIndexOf("\\") + 1);
    }
    
    //重写已存在的文件
    public static File createFile(File filePath, String fileName) throws IOException {
    	File file = new File(filePath, fileName);
    	return file;
    }
    /**
     * Generate unique file based on the given path and name. If the file exists, then it will
     * add "[i]" to the file name as long as the file exists. The value of i can be between
     * 0 and 2147483647 (the value of Integer.MAX_VALUE).
     * @param filePath The path of the unique file.
     * @param fileName The name of the unique file.
     * @return The unique file.
     * @throws IOException If unique file cannot be generated, this can be caused if all file
     * names are already in use. You may consider another filename instead.
     * 不重写已存在的文件
     */
    public static File uniqueFile(File filePath, String fileName) throws IOException {
    	
    	File file = new File(filePath, fileName);

    	if (file.exists()) {

            // Split filename and add braces, e.g. "name.ext" --> "name[", "].ext".
            String prefix;
            String suffix;
            int dotIndex = fileName.lastIndexOf(".");

            if (dotIndex > -1) {
                prefix = fileName.substring(0, dotIndex) + "[";
                suffix = "]" + fileName.substring(dotIndex);
            } else {
                prefix = fileName + "[";
                suffix = "]";
            }

            int count = 0;

            // Add counter to filename as long as file exists.
            while (file.exists()) {
                if (count < 0) { // int++ restarts at -2147483648 after 2147483647.
                    throw new IOException("No unique filename available for " + fileName 
                        + " in path " + filePath.getPath() + ".");
                }

                // Glue counter between prefix and suffix, e.g. "name[" + count + "].ext".
                file = new File(filePath, prefix + (count++) + suffix);
            }
        }

        return file;
    }
    
	
	  /** 强制删除文件
	  * try to delete given file , try 10 times 
	  * @param f 
	  * @return true if file deleted success, nor false; 
	  */  
	public static boolean forceDelete(String path,String filename)	{  
		if (!(path.substring(path.length() - 1 )).equals(SEPARATOR)){
			path = path + SEPARATOR;
		}
		path = path +  filename;
		File file = new File(path);
		
		boolean result = false;  
		int tryCount = 0;  
		while(!result && tryCount++ <10)  
		{  
			System.gc();  
			result = file.delete();  
		}  
		return result;  
	} 
	
	
  
    

    /**
     * Close the given inputstream of the given file.
     * @param input The inputstream to be closed.
     * @param file The inputstream's subject.
     */
    private static void close(InputStream input, File file) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Close the given outputstream of the given file.
     * @param input The outputstream to be closed.
     * @param file The outputstream's subject.
     */
    private static void close(OutputStream output, File file) {
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Close the given reader of the given file.
     * @param input The reader to be closed.
     * @param file The reader's subject.
     */
    private static void close(Reader reader, File file) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Close the given writer of the given file.
     * @param input The writer to be closed.
     * @param file The writer's subject.
     */
    private static void close(Writer writer, File file) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Handle error when closing file failed.
     * @param file The file which isn't been closed.
     * @param ioException The IOException being thrown.
     */
    private static void closingFailed(File file, IOException e) {
        String message = "Closing file " + file.getPath() + " failed.";
        // Do your thing with the exception and the message. Print it, log it or mail it.
        System.err.println(message);
        e.printStackTrace();
    }

    
    
	
   /**
	 * 写txt里的单行内容
	 * @param filePath  文件路径
	 * @param content  写入的内容
	 */
	public static void writeFile(String fileP,String content){
		String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";	//项目路径
		filePath = (filePath.trim() + fileP.trim()).substring(6).trim();
		PrintWriter pw;
		try {
			pw = new PrintWriter( new FileWriter(filePath));
			pw.print(content);
	        pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取txt里的单行内容
	 * @param filePath  文件路径
	 */
	public static String readTxtFile(String fileP) {
		try {
			
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";	//项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { 		// 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), encoding);	// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					return lineTxt;
				}
				read.close();
			}else{
				Console.showMessage("找不到指定的文件,查看此路径是否正确:"+filePath);
			}
		} catch (Exception e) {
			Console.showMessage("读取文件内容出错");
		}
		return "";
	}
   
}