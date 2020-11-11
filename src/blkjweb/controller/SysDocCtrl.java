package blkjweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.FileTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import blkjweb.service.*;
import blkjweb.utils.Const;

@Controller
public class SysDocCtrl extends AbstractBase {
	@Resource
	private SystemServiceImp systemService;

	// 除系统文档外，其他文档的描述信息都存在此库表中。文档本身保存在相应的文件夹下
	private final String tableName = "folder";

	// 初始化表格
	@RequestMapping(value = "/doc_center_init", produces = "application/json; charset=utf-8")
	@ResponseBody // 作用是将返回的对象作为响应，发送给页面
	public Object doc_center_init() {
		PageTool pd = new PageTool();
		pd = this.getPageData();

		// 获取对应文件所在文件夹名称
		String folderId = pd.getString("FolderId");
		if (StringTool.isNullEmpty(folderId))
			return "";

		String fileType = pd.getString("FileType");
		String where = "filetype='" + fileType + "'";

		if (folderId.startsWith("R1"))
			where = "filetype < '50' ";
		else if (folderId.startsWith("R2"))
			where = "filetype > '50' ";

		// 获取prmNames传递的参数
		int pageSize = pd.getInt("rows");// 取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); // 取得当前页数,这是jqgrid自身的参数
		String sort = pd.getString("sidx");// 排序列
		String order = pd.getString("sord");// 排序方式
		int index = (page - 1) * pageSize; // 开始记录数

		long totalRecord = systemService.queryCount(tableName, where); // 数据库中满足条件的总记录数
		long totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1; // 计算总页数

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		// 只从库中检索所需要的记录：LIMIT 5,10 检索记录行 6-15 第一个参数指定第一个返回记录行的偏移量，第二个参数指定返回记录行的最大数目
		lists = systemService.query(tableName, sort, order, where, new Object[] { index, pageSize });
		// 一次性全部读取，然后返回子集.index记录起始位置,注意表中记录是从1开始;越界则返回0条记录
		// lists = systemService.query(tableName, sort, order, where, index+1,
		// pageSize);

		// 数据转换[{},{}...]
		String rowList = ConvertTool.list2json((ArrayList<?>) lists);
		// 支持服务器端翻页的Json格式
		String json = "{\"page\":" + page + "," + " \"total\":" + totalPage + "," + " \"records\":" + totalRecord + ","
				+ " \"rows\":" + rowList + "}";

		return json;
	}

