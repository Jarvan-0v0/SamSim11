package blkjweb.controller;

/**ArrayList实现，虽然底层是数组实现，但效率要低于数组*/

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.DateTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.codehaus.jackson.map.util.JSONPObject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import blkjweb.utils.Console;

public abstract class AbstractBase {
	protected Map<String, String> message(boolean result, String message) {
		int code = 2;
		if (!result)// 不成功
			code = -1;
		return message(code, message);
	}

	protected Map<String, String> message(int result, String tip) {
		String code = "1";
		String message = "操作成功!";
		if (result <= 0) {// 不成功
			code = "-1";
			message = "操作失败!";
		}

		return message(code, tip + message);
	}


	protected String getSQLWhere(String Ids) {
		String where = "";
		if (StringTool.isNullEmpty(Ids))
			return where;

		String[] idArry = StringTool.stringToArray(Ids, ",");

		if (idArry != null && idArry.length > 0) {

			for (int i = 0; i < idArry.length; i++) {
				String[] keyvalue = StringTool.stringToArray(idArry[i], ":");
				if (keyvalue != null && (keyvalue.length % 2 == 0)) {// 只有两个元素
					if (where.isEmpty())
						where = keyvalue[0] + "='" + keyvalue[1] + "'";
					else
						where = where + " AND " + keyvalue[0] + "='" + keyvalue[1] + "'";
				}
			}
		}
		return where;
	}

	protected Map<String, String> message(int result) {
		String code = "1";
		String message = "操作成功!";
		if (result <= 0) {// 不成功
			code = "-1";
			message = "操作失败!";
		}
		return message(code, message);
	}

