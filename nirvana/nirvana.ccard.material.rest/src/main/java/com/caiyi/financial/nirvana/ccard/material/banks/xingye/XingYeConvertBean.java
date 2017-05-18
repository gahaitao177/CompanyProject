package com.caiyi.financial.nirvana.ccard.material.banks.xingye;

import java.util.HashMap;
import java.util.Map;

public class XingYeConvertBean {

	public  static Map<String,String> degrees=new HashMap<String,String>(); //学历
	public  static Map<String,String> relation=new HashMap<String,String>();//与办卡人关系
	public  static Map<String,String> industries=new HashMap<String,String>();//所处的行业 
	public  static Map<String,String> jobs=new HashMap<String,String>();//职务  
	public  static Map<String,String> workTime=new HashMap<String,String>();//工作年限
	public  static Map<String,String> marriages=new HashMap<String,String>();//婚姻状态
	//性别 1、男 2、女
	
	static {
		// 5、 博士及以上 4、硕士 3、本科 2、大专 1、高中及以下
		degrees.put("1", "4");
		degrees.put("2", "5");
		degrees.put("3", "3");
		degrees.put("4", "1");
		degrees.put("5", "2");
		//S 未婚  M 已婚 O 其他
		marriages.put("1", "S");
		marriages.put("2", "M");
		marriages.put("3", "O");
		//任职年数：1、一年以下 2、一年 3、两年 4、三年 5、四年 6、五年及以上
		//01、6个月之内  02、6-12个月 03、12-24个月  04、24-36个月 05、36个月以上
		workTime.put("1", "01");
		workTime.put("2", "02");
		workTime.put("3", "03");
		workTime.put("4", "04");
		workTime.put("5", "05");
		//行业性质：1、金融业 2、IT通讯 3、服务业 4、制造业 5、建筑地产 6、商贸零售 7、运输物流 8、法律咨询 9、教育科研 10、医疗卫生 11、机关团体 12、体育娱乐 13、旅游酒店餐饮 14、其它
		//01、政府机关、社会团体  02、教育、科研 03、公用事业单位 04、基层群众自治团体 05、电信/计算机/信息传输 06、商业贸易、批发零售
		//07、金融业 08、建筑业 09、服务业 10、旅游、酒店、餐饮 11、医疗卫生 12、大众传媒 13、农林牧渔矿 14、加工制造业 15、交通、运输业 16、宗教组织、专门行业团体
		//17、专业运动 18、个人服务、娱乐休闲 19、其他
		industries.put("1", "07");
		industries.put("2", "05");
		industries.put("3", "09");
		industries.put("4", "14");
		industries.put("5", "08");
		industries.put("6", "06");
		industries.put("7", "07");
		industries.put("8", "19");
		industries.put("9", "02");
		industries.put("10", "11");
		industries.put("11", "01");
		industries.put("12", "12");
		industries.put("13", "10");
		industries.put("14", "19");
		
		// 职位：1、一般员工 2、部门经理/处级 3、总经理/局级以上 4、主管/科级
		// 04、 职员 03、基层主管 02、中层主管 01、高层主管 
		jobs.put("1", "04");
		jobs.put("2", "03");
		jobs.put("3", "02");
		jobs.put("4", "01");
		
		//亲属关系1.配偶 2.父母 3.子女 4.兄弟姐妹
		//1、配偶 3、父母 4、兄弟姐妹
		relation.put("1", "配偶");
		relation.put("2", "父母");
		relation.put("3", "父母");
		relation.put("4", "兄弟姐妹");		
	}
}
