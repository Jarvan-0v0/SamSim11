package org.blkj.text;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.web.servlet.view.AbstractView;

import blkjweb.utils.Console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class JxlsExcelView extends AbstractView {
	private static final String CONTENT_TYPE = "application/vnd.ms-excel";

	private String templatePath;
	private String exportFileName;

	/**
	 * @param templatePath   模版相对于当前classpath路径
	 * @param exportFileName 导出文件名
	 */
	public JxlsExcelView(String templatePath, String exportFileName) {
		this.templatePath = templatePath;
		if (exportFileName != null) {
			try {
				exportFileName = URLEncoder.encode(exportFileName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		this.exportFileName = exportFileName;
		setContentType(CONTENT_TYPE);
	}

	@Override
	protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request,HttpServletResponse response)  
	{
			
		Context context = new Context(model);
		//MyLogger.showMessage(context);
		
		response.reset();// 清空response  
		response.setContentType(getContentType());
		response.setHeader("content-disposition","attachment;filename=" + exportFileName + ".xls");
		//response.addHeader("Content-Disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"),"iso-8859-1"));

		try{
		
			OutputStream os = response.getOutputStream();
			//ServletOutputStream os = response.getOutputStream();
			
			//从classpath下的相对路径中读取;得到Template的FIle
			InputStream is = getClass().getClassLoader().getResourceAsStream("/template/"+ templatePath );
			/*URL fileResource = this.getClass().getClassLoader().getResource("/template/"+templatePath);
			File file = new File(fileResource.toURI());
			InputStream is = new FileInputStream(file);
			*/
			
			//JxlsHelper.getInstance().processTemplate(is, os, context);
			JxlsHelper jh = JxlsHelper.getInstance();
			jh.processTemplate(is, os, context);//出错？ Cannot load XLS transformer
						
			//is.close();
		} catch (Exception e) {
			Console.showMessage(e.getMessage());
		}
	}
}
