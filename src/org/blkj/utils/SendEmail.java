package org.blkj.utils;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;

import org.blkj.config.ReadPropertiesFile;

import blkjweb.utils.Console;

import com.sun.mail.util.MailSSLSocketFactory;

import java.security.GeneralSecurityException;
import java.util.*;

/**Send an Email via SMTP(发送邮件服务器) server using TLS connection.接收邮件（POP）服务器
 *  smtp=mail.bjtu.edu.cn smtpport=25
 *  smtp=smtp.gmail.com smtpport=587
 *  smtp.qq.com smtpport=465 或  587
 */

public class SendEmail {

	public static void main(String[] args) {
		new SendEmail();//.senderByQQ();
	}

	private String emailserverflag = "qq";
	private String emailServerPort;//端口
	private String emailSMTPserver;//"smtp.gmail.com"
	private String senderEmailID; //登录邮件服务器所需的用户名  发送者邮箱
	private String senderPassword;//登录邮件服务器所需的密码  qq邮箱生成的授权码authcode
	private String receiverEmailID;//接收者信箱

	private Message message; //邮件对象
	private String copyto;//抄送人的信箱
	private boolean htmlFlag = true;//html格式发送 true
	private String subject="测试";//设置邮件主题
	private String content ="hello";//设置邮件正文

	boolean sendFlag = true;

	public SendEmail() 
	{   //获取配置文件所在路径
		String propsFile = getClass().getResource("/").getPath() + "smtp.properties";
		ReadPropertiesFile config = new ReadPropertiesFile(propsFile);

		String tempValue = config.getValue("smtpserver").trim();
		
		if(tempValue != null && tempValue.length() !=0)
			emailSMTPserver = tempValue; //邮箱服务器
		else
			sendFlag = false;

		tempValue = config.getValue("smtpserverPort").trim();
		if(tempValue != null && tempValue.length() !=0)
			emailServerPort = tempValue; //邮箱服务器的端口
		else
			sendFlag = false;

		tempValue = config.getValue("senderEmailID").trim();
		if(tempValue != null && tempValue.length() !=0)
			senderEmailID = tempValue;
		else
			sendFlag = false;

		tempValue = config.getValue("senderPassword").trim();
		if(tempValue != null && tempValue.length() !=0)
			senderPassword = tempValue;
		else
			sendFlag = false;

		tempValue = config.getValue("emailserverflag").trim();
		if(tempValue != null && tempValue.length() !=0)
			emailserverflag = tempValue;
		
		tempValue = config.getValue("copyto").trim();
		if(tempValue != null && tempValue.length() !=0)
			copyto = tempValue;//抄送人的信箱

		tempValue = config.getValue("html").trim();
		if(tempValue != null && tempValue.length() !=0 && !tempValue.equalsIgnoreCase("true"))
			htmlFlag = false;
	}
	
	public boolean sender(String receiverEmailID, String subject, String content)
	{
		boolean result = true;
		this.receiverEmailID = receiverEmailID;
		this.subject = subject;
		this.content = content;
		if(emailserverflag.equalsIgnoreCase("qq"))
			result = senderByQQ();
		return result; 
	}
	
	
	/*
	 * 通过qq邮箱发送邮件,qq邮箱需要在设置里开启POP3/SMTP的授权，通过用户名+授权码方式才能发邮件
	 */
	private boolean senderByQQ() 
	{
		boolean result = true;
		MailSSLSocketFactory msf = null;
		try {
			msf = new MailSSLSocketFactory();
			msf.setTrustAllHosts(true);
		} catch (GeneralSecurityException e) {
			Console.showMessage(SendEmail.class.getSimpleName(), e.getMessage(), e);
		}

		Properties props = new Properties();//创建Properties 对象

		//props.setProperty("mail.debug", "true");//开启调试

		props.setProperty("mail.smtp.auth", "true");//访问服务器是否需要用户名和密码验证
		props.setProperty("mail.smtp.host", emailSMTPserver);//发送邮件服务器
		props.put("mail.smtp.port", emailServerPort);//smtpport
		props.setProperty("mail.transport.protocol","smtp");//发送邮件协议名称
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.socketFactory", msf);

		//连接超时时限
		props.put("mail.smtp.connectiontimeout", "10000"); //
		props.put("mail.smtp.timeout", "10000");	

		//用邮箱进行验证
		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getInstance(props, auth);

		message = new MimeMessage(session);//定义邮件信息
		try {
			message.setFrom(new InternetAddress(senderEmailID));//设置邮件发送者的地址
			message.setSubject(subject);//设置邮件标题
			message.setSentDate(new Date()); //设置邮件发送日期

			//设置邮件内容
			if(htmlFlag)//HTML格式发送
				message.setContent(content, "text/html;charset=utf-8");
			else//普通格式
				message.setText(content);//设置邮件正文

			//设置邮件接收方的地址
			message.addRecipient(RecipientType.TO, new InternetAddress(receiverEmailID));
			//设置邮件抄送方的地址
			if(copyto != null && copyto.length() != 0)
				message.setRecipients(javax.mail.Message.RecipientType.CC,InternetAddress.parse(copyto));
			//发送邮件
			Transport.send(message);
		} catch (Exception e) {
			Console.showMessage(SendEmail.class.getSimpleName(), e.getMessage(), e);
			result = false;
		}
		
		return  result;
	}
	// 通过用户名和密码进行验证
	private class SMTPAuthenticator extends javax.mail.Authenticator
	{
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(senderEmailID, senderPassword);
		}
	}


	/*??
	 * 通过gmail邮箱发送邮件
	 */
	public static void gmailSender() {

		// Get a Properties object
		Properties props = new Properties();
		//选择ssl方式
		gmailssl(props);

		final String username = "from@gmail.com";
		final String password = "password";
		Session session = Session.getDefaultInstance(props,
				new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		// -- Create a new message --
		Message msg = new MimeMessage(session);

		// -- Set the FROM and TO fields --
		try {
			msg.setFrom(new InternetAddress(username));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("to@qq.com"));
			msg.setSubject("Hello");
			msg.setText("How are you");
			msg.setSentDate(new Date());
			Transport.send(msg);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//gmail邮箱SSL方式
	private static void gmailssl(Properties props) {
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		props.put("mail.debug", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.auth", "true");
	}

	//gmail邮箱的TLS方式
	public static void gmailtls(Properties props) {
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
	}

}