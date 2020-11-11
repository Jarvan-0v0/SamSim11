package org.blkj.file;
//SFTP传输

/**
 * Apache commons VFS支持如下文件格式：
 * FTP 、Local Files 、HTTP and HTTPS 、SFTP 、Temporary Files 、Zip, Jar and Tar (uncompressed, tgz or tbz2) 、
 * gzip and bzip2 、res 、ram 、mime。
 * **/
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.cache.DefaultFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs2.provider.sftp.SftpFileProvider;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.zip.ZipFileProvider;

//http://blog.csdn.net/ffm83/article/details/42080387
//http://blog.163.com/shy_zzu_2008/blog/static/751467362011111922038453/
//http://biancheng.dnbcw.info/java/89765.html

/**http://commons.apache.org/proper/commons-vfs/  :Commons Virtual File System*/
public class FileManager {

	private String _sourceroot = "C:/vfsroot";  
	  
	  private String _targetroot = "sftp://xxx:xxx@xxxx/doc-root/myaoVfstest/";  
	  
	  // b1:f1:ef:26:3e:5f:a5:0d:70:fa:5e:df:d9:6b:55:41  
	  
	  private FileObject localfs, targetfs;  
	  
	  private DefaultFileSystemManager vfsmgr;  
	  
	  FileManager() {  
	    try {  
	      init();  
	    } catch (FileSystemException e) {  
	  
	      e.printStackTrace();  
	    }  
	  }  
	  
	  void init() throws FileSystemException {  
	  
	    vfsmgr = getDefaultFileSystemManager();  
	  
	  }  
	  
	  void moveFile(String sourcePath, String targetPath)  
	      throws FileSystemException {  
	  
	    localfs = vfsmgr.resolveFile(sourcePath);  
	  
	    if (!localfs.exists()) {  
	  
	      localfs.createFolder();  
	      // localfs.  
	    }  
	    // vfsmgr.  
	    FileSystemOptions opts = new FileSystemOptions();  
	    SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");  
	    targetfs = vfsmgr.resolveFile(targetPath, opts);  
	  
	    if (!targetfs.exists()) {  
	  
	      targetfs.createFolder();  
	    }  
	  
	    try {  
	      long startTime = System.currentTimeMillis();  
	      targetfs.copyFrom(localfs, Selectors.SELECT_FILES);  
	      
	      // TODO 要想办法用遍历的方法来拷贝文件，否则无法留下拷贝的细节。在装载数据……  
	  
	      // FileObject[] flist = localfs.getChildren();  
	      // for (int i = 0; i < flist.length; i++) {  
	      // FileObject tmp = vfsmgr.resolveFile(targetfs, flist[i].getName()  
	      // .getBaseName());  
	      // if (!tmp.exists()) {  
	      // tmp.createFile();  
	      // }  
	      //  
	      // VfsMutiMove t = new VfsMutiMove(flist[i], tmp);  
	      // t.run();  
	      // // tmp.copyFrom(flist[i], Selectors.SELECT_SELF);  
	      // }  
	  
	      long endTime = System.currentTimeMillis();  
	      //System.out.println(this.getClass().getName());  
	      //System.out.println("Cost time(ms:):" + (endTime - startTime));  
	  
	    } catch (FileSystemException e) {  
	  
	      e.printStackTrace();  
	    }  
	  
	  }  
	  
	  private DefaultFileSystemManager getDefaultFileSystemManager() {  
	  
	    DefaultFileSystemManager mgr = new DefaultFileSystemManager();  
	    // SFTP 供应者  
	    SftpFileProvider fp = new SftpFileProvider();  
	    FileSystemOptions t = new FileSystemOptions();  
	  
	    // ZIP 供应者  
	    ZipFileProvider zp = new ZipFileProvider();  
	    // 缺省本地文件供应者  
	    DefaultLocalFileProvider lf = new DefaultLocalFileProvider();  
	  
	    try {  
	      // common-vfs 中 文件管理器的使用范例  
	      mgr.addProvider("sftp", fp);  
	      mgr.addProvider("zip", zp);  
	      mgr.addProvider("file", lf);  
	      mgr.setFilesCache(new DefaultFilesCache());  
	      mgr.init();  
	  
	    } catch (FileSystemException e) {  
	      // 此处应该改为log  
	      e.printStackTrace();  
	    }  
	  
	    return mgr;  
	  }  
	  
	  void getWorkspaceFromProperties() {  
	  
	    /* 
	     * Properties tmpProperties; File tmpfile = new 
	     * File("workspace.properties"); 
	     */  
	  
	  }  
	  
	  /** 
	   * @param args 
	   * @throws Exception 
	   */  
	  public static void main(String[] args) throws Exception {  
	  
		  FileManager op = new FileManager();  
	  
	    op.moveFile("C:/downloads",  
	          "sftp://xxxx:xxxx@192.168.1.16/doc-root/myaoVfstest/");  
	  
	  }  
	  
}
