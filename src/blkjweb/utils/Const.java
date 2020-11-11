package blkjweb.utils;


public class Const {
	// 常量
	public static final String MODEL_PATH = "blkjweb.model.";// Bean所在目录，这些Bean用于动态产生对象
	public static final String SESSION_VALIDATE_CODE = "validateCode";// 验证码
	public static final String SESSION_USERNAME = "USERNAME";// 用户成功登陆后的用户名，Interceptor类中用
	public static final String SESSION_USERROLE = "USERROLE"; // 用户角色名字
	public static final String SESSION_USERROLECODE = "USERROLECODE";// 用户角色编码
	public static final String SESSION_USERAUTH = "USERAUTH"; // 权限
	public static final String SESSION_LOG4JNAME = "USERROLE";
	public static final String SESSION_UNITID = "0";// 登录用户所属单位编码

	private static final String lineSeparator = System.getProperty("line.separator");// 行分隔符
	public static final String separator = System.getProperty("file.separator");// 分隔符
	private static final String preSeparator = separator + "blkj" + separator;
	private final static String PATH_VLOG = preSeparator + "visit" + separator;// 访问日志文件的输出文件夹
	private final static String PATH_ELOG = preSeparator + "debug" + separator;// 调试日志文件的输出文件夹
	private final static String PATH_DBBACK = preSeparator + "dbbackup" + separator;// 默认的备份数据库文件的文件夹

	public static String getLineSeparator() {
		return lineSeparator;
	}

	/** 可通过sysconfig.properties配置的参数 */
	// 系统信息输出目录
	public static String logDir = "D:";
	// 调试程序时用。发行版应设置此项为false
	public static boolean showTip = true; // 是否执行System.out.println语句。 true执行
	// 用户访问网站时所产生的访问信息:L传给Logger; F文件；D数据库; 其他依据showTip的值而定
	private static String visitLog = "C";
	// 运行时所产生的信息:L传给Logger; F文件；D数据库(有时会有问题，因为系统异常时，数据库连接已经关闭); 其他依据showTip的值而定
	private static String runtimeLog = "C";

	public static boolean isShowTip() {
		return showTip;
	}

	public static void setShowTip(boolean showTip) {
		Const.showTip = showTip;
	}

	public static String getRuntimeLog() {
		return runtimeLog;
	}

	public static void setRuntimeLog(String runtimeLog) {
		Const.runtimeLog = runtimeLog;
	}

	public static String getVisitLog() {
		return visitLog;
	}

	public static void setVisitLog(String visitLog) {
		Const.visitLog = visitLog;
	}

	public static String getLogDir() {
		return logDir;
	}

	public static void setLogDir(String logDir) {
		Const.logDir = logDir;
	}

	public static String getVisitLogsPath() {// 保存访问信息的路径
		return logDir + PATH_VLOG;
	}

	public static String getDebugLogsPath() {// 保存运行时信息的路径
		return logDir + PATH_ELOG;
	}

	public static String getPathDbback() {// 用户不配置，用系统默认路径
		return logDir + PATH_DBBACK;
	}

	// 数据库相关的参数
	private static String ROOT = "root"; // 数据库帐号
	private static String PASSWORD = "123456"; // 数据库登陆密码
	private static String DB_binPath; // 数据库的bin目录 Linux：\ /usr/local/mysql/bin
	private static String DATABASE = ""; // 数据库名

	public static String getDATABASE() {
		return DATABASE;
	}

	public static void setDATABASE(String dATABASE) {
		DATABASE = dATABASE;
	}

	public static String getDB_binPath() {
		return DB_binPath;
	}

	public static void setDB_binPath(String dB_binPath) {
		DB_binPath = dB_binPath;
	}

	public static String getROOT() {
		return ROOT;
	}

	public static void setROOT(String rOOT) {
		ROOT = rOOT;
	}

	public static String getPASSWORD() {
		return PASSWORD;
	}

	public static void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	/**
	 * 位于服务器内的目录： 保存上传文件的临时目录：document/temp 供下载的excel文件模板的目录：document/template
	 */
	public static String appAbsPath = ""; // D:\programs\Tomcat8.0\webapps\easyui\
	public static final String UPLOAD_FILE_PATH = "bridgeImgs";

	public static final String DOC_CENTER_PATH = "document";// 文档中心文件夹
	public static final String BRIDGE_IMAGES_PATH = "bridgeImgs";// 用户所上传的劣化检查的有关图像
	public static final String QIAOHAN_IMAGES_PATH = "qiaohanImgs";// 桥涵图像
	public static final String TEMPLATE_FILE_PATH = "templatefiles";

