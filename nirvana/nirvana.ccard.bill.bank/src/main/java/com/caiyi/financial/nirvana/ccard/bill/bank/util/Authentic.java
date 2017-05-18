package com.caiyi.financial.nirvana.ccard.bill.bank.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 *  这里有默认的认证服务器 也是我邮箱地址 163 的
 */

public class Authentic extends Authenticator {

	/** Creates a new instance of Authentic */

	public Authentic() {
	}

	public Authentic(String name, String password) {
		this.setUsername(name);
		this.setPwd(password);
	}

	// 这里是我重载的构造方法，你也可以不去重载它，但是这样写会有一定的灵活性

	private String username = "huasheng_85"; // 大多数是你邮件@前面的部分
	private String pwd = "*****"; // 这里的username
										// 是你要发送邮箱的帐号比如说我要用我的hackq@163.com 这

	// 去往外发 那么你这里的username就填写 hackq 也就是说你@前面的部分
	// pwd就是你发送邮箱的密码啦，这里还没有涉及到收件箱的 别急在下边

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(getUsername(), getPwd());
	}

	// 看到了吧protected PasswordAuthentication
	// getPasswordAuthentication()这个方法是最重要的了，我们发送
	// 邮件的时候就要靠它去验证我们的发送邮件服务器了
	// 下面的get()和set() 不是必须的，可以不写，写了可以增加程序的灵活性
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
