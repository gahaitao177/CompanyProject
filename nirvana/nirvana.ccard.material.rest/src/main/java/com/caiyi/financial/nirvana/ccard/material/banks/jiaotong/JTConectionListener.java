package com.caiyi.financial.nirvana.ccard.material.banks.jiaotong;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

import java.io.IOException;

public class JTConectionListener extends FalsifyingWebConnection {
	//public static Logger logger = LoggerFactory.getLogger("materialBeanImpl");
	public JTConectionListener(WebClient webClient)	throws IllegalArgumentException {
		super(webClient);
	}
	
	@Override 
	public WebResponse getResponse(WebRequest request) throws IOException { 
		WebResponse response = super.getResponse(request); 
		String url = response.getWebRequest().getUrl().toString(); 
		System.out.println("下载文件链接：" + url); 
		/*
		if (url.contains("https://track.bankcomm.com:8443/customerMonitor/monitorService")) {
			logger.info("下载文件链接：" + url); 
			//JiaoTongHelper.curl=url;
		}
		*/
		return response; 
	}	

}
