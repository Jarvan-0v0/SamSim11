package blkjweb.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.blkj.excel.ReadExcelFile;
import org.blkj.excel.core.TableData;
import org.blkj.text.ExcelView;
import org.blkj.utils.FileTool;
import org.blkj.utils.PageTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import blkjweb.service.SystemServiceImp;
import blkjweb.utils.Const;
import blkjweb.utils.ExcelToDB;
import blkjweb.utils.NameForChinese;


@Controller
public class FileCtr extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	//导出记录到excel文件
	@RequestMapping("/Excel_export")  
	public ModelAndView viewExcel( ) 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("tableName");
		String id = pd.getString("id");//肯定不空

		String item = pd.getString("selectedField") + id;//含有利用“,”分割的字符串,可以空	
		String [] items = item.split(",");
		String conditionStr = new String();//肯定不空，至少含有id
		for(String string: items){
			conditionStr = conditionStr + string;
			conditionStr = conditionStr + ","; 
		}
		conditionStr = conditionStr.substring(0,conditionStr.length() - 1) ;

		//对用户密码没有解密
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = systemService.query(tableName, conditionStr, "");
		
		if (result == null)
			return new ModelAndView("/common/Error");

		//Map<String,List<?>> model = new HashMap<String, List<?>>();
		Map<String,List<Map<String, Object>>> model = new HashMap<String,List<Map<String, Object>>>();
		model.put("dataList",result);//数据

		//英文表头
		String[] arr = conditionStr.split(",");
		List<String> headList = Arrays.asList(arr);

		Map<String,String> headMap = new HashMap<String,String>();//中文表头
		headMap = new NameForChinese().colTitleToChinese(tableName, headList);

		//ExcelView3用于SPringMVC3
		//return new ModelAndView(new ExcelView(headList), model);//英文标题
		return new ModelAndView(new ExcelView(headMap), model);//中文标题

		/*  利用jxls模板产生excel文件，有问题？  
		Map<String, Object> model = new HashMap<>();
		model.put("report_year", 2015);
		model.put("report_month", 8);
		List<Employee> userList = generateSampleEmployeeData();
		model.put("employees", userList);
		return new ModelAndView(new JxlsExcelView("template.xls","output"), model); 
		 */	    
	}
	
	@RequestMapping(value="/downloadTemplate")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public void downloadFile()
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String fileName = pd.getString("fileName"); 
		//String relPath = Const.DOC_CENTER_PATH + Const.separator + "template/";
		//String path = this.getRequest().getSession().getServletContext().getRealPath(relPath) ;
		String path = Const.getAppAbsPath() + Const.DOC_CENTER_PATH + Const.separator + "template"+ Const.separator; 
				
		printToClient(path,fileName);
	}
	
	/**对上传的excel文件，先保存在某目录下，然后进行导入数据库的操作，最后进行删除*/
	@RequestMapping(value="/uploadFile")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public ModelAndView uploadFile(HttpServletRequest request)
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("tableName");
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile file = multipartRequest.getFile("filePath");//读取上传的文件

		int infoCode = -1;
		if (file != null && ! file.isEmpty()){
			// 得到上传的文件的文件名
			//String content = file.getContentType(); //application/msexcel 
			//String fileName = file.getName(); //文件选择框中 id所给出的文件名 
			String oFileName = file.getOriginalFilename(); //上传文件的原始文件名
			
			boolean excelTypeFlag = true;//Office格式 2003标记为true,2007标记为false
			/** 检查文件名是否为空或者是否是Excel格式的文件xls或xlsx文件 */
			if (! FileTool.extExist(oFileName,"xls","xlsx")){
				infoCode = 1;//文件格式不对!
			}			
			else{  // 对文件的是否xlsx格式进行验证 
				if ( FileTool.extExist(oFileName,"xlsx")){
					excelTypeFlag = false;//excel 2007标记
				}
				//得到上传服务器的文件路径和文件名
				String relPath = Const.DOC_CENTER_PATH + Const.separator + "temp/";
				String dstPath = request.getSession().getServletContext().getRealPath(relPath); 
				String path = dstPath + oFileName;
				/*
				File localFile = new File(path); //创建File对象，参数为String类型，
				file.transferTo(localFile);//写上传文件的内容到服务器
				//读文件流到内存
				inputStream = file.getInputStream();
				byte[] b = new byte[1048576];
				int length = inputStream.read(b);
				*/
				boolean writeFileFlag = FileTool.copy(file,path,true);//返回true写成功（写入硬盘）
				boolean writeDBFlag = false;
				
				if(writeFileFlag){//写excel文件到mysql表中
					String selectedDBName = tableName;//数据库表名字
					writeDBFlag = executeDispatcher(path,selectedDBName,excelTypeFlag);	
					//无论成功与否均删除上传到服务器上的excel文件
					FileTool.delFile(dstPath,oFileName);
				}
				
				if(!writeFileFlag)
					infoCode = 4;//上传文件写盘错误！
				else{
					if(writeDBFlag)	infoCode = 2; //数据成功导入数据库!
					else infoCode = 3;//数据导入数据库失败!	
				}
			}
		}
		else
			infoCode = 0;//文件为空

		Map<String,String> map = new HashMap<String,String>();
		map.put("Status", "" + infoCode);
		return new ModelAndView(new MappingJackson2JsonView(),map);
	} 
	
	
	///////////////////////////////////////////////////////////////////////

	//写excel数据到给定的数据库
	/**excelTypeFlag = false;表示文件格式为：excel 2007;否则为：excel2003;  fileName是 excel文件的名称,包括路径；tableName表的名称*/
	private boolean executeDispatcher(String fileName, String tableName,boolean excelTypeFlag)
	{//创建表及写入数据
		boolean result = false;
		ReadExcelFile readExcel = new ReadExcelFile();
		TableData data = readExcel.tableProcessor(fileName,tableName, excelTypeFlag);
		//MyLogger.showMessage(data);
		if ( data == null)
			return result;

		String keySet = "id" ;//对应表的关键字集合  注意：统一用小写字符
		if ( tableName.equalsIgnoreCase("user")	){
			/**批量插入表数据规则："存在更新，不存在插入" 使用duplicate语句*/
			ArrayList<String> arrayList =  new ExcelToDB().SQLAssemblyForBatchInsertUpdate(data,keySet) ;
			result = systemService.batchInsertUpdate(arrayList);	
		}
		else if (tableName.equalsIgnoreCase("equipment")	){
			/**批量写数据到数据库 */
			ArrayList<String> arrayList = new ExcelToDB().SQLAssemblyForBatchInsert(data,keySet) ;
			result = systemService.batchInsertUpdate(arrayList);	
		}
		return result;
	}

}

	/*@RequestMapping("/SysLog_cvs") //OK 
	public void downloadCSV(HttpServletResponse response) throws IOException 
	{
		String csvFileName = "数.csv";
		//设置服务器端的编码
		response.setContentType("text/csv;charset=gbk");
		// creates mock data
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",new String(csvFileName.getBytes("gbk"),"iso-8859-1"));
		response.setHeader(headerKey, headerValue);
				
		Book book1 = new Book("Effective Java", "Java Best Practices",
				"Joshua Bloch", "Addision-Wesley", "0321356683", "05/08/2008",
				38);

		Book book2 = new Book("Head First Java", "Java for Beginners",
				"Kathy Sierra & Bert Bates", "O'Reilly Media", "0321356683",
				"02/09/2005", 30);

		Book book3 = new Book("Thinking in Java", "Java Core In-depth",
				"Bruce Eckel", "Prentice Hall", "0131872486", "02/26/2006", 45);

		Book book4 = new Book("X中文BM","Comprehensive guide to generics and collections",
				"Naftalin & Philip Wadler", "O'Reilly Media", "0596527756",
				"10/24/2006", 27);
		List<Book> listBooks = Arrays.asList(book1, book2, book3, book4);
		// uses the Super CSV API to generate CSV data from the model data 
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);
		String[] header = { "Title", "Description", "Author", "Publisher","isbn", "PublishedDate", "Price" };
		csvWriter.writeHeader(header);
		  
		for (Book aBook : listBooks){csvWriter.write(aBook, header);}
		csvWriter.close();
	}
	*/
	
	/*@RequestMapping("/SysLog_pdf")  //ok
	public ModelAndView viewPDF()
	{  
		Map<String,List<?>> model = new HashMap<String, List<?>>();   
		model.put("list", getStudents());             
		return new ModelAndView(new PDFView(), model);
	}  
*/