	protected Map<String, String> message(String code, String message) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Code", code);
		map.put("Message", message);
		return map;
	}

	// 得到request对象
	protected HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		return request;
	}

	// 得到Response对象
	protected HttpServletResponse getResponse() {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		return response;
	}

	void xbm() {
	};

	protected void dateToYear(Map<String, Object> mapRecord, String fieldName) {
		if (mapRecord != null && mapRecord.size() > 0) {
			// 从库中读取的date格式：YYYY-MM-DD；页面只需YYYY
			Date date = (Date) mapRecord.get(fieldName);
			String year = DateTool.getYear(date);
			mapRecord.put(fieldName, year);
		}
		return;
	}

	/**
	 * map: key有:id和name; 依据id(key)的值（value）获取对应name(key2)的值
	 */
	protected String getNameByID4List(List<Map<String, Object>> lists, String id, String value, String key) {
		String result = "";
		if (StringTool.listIsNullEmpty(lists))
			return result;

		boolean breakFlag = false;
		for (Map<String, Object> map : lists) {// lists的每个元素
			for (String keyT : map.keySet()) {
				String valueT = (String) map.get(keyT);
				/*
				 * 等同上述两天语句 for (Map.Entry<String, Object> m : map.entrySet())
				 * {//每个map的元素:key（id,name） String keyT = m.getKey(); String valueT =
				 * (String)m.getValue();
				 */
				if (keyT.equalsIgnoreCase(id) && valueT.equalsIgnoreCase(value)) {
					result = (String) map.get(key);
					breakFlag = true;
					break;
				}
			}
			if (breakFlag)
				break;
		}
		return result;
	}

	// SpringMVC+Shiro+RedisCluster搭建session统一管理
	/**
	 * 将一些数据放到ShiroSession中,以便于其它地方使用
	 * 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到
	 */
	protected void setSessionAttribute(Object key, Object value) {
		Subject currentUser = SecurityUtils.getSubject();
		if (null != currentUser) {
			Session session = currentUser.getSession();
			if (null != session) {
				session.setAttribute(key, value);
			}
		}
	}

	// Shiro提供了 SecurityManager 实例的保存和获取的方法，以及创建Subject的方法
	protected Object getSessionAttribute(String key) {
		Session session = null;
		Subject currentUser = SecurityUtils.getSubject();
		if (null != currentUser) {
			session = currentUser.getSession();
			if (null != session)
				return session.getAttribute(key);
		}
		return session;
	}

	protected Object returnObject(PageTool pd, Map<?, ?> map) {
		if (pd.containsKey("callback")) {// 此分支没有验证
			String callback = pd.get("callback").toString();
			return new JSONPObject(callback, map);
		} else {
			return map;
		}
	}

	// 得到ModelAndView
	protected ModelAndView getModelAndView() {
		return new ModelAndView();
	}

	// 获取上传文件页面上非文件表格数据 （MultipartHttpServletRequest上传文件）
	protected PageTool getPageData(MultipartHttpServletRequest multipartRequest) {
		return new PageTool(multipartRequest);
	}

	// 得到PageData 使用HttpServletRequest协议获取的常规数据（不含文件上传）
	protected PageTool getPageData() {
		return new PageTool(this.getRequest());
	}

	protected void printToClient(String path, String fileName) {
		HttpServletResponse response = this.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		String contentType = "application/octet-stream";
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			// request.setCharacterEncoding("UTF-8");
			long fileLength = new File(path + fileName).length();
			response.setContentType(contentType);
			response.setHeader("Content-disposition", "attachment; filename=" + ConvertTool.getISO(fileName));
			response.setHeader("Content-Length", String.valueOf(fileLength));

			bis = new BufferedInputStream(new FileInputStream(path + fileName));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (Exception e) {
			Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
			}
		}
	}

	protected void printToClient(String info) {
		HttpServletResponse response = this.getResponse();
		PrintWriter out = null;
		try {
			response.setHeader("Charset", "UTF-8");
			// response.setHeader("ContentType", "text/json");
			// response.setHeader("ContentType", "xml");
			response.setContentType("text/html;charset=UTF-8");
			// 禁止缓存
			// response.addHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.write(info);// print
			out.flush();
			// response.getWriter().write(info);
		} catch (Exception e) {
			Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
		} finally {
			if (out != null)
				out.close();
		}
	}

	// 得到用户的IP地址
	protected String getRemoteIP() {
		return this.getRequest().getRemoteAddr();
	}

	// 通过ip来获取mac地址
	protected String getMACAddress(String ip) {
		String str = "";
		String macAddress = "";
		try {
			Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
			InputStreamReader ir = new InputStreamReader(p.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					if (str.indexOf("MAC Address") > 1) {
						macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());
						break;
					}
				}
			}
		} catch (IOException e) {
			Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
		}
		return macAddress;
	}

	protected String getNowTime() {
		Date theDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// yyyy-MM-dd(HH-MM)
		String nowTime = sdf.format(theDate);
		return nowTime;
	}

	// 获取特定的评价等级。 属于专用方法。使用map自动排序除的结果是：A1 AA B C D 。 无法直接获取满足系统所要求的值
	protected String getFinalJudge(Map<String, Object> mapRecord) {
		String result = "";

		if (!StringTool.isNull(mapRecord))
			for (Map.Entry<String, Object> entry : mapRecord.entrySet()) {
				String key = (String) entry.getKey();
				if (key.indexOf("judge") != -1) {// 包含
					String value = (String) entry.getValue();
					if (StringTool.isNullEmpty(value) || result.indexOf(value) != -1)// 是空 或 已存在
						continue;
					else
						result += value + ",";
				}
			}

		if (result.indexOf("AA") != -1)
			return "AA";
		else if (result.indexOf("A1") != -1)
			return "A1";
		else if (result.indexOf("B") != -1)
			return "B";
		else if (result.indexOf("C") != -1)
			return "C";
		else if (result.indexOf("D") != -1)
			return "D";
		return result;
	}

	// 获取最终的总评价等级。 属于专用方法
	protected String getFinalTotalJudge(Map<String, Object> mapRecord, String level, String field) {
		String result = level;

		if (!StringTool.isNull(mapRecord))
			for (Map.Entry<String, Object> entry : mapRecord.entrySet()) {
				String key = (String) entry.getKey();
				if (!key.equalsIgnoreCase(field) && !key.equalsIgnoreCase("qiaoliangjudge")) {
					String value = (String) entry.getValue();

					if (StringTool.isNullEmpty(value) || result.indexOf(value) != -1)// 已存在
						continue;
					else
						result += value + ",";
				}
			}

		if (result.indexOf("AA") != -1)
			return "AA";
		else if (result.indexOf("A1") != -1)
			return "A1";
		else if (result.indexOf("B") != -1)
			return "B";
		else if (result.indexOf("C") != -1)
			return "C";
		else if (result.indexOf("D") != -1)
			return "D";
		return result;
	}

	// 11.5 修订版
	protected String getFinalTotalJudgeM(Map<String, Object> mapRecord, String level) {
		String result = level;

		if (!StringTool.isNull(mapRecord))
			for (Map.Entry<String, Object> entry : mapRecord.entrySet()) {
				String value = (String) entry.getValue();

				if (StringTool.isNullEmpty(value) || result.indexOf(value) != -1)// 已存在
					continue;
				else
					result += "," + value;
			}

		if (result.indexOf("AA") != -1)
			return "AA";
		else if (result.indexOf("A1") != -1)
			return "A1";
		else if (result.indexOf("B") != -1)
			return "B";
		else if (result.indexOf("C") != -1)
			return "C";
		else if (result.indexOf("D") != -1)
			return "D";
		return result;
	}

}
//获取用户的操作权限代码  此函数已被authority_check()所替代
//protected /*String[]*/boolean getAuthCode(String module,String op)
/*
 * { //String [] result = new String[2]; 调用：String[] authCode = getAuthCode();
 * boolean result = true; String authCode =
 * (String)getSessionAttribute(Const.SESSION_USERAUTH); String moduleID = "";
 * String ops = ""; if (authCode != null && authCode.length() == 6) { moduleID =
 * authCode.substring(0,2); ops = authCode.substring(2,6); //result[0] =
 * moduleID; //result[1] = ops; } //10：工队 ; 90总部; 后四位表示增 删 改 查：1表示可以进行对应的操作，0表示否
 * if( ! moduleID.equals(module) || ! ops.equals(op) ) {//同用户传递过来的参数不完全相同 result
 * = false; } return result; }
 */