	public static String getUploadFilePath() {
		return appAbsPath + UPLOAD_FILE_PATH;
	}

	public static String getTemplatePath() {
		return appAbsPath + "pages" + separator + TEMPLATE_FILE_PATH;
	}

	public static String getQiaohanImagesPath() {
		return appAbsPath + QIAOHAN_IMAGES_PATH;
	}

	public static String getBridgeImagesPath() {
		return appAbsPath + BRIDGE_IMAGES_PATH;
	}

	public static String getDocCenterPath() {
		return appAbsPath + DOC_CENTER_PATH;
	}

	public static String getAppAbsPath() {
		return appAbsPath;
	}

	public static void setAppAbsPath(String appAbsPath) {
		Const.appAbsPath = appAbsPath;
	}

	////// ?

	public static final boolean readNumberAsText = true;// 设置读取excel的数字类型值是否转换为text，true转换
	public static final String DB_CONPOOL_TYPE = "Proxool";// 数据库连接池类别 "Proxool"; "C3P0"; "OCI"; "jdbc-pool"

	// ?? 远程访问？？？？
	private final static String PATH_COURSE = "D:";// 课程资料的根目录

	public static String getPathCourse() {
		return PATH_COURSE;
	}

	// 目前只支持采用proxool连接池的用户名和账号是否加密; true表示加密
	// 解密方法见proxool.xml中<encrypt>com.encryption.DBDecrypt</encrypt>
	private static boolean DB_ENCRYPT = false;// true表示加密;

	public static boolean isDB_ENCRYPT() {
		return DB_ENCRYPT;
	}

	public static void setDB_ENCRYPT(boolean dB_ENCRYPT) {
		DB_ENCRYPT = dB_ENCRYPT;
	}

	// "Proxool" "BoneCP"; 使用Proxool配置文件获取用户名、密码、数据库表名信息。否则使用dbserver.properties
	private static String DBCONPOOL_TYPE = "jdbc-pool";
	private static String IP = "127.0.0.1";
	private static String PORT = "3306";

	public static String getIP() {
		return IP;
	}

	public static void setIP(String iP) {
		IP = iP;
	}

	public static String getPORT() {
		return PORT;
	}

	public static void setPORT(String pORT) {
		PORT = pORT;
	}

	public static String getDBCONPOOL_TYPE() {
		return DBCONPOOL_TYPE;
	}

	public static void setDBCONPOOL_TYPE(String dBCONPOOL_TYPE) {
		DBCONPOOL_TYPE = dBCONPOOL_TYPE;
	}

	// 数据库备份
	private static String osType = "Linux";// 操作系统 windows
	private static String DB_DIR = "D:";// 数据库管理系统所安装的根目录。目前没有用

	public static String getOsType() {
		return osType;
	}

	public static void setOsType(String osType) {
		Const.osType = osType;
	}

	public static String getDB_DIR() {
		return DB_DIR;
	}

	public static void setDB_DIR(String dB_DIR) {
		DB_DIR = dB_DIR;
	}

}
/*
 * 键 相关值的描述 java.version Java 运行时环境版本 java.vendor Java 运行时环境供应商 java.vendor.url
 * Java 供应商的 URL java.home Java 安装目录 java.vm.specification.version Java 虚拟机规范版本
 * java.vm.specification.vendor Java 虚拟机规范供应商 java.vm.specification.name Java
 * 虚拟机规范名称 java.vm.version Java 虚拟机实现版本 java.vm.vendor Java 虚拟机实现供应商
 * java.vm.name Java 虚拟机实现名称 java.specification.version Java 运行时环境规范版本
 * java.specification.vendor Java 运行时环境规范供应商 java.specification.name Java
 * 运行时环境规范名称 java.class.version Java 类格式版本号 java.class.path Java 类路径
 * java.library.path 加载库时搜索的路径列表 java.io.tmpdir 默认的临时文件路径 java.compiler 要使用的 JIT
 * 编译器的名称 java.ext.dirs 一个或多个扩展目录的路径 os.name 操作系统的名称 os.arch 操作系统的架构 os.version
 * 操作系统的版本 file.separator 文件分隔符（在 UNIX 系统中是“/”） path.separator 路径分隔符（在 UNIX
 * 系统中是“:”） line.separator 行分隔符（在 UNIX 系统中是“/n”） user.name 用户的账户名称 user.home
 * 用户的主目录 user.dir 用户的当前工作目录
 */