/*
spring内建的multipart支持网络程序文件上传。我们可以通过配置MultipartResolver来启动上传支持。它定义在org.springframework.web.multipart包中。
spring是通过使用Commons FileUpload插件来完成MultipartResolver的。
如果只是上传一个文件，只需要声明MultipartFile类型即可,如果上传的是多个文件，那么这里就要用MultipartFile[]数组来接收文件，并且还要指定@RequestParam注解
上传多个文件时，前台表单中的所有<input type="file" name=”files”/>的name都应该是files，否则参数里的files无法获取到所有上传的文件
public String addUser(User user, @RequestParam MultipartFile[] files, HttpServletRequest request) throws IOException{  
	for(MultipartFile myfile : files){   
		MyLogger.showMessage("文件长度: " + myfile.getSize());  
		MyLogger.showMessage("文件类型: " + myfile.getContentType());  
		MyLogger.showMessage("文件名称: " + myfile.getName());  
		MyLogger.showMessage("文件原名: " + myfile.getOriginalFilename());  
		MyLogger.showMessage("========================================");   
		//可以使用FileUtils来保存文件，这里不再列出代码
		//FileUtils.copyInputStreamToFile()方法会自动关闭IO流
		//FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, myfile.getOriginalFilename()));  
		}  
	}    
	return "success";  
}


*/