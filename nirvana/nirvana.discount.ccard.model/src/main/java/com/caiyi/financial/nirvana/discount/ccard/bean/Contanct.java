package com.caiyi.financial.nirvana.discount.ccard.bean;


import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.ArrayList;
import java.util.List;

public class Contanct extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1740751935264949754L;

	private Integer contactId;//主键
	
	private String title="";//标题
	
	private String content="";//内容
	
	private String url="";//百科对应的url
	
	private String author="";//作者
	
	private Integer type=0;//0:百科 1:卡神攻略 2:每日优惠时光

	private String createdTime="";

	private String modifiedTime;
	
	private String picUrl;//图片地址

	private Integer active;//标记是否为删除记录
	
	private Integer published;//是否已经发布
	
	private String publishedTime = "";
	
	private String accessUrl = "";//h5静态页面访问地址		
	
	
	private String origin="";//来源
	
	private Integer views = 0;//浏览量
	
	
	private String iconUrl = "";//小图片url
	
	// add by lcs 20150708
	private String position = "0";
	
	// 
	private String category = "";

    private String icityids;
    private String ibankids;
    private String order;
    private int imsgid;
    
	public int getImsgid() {
		return imsgid;
	}


	public void setImsgid(int imsgid) {
		this.imsgid = imsgid;
	}


	public String getIcityids() {
		return icityids;
	}


	public void setIcityids(String icityids) {
		this.icityids = icityids;
	}


	public String getIbankids() {
		return ibankids;
	}


	public void setIbankids(String ibankids) {
		this.ibankids = ibankids;
	}


	public String getOrder() {
		return order;
	}


	public void setOrder(String order) {
		this.order = order;
	}


	public String getPosition() {
		return position;
	}


	public void setPosition(String position) {
		this.position = position;
	}


	//暂时未使用
	private String h5Path = "E:/workspace/been/CreditAdminWeb/WebContent/h5";
	//暂时未使用
	private String h5TemplatesPath = "E:/workspace/been/CreditAdminWeb/WebContent/templates";
	
	//远程调用 queryList方法的"返回值"
	
	private List<Contanct> list = new ArrayList<Contanct>();

	public Integer getContactId() {
		return contactId;
	}


	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public Integer getType() {
		return type;
	}


	public void setType(Integer type) {
		this.type = type;
	}


	public String getCreatedTime() {
		return createdTime;
	}


	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}


	public String getModifiedTime() {
		return modifiedTime;
	}


	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}


	public String getPicUrl() {
		return picUrl;
	}


	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}


	public Integer getActive() {
		return active;
	}


	public void setActive(Integer active) {
		this.active = active;
	}
	

	public Integer getPublished() {
		return published;
	}


	public void setPublished(Integer published) {
		this.published = published;
	}


	public String getH5Path() {
		return h5Path;
	}


	public void setH5Path(String h5Path) {
		this.h5Path = h5Path;
	}


	public String getH5TemplatesPath() {
		return h5TemplatesPath;
	}


	public void setH5TemplatesPath(String h5TemplatesPath) {
		this.h5TemplatesPath = h5TemplatesPath;
	}


	public List<Contanct> getList() {
		return list;
	}


	public void setList(List<Contanct> list) {
		this.list = list;
	}


	public String getOrigin() {
		return origin;
	}


	public void setOrigin(String origin) {
		this.origin = origin;
	}


	public String getPublishedTime() {
		return publishedTime;
	}


	public void setPublishedTime(String publishedTime) {
		this.publishedTime = publishedTime;
	}


	public String getAccessUrl() {
		return accessUrl;
	}


	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}


	public Integer getViews() {
		return views;
	}


	public void setViews(Integer views) {
		this.views = views;
	}


	public String getIconUrl() {
		return iconUrl;
	}


	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}
	
	
	 
}
