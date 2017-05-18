package com.caiyi.financial.nirvana.discount.ccard.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Model extends JSONObject implements Serializable{
	private static final long serialVersionUID = 1L;
	 public static List<String> CATEGOERY = new ArrayList<String>(); 
	 
	 public static Map<Integer,String> HANDLE_CREDIT = new HashMap<Integer,String>(); 
	 static{
		//SELECT  'CATEGOERY.add("'||CCATEGOERYNAME||'");'  FROM TB_CATEGORY;
		//select  'HANDLE_CREDIT.put('||IBANKID||', "'||cardaddr||'");' from tb_handle_credit order by IBANKID
		 CATEGOERY.add("美食");
		 CATEGOERY.add("小吃快餐");
		 CATEGOERY.add("咖啡");
		 CATEGOERY.add("火锅");
		 CATEGOERY.add("日韩料理");
		 CATEGOERY.add("蛋糕甜点");
		 CATEGOERY.add("自助餐");
		 CATEGOERY.add("烧烤");
		 CATEGOERY.add("西餐");
		 CATEGOERY.add("粤菜");
		 CATEGOERY.add("川湘菜");
		 CATEGOERY.add("新疆菜");
		 CATEGOERY.add("江浙菜");
		 CATEGOERY.add("其他");
		 CATEGOERY.add("电影");
		 CATEGOERY.add("娱乐");
		 CATEGOERY.add("KTV");
		 CATEGOERY.add("酒吧");
		 CATEGOERY.add("足疗按摩");
		 CATEGOERY.add("运动健身");
		 CATEGOERY.add("旅游");
		 CATEGOERY.add("其他");
		 CATEGOERY.add("购物");
		 CATEGOERY.add("百货超市");
		 CATEGOERY.add("数码电器");
		 CATEGOERY.add("家居装潢");
		 CATEGOERY.add("网购");
		 CATEGOERY.add("其他");
		 CATEGOERY.add("丽人");
		 CATEGOERY.add("美发");
		 CATEGOERY.add("美容spa");
		 CATEGOERY.add("写真");
		 CATEGOERY.add("瑜伽");
		 CATEGOERY.add("其他");
		 CATEGOERY.add("汽车");
		 CATEGOERY.add("汽车维修美容");
		 CATEGOERY.add("汽车保险");
		 CATEGOERY.add("加油");
		 CATEGOERY.add("其他");
		 CATEGOERY.add("酒店");
		 CATEGOERY.add("星级酒店");
		 CATEGOERY.add("快捷酒店");
		 CATEGOERY.add("其他");
		 CATEGOERY.add("生活");
		 CATEGOERY.add("培训课程");
		 CATEGOERY.add("医疗保健");
		 CATEGOERY.add("母婴亲子");
		 CATEGOERY.add("其他");
		 CATEGOERY.add("航空");
		 CATEGOERY.add("机票优惠");
		 CATEGOERY.add("机场专车");
		 CATEGOERY.add("其他");
		 
		 HANDLE_CREDIT.put(1, "https://wap.cgbchina.com.cn/creditCardApplyIn.do?shortUrl=BKbH45Q");
		 HANDLE_CREDIT.put(2, "https://creditcard.ecitic.com/citiccard/applycard/card/index.jsp?sid=SJWXBK&mobid=wc&openId=oQEeUjom5HHEfip4iuwj4wSef71Y&uName=%E9%AD%8F%E4%B8%BD%E4%BD%B3+BH_+Torie&sex=2&city=%E9%97%B5%E8%A1%8C&province=%E4%B8%8A%E6%B5%B7&country=%E4%B8%AD%E5%9B%BD&headimgurl=http%3A%2F%2Fwx.qlogo.cn%2Fmmopen%2FajNVdqHZLLA1VQ7veDtL6uWsz65vjtQo9dQ4fJAhOR7X3CpEKjPdBY5Q8u8mpPYUezG9yEeFwsseKSkiaJ93PiateFWxPb6AweLgu8U0qZroU%2F0&timstamp=1438759649712");
		 HANDLE_CREDIT.put(3, "https://xyk.cebbank.com/cebmms/apply/ps/apply-card-list.htm");
		 HANDLE_CREDIT.put(4, "http://abccc.vfengche.cn/abcCarCenter/card/classic.htm");
		 HANDLE_CREDIT.put(5, "https://www.citibank.com.cn/sim/ICARD/forms/mobile/index.html?code=SNWECHAT");
		 HANDLE_CREDIT.put(6, "http://www.sc.com/cn/credit-cards/");
		 HANDLE_CREDIT.put(7, "https://wap-ebank.pingan.com/weixin/modules/online_apply_card/index.html?scc=920000002#/showCards");
		 HANDLE_CREDIT.put(8, "https://onlinepay.cupdata.com/wxhx/apply.do?action=cardActInit&bankNum=6304&userId=oTBePjiR7bc0xZgSHhpIcSzsMKio");
		 HANDLE_CREDIT.put(9, "https://onlineapp.spdbccc.com.cn/ccoa/newccoapage/home.jsp?customerType=1&pid=website001&cid=website001");
		 HANDLE_CREDIT.put(10, "https://3g.cib.com.cn/app/00285.html");
		 HANDLE_CREDIT.put(11, "http://cmbc.vip.txooo.com/Card/CatchCard.html");
		 HANDLE_CREDIT.put(12, "");
		 HANDLE_CREDIT.put(13, "http://creditcard.ccb.com/ccbweixin/credit_card_application/form.html");
		 HANDLE_CREDIT.put(15, "https://card.bank-of-china.com/wechatebank/booking/list.do");
		 HANDLE_CREDIT.put(16, "https://creditcardapp.bankcomm.com/applynew/front/apply/new/identity.html");
		 HANDLE_CREDIT.put(17, "https://onlinepay.cupdata.com/weixin/apply.do?action=applyCCInit&bankNum=6413&userId=o37EKj7w3BJQUOGyUqQcgqBAcQuA");
		 HANDLE_CREDIT.put(18, "https://onlinepay.cupdata.com/weixin/apply.do?action=applyCCInit&bankNum=8479&userId=oyzTvjuAPzXcDjnoLVKBDz2PpDwM");
		 HANDLE_CREDIT.put(19, "http://www.bankofshanghai.com/WebServlet?go=shbank_creditcard_pg_CreditCardNew&type=m");
		 HANDLE_CREDIT.put(21, "");

	 }
	 
		public static void main(String[] args)  {
		Model m = new Model();
		Model sm = new Model();
		m.put("success", true);
		sm.put("success", true);
		m.put("sm", sm);
		System.out.println(HANDLE_CREDIT.get(1));
	}
}