	// 上传单个文档及其他表格数据
	@RequestMapping(value = "doc_center_upload", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object doc_center_upload(HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		// 获取非文件表格数据
		String relativepath = multipartRequest.getParameter("relativepath");
		/*
		 * PageTool pd = new PageTool(); pd = this.getPageData(multipartRequest);
		 * Map<String, Object> returnMap = pd.getMap();
		 */

		// 读取上传的文件
		MultipartFile mf = multipartRequest.getFile("uploadFile");// 读取上传的文件
		String folderId = Const.getDocCenterPath() + Const.separator + relativepath + Const.separator;

		String filename = "";
		Long filesize = 0L;
		boolean flag = false;
		String responseStr = "文档成功上传！";
		if (mf != null && !mf.isEmpty()) {// 得到上传的文件的文件名
			// String content = file.getContentType(); //application/msexcel
			// String fileName = file.getName(); //文件选择框中 id所给出的文件名
			filename = mf.getOriginalFilename(); // 上传文件的原始文件名
			filesize = mf.getSize();
			flag = FileTool.copy(mf, folderId, filename);
		} else
			return "请选取要上传的文件！";

		if (flag) {// 写文件正确，下一步开始写记录
			Map<String, Object> mapRecord = new HashMap<String, Object>();
			mapRecord.put("ispublic", multipartRequest.getParameter("ispublic"));

			mapRecord.put("filetype", multipartRequest.getParameter("filetype"));
			mapRecord.put("relativepath", relativepath);
			mapRecord.put("filename", filename);
			mapRecord.put("uploaddate", getNowTime());
			mapRecord.put("filesize", filesize);

			mapRecord.put("name", multipartRequest.getParameter("name"));
			mapRecord.put("id", multipartRequest.getParameter("id"));
			mapRecord.put("uploader", multipartRequest.getParameter("uploader"));
			mapRecord.put("publishdate", multipartRequest.getParameter("publishdate"));
			mapRecord.put("author", multipartRequest.getParameter("author"));
			mapRecord.put("descript", multipartRequest.getParameter("descript"));

			int result = systemService.insert(tableName, mapRecord);
			if (result <= 0) {// 不成功
				responseStr = "文档上传失败！(可能原因：写库失败！)";
				FileTool.delFile(folderId, filename);// 成功与否不考虑！
			}
		} else
			responseStr = "文档上传失败！(可能原因：没有选择附件！)";

		return responseStr;// 返回给前台的提示信息
	}

	@RequestMapping(value = "doc_center_uploadSWF", produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object doc_center_uploadSWF(HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		// 获取非文件表格数据
		String id = multipartRequest.getParameter("id");
		String filename = multipartRequest.getParameter("filename");

		if (StringTool.isNullEmpty(filename) || StringTool.isNullEmpty(id))
			return "操作失败（原因：文档编号或文档名为空）！";
		filename = filename.substring(0, filename.lastIndexOf("."));

		// 读取上传的文件
		MultipartFile mf = multipartRequest.getFile("uploadFile");// 读取上传的文件
		String folderId = Const.getDocCenterPath() + Const.separator + "flexpaper" + Const.separator;
		boolean flag = false;
		String responseStr = "文档成功上传！";
		if (mf != null && !mf.isEmpty()) {// 得到上传的文件的文件名
			String oriFilename = mf.getOriginalFilename(); // 上传文件的原始文件名
			String extName = oriFilename.substring(oriFilename.lastIndexOf("."));
			if (!extName.endsWith(".swf"))
				return "请选取swf格式的文件进行上传！";
			filename = filename + extName;
			flag = FileTool.copy(mf, folderId, filename);
		} else
			return "请选取要上传的文件！";

		if (flag) {// 写文件正确，下一步开始写记录
			Map<String, Object> mapRecord = new HashMap<String, Object>();
			mapRecord.put("swfflag", "1");
			String where = "id='" + id + "'";
			int result = systemService.update(tableName, mapRecord, where);
			if (result < 0) {// 不成功
				responseStr = "文档上传失败！(可能原因：写库失败！)";
				FileTool.delFile(folderId, filename);// 成功与否不考虑！
			}
		} else
			responseStr = "文档上传失败！";

		return responseStr;// 返回给前台的提示信息
	}

	// 文档下载
	@RequestMapping(value = "/doc_center_Download")
	@ResponseBody // 作用是将返回的对象作为响应，发送给页面
	public void downloadFile() {
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String path = Const.getDocCenterPath() + Const.separator + pd.getString("Path") + Const.separator;

		String fileName = pd.getString("Filename");

		/*
		 * 暂不限制，都可以下载； if( !getAuthCode("90","1111")){ fileName = "default.pdf"; path =
		 * Const.getDocCenterPath() +"/temp/"; }
		 */
		printToClient(path, fileName);
	}




	// 删除目录或文件
	@RequestMapping(value = "/doc_center_delete")
	@ResponseBody // 作用是将返回的对象作为响应，发送给页面
	public Object delete() {
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();
		pd = this.getPageData();
		String path = Const.getDocCenterPath() + Const.separator + pd.getString("Path") + Const.separator;

		String fileName = pd.getString("Filename");
		String id = pd.getString("ID");

		String where = "id='" + id + "'";
		int result = systemService.delete(tableName, where);
		// boolean flag = false;
		if (result <= 0) {// 不成功
			code = "-1";
			message = "删除失败!";
		} else {// 删除文件
			FileTool.delFile(path, fileName);
			/* flag = */FileTool.fileExist(path + Const.separator + fileName);

			// 删除对应的SWF文件
			fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".swf";
			path = Const.getDocCenterPath() + Const.separator + "flexpaper" + Const.separator;
			FileTool.delFile(path, fileName);
		}

		/*
		 * 暂不考虑文件删除失败的情况 if (flag){//不成功 code = "-1"; message = "删除失败!"; }
		 */
		return message(code, message);
	}

	@RequestMapping(value = "/doc_center_editinit", produces = "application/json; charset=utf-8")
	@ResponseBody // 返回类对象没有问题；返回json数组？
	public Object doc_center_edit() {
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String id = pd.getString("KeyValue");
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		// 方法一：最多一条记录；查询条件由一个字段构成
		mapRecord = systemService.queryOne(tableName, "id", id);

		/*
		 * 方法二：最多一条记录；查询条件由多个字段构成 String where ="id='" + id + "'"; mapRecord =
		 * systemService.queryOne(tableName,where);
		 */
		if (mapRecord != null && mapRecord.size() > 0)
			return mapRecord;
		else
			return "";
	}

	// 保存编辑后的信息
	@RequestMapping(value = "/doc_center_editsave")
	@ResponseBody
	public Object doc_center_editsave() {
		String code = "2";
		String message = "成功更新!";

		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();

		String id = pd.getString("id");
		String oldID = pd.getString("oldID");

		boolean isUpdateKey = false;
		String where = "id='" + oldID + "'";
		if (!StringTool.isNullEmpty(id) && // 新编号不空 且 新旧编号不等
				!oldID.equals(id)) {
			isUpdateKey = true; // 更新主键
			mapRecord.put("id", id);
		}
		// 系统会自动过滤到无用字段
		int result = systemService.update(tableName, mapRecord, isUpdateKey, where);

		if (result >= 0) {// 成功更新记录
			String oldFilename = pd.getString("oldFilename");
			String newFilename = pd.getString("filename");
			String oldRelativepath = pd.getString("oldRelativepath");
			String newRelativepath = pd.getString("relativepath");

			String oldPath = Const.getDocCenterPath() + Const.separator + oldRelativepath + Const.separator;
			String newPath = Const.getDocCenterPath() + Const.separator + newRelativepath + Const.separator;
			// 对于更改文件名和移动文件操作不成功的情况，暂时不考虑
			if (!oldFilename.equalsIgnoreCase(newFilename)) { // 更改文件名
				FileTool.renameFile(oldPath, oldFilename, newFilename);// 成功返回true,不存在返回false

				// 更改swf文件名
				String swfPath = Const.getDocCenterPath() + Const.separator + "flexpaper" + Const.separator;
				String swfOldFilename = oldFilename.substring(0, oldFilename.lastIndexOf(".")) + ".swf";
				String swfNewFilename = newFilename.substring(0, newFilename.lastIndexOf(".")) + ".swf";
				FileTool.renameFile(swfPath, swfOldFilename, swfNewFilename);
			}

			if (!oldRelativepath.equalsIgnoreCase(newRelativepath)) {// 移动文件
				FileTool.moveFile(oldPath + newFilename, newPath + newFilename);// 成功返回true,不存在返回false
			}
		}

		if (result < 0) {// 不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code, message);
	}

}
