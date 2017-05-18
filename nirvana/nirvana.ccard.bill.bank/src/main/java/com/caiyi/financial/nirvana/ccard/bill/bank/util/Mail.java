package com.caiyi.financial.nirvana.ccard.bill.bank.util;

import com.util.string.StringUtil;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mail {
	private String SmtpUid = "qinjian@021mobile.com";// service@m2.9188.com
	private String SmtpPwd = "qinjian123456";// service9188cp123
	private String SmtpServer = "smtp.exmail.qq.com";// smtp.m2.9188.com
	private String SenderMail = "qinjian@021mobile.com";// service@m2.9188.com
	private String SenderNick = "9188彩票网";

	public Mail() {
	}

	public String getSmtpUid() {
		return SmtpUid;
	}

	public void setSmtpUid(String smtpUid) {
		SmtpUid = smtpUid;
	}

	public String getSmtpPwd() {
		return SmtpPwd;
	}

	public void setSmtpPwd(String smtpPwd) {
		SmtpPwd = smtpPwd;
	}

	public String getSmtpServer() {
		return SmtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		SmtpServer = smtpServer;
	}

	public String getSenderMail() {
		return SenderMail;
	}

	public void setSenderMail(String senderMail) {
		SenderMail = senderMail;
	}

	public String getSenderNick() {
		return SenderNick;
	}

	public void setSenderNick(String senderNick) {
		SenderNick = senderNick;
	}

	/**
	 * 格式化 Name <email@address.com> 的地址
	 * 
	 * @param name
	 *            名字
	 * @param email
	 *            Email地址
	 * @return 格式化的地址
	 */
	public static String formatAddress(String name, String email) {
		if (StringUtil.isEmpty(name)) {
			return email;
		}
		try {
			return String.format("%1$s <%2$s>", MimeUtility.encodeText(name, "GBK", "B"), email);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return email;
	}

	/**
	 * 重载的sendMsg()方法，这个方法的参数比较多，这里是设置自己的发送邮件的smtp服务器了 也就是说不用我的那个默认的资源 参数说明name
	 * 发送邮箱的帐号 password发送邮箱的密码 smtp 发送邮箱的stmp服务器地址 例smtp.163.com sendAddress
	 * 发送邮箱mail地址 inceptAddress 接收邮箱地址 title content 同上
	 * 
	 * @return
	 */
	public boolean sendMsg(String inceptAddress, String title, String content) {
		if (!verifyEmail(inceptAddress)) {
			return false;
		}
		boolean sessionDebug = false;
		InternetAddress[] address = null;
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
		try {
			// 设定所要用的Mail 服务器和所使用的传输协议
			java.util.Properties props = System.getProperties();
			props.put("mail.host", this.getSmtpServer());
			props.put("mail.transport.protocol", this.getSmtpServer());
			props.put("mail.smtp.auth", "true");

			// 产生新的Session 服务
			Authentic auth = new Authentic(this.getSmtpUid(), this.getSmtpPwd());
			Session mailSession = Session.getDefaultInstance(props, auth);
			mailSession.setDebug(sessionDebug);
			Message msg = new MimeMessage(mailSession);

			// 设定传送邮件的发信人
			try {
				msg.setFrom(new InternetAddress(this.getSenderMail(), MimeUtility.encodeText(this.getSenderNick(), "GB2312", "B")));//
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// 发件人邮件地址以及名称
			// 设定传送邮件至收信人的信箱
			address = InternetAddress.parse(inceptAddress, false);
			msg.setRecipients(Message.RecipientType.TO, address);
			// 设定信中的主题
			msg.setSubject(title);
			// 设定送信的时间
			 msg.setSentDate(new Date());

			MimeBodyPart bodyPart1 = new MimeBodyPart();
			bodyPart1.setContent(content, "text/html;charset=gb2312");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPart1);

			msg.setContent(multipart);
			Transport.send(msg);
			// System.out.println("邮件已顺利传送");
		} catch (MessagingException mex) {
			mex.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * 重载的sendMsg()方法，这个方法的参数比较多，这里是设置自己的发送邮件的smtp服务器了 也就是说不用我的那个默认的资源 参数说明name
	 * 发送邮箱的帐号 password发送邮箱的密码 smtp 发送邮箱的stmp服务器地址 例smtp.163.com sendAddress
	 * 发送邮箱mail地址 inceptAddress 接收邮箱地址 title content 同上
	 * 
	 * @return
	 */
	public boolean sendMsg(String inceptAddress, String title, String content, Vector file) {
		if (!verifyEmail(inceptAddress)) {
			return true;
		}
		boolean sessionDebug = false;
		InternetAddress[] address = null;
		try {
			// 设定所要用的Mail 服务器和所使用的传输协议
			java.util.Properties props = System.getProperties();
			props.put("mail.host", this.getSmtpServer());
			props.put("mail.transport.protocol", this.getSmtpServer());
			props.put("mail.smtp.auth", "true");

			// 产生新的Session 服务
			Authentic auth = new Authentic(this.getSmtpUid(), this.getSmtpPwd());
			Session mailSession = Session.getDefaultInstance(props, auth);
			mailSession.setDebug(sessionDebug);
			Message msg = new MimeMessage(mailSession);

			// 设定传送邮件的发信人
			try {
				msg.setFrom(new InternetAddress(this.getSenderMail(), MimeUtility.encodeText(this.getSenderNick(), "GB2312", "B")));//
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// 发件人邮件地址以及名称
			// 设定传送邮件至收信人的信箱
			address = InternetAddress.parse(inceptAddress, false);
			msg.setRecipients(Message.RecipientType.TO, address);
			// 设定信中的主题
			msg.setSubject(title);
			// 设定送信的时间
			 msg.setSentDate(new Date());

			MimeBodyPart bodyPart1 = new MimeBodyPart();
			bodyPart1.setContent(content, "text/html;charset=gb2312");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPart1);

			// 向Multipart添加附件
			String filename = "";// 附件文件名
			Enumeration efile = file.elements();
			while (efile.hasMoreElements()) {
				MimeBodyPart mbpFile = new MimeBodyPart();
				filename = efile.nextElement().toString();
				FileDataSource fds = new FileDataSource(filename);
				mbpFile.setDataHandler(new DataHandler(fds));
				try {
					mbpFile.setFileName(MimeUtility.encodeText(fds.getName()));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 向MimeMessage添加（Multipart代表附件）
				multipart.addBodyPart(mbpFile);
			}

			msg.setContent(multipart);
			Transport.send(msg);
			// System.out.println("邮件已顺利传送");
		} catch (MessagingException mex) {
			mex.printStackTrace();
			return false;
		}
		return true;

	}

	public final static boolean verifyEmail(String email) {
		String str = "^([a-z0-9A-Z]+[-_|.]*)+[a-z0-9A-Z]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		if (m.matches()) {
			return true;
			// String[] mail= email.split("@");
			// try
			// {
			// InetAddress IP = InetAddress.getByName(mail[1]);
			// System.out.println("IP Address is--->;"+IP);
			// return true;
			// }
			// catch(UnknownHostException e)
			// {
			// e.printStackTrace();
			// return false;
			// }
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {

//		InternetAddress[] address = InternetAddress.parse("240480363@qq.com,wenshiliang@9188.com", false);
//		System.out.println(address);
//		for(InternetAddress a: address){
//			System.out.println(a);
//		}


		Mail m = new Mail();
		String smtpUid = "service@m3.9188.com";// service@m2.9188.com
		String smtpPwd = "service9188cp123";// service9188cp123
		String smtpServer = "m3.9188.com";// smtp.m2.9188.com
		String senderMail = "service@m3.9188.com";// service@m2.9188.com
		String senderNick = "9188彩票网";
		m.setSmtpUid(smtpUid);
		m.setSmtpPwd(smtpPwd);
		m.setSmtpServer(smtpServer);
		m.setSenderMail(senderMail);
		m.setSenderNick(senderNick);
//
		//
		m.sendMsg("240480363@qq.com,wenshiliang@9188.com", "购彩上9188.com彩票网", "购彩上9188.com彩票网123456 <a href=\"http://www.9188.com/useraccount/\">www.9188.com</a>");
		System.out.println("=====end。");
	}
}
