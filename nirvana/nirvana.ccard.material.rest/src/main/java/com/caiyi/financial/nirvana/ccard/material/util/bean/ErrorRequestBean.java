package com.caiyi.financial.nirvana.ccard.material.util.bean;

/**
 * Created by susan on 2016/4/6.
 * 模拟申请错误请求封装bean
 */
public class ErrorRequestBean {
    private String fileName;//保存文件名
    private Object param;//请求的参数bean或者map
    private String result;//请求结果
    private String url;//地址

    /**
     * [中信] 0，异常；1，短信验证码发送错误；2，用户不满足要求；3，资料提交错误
	 * [渣打] 0，异常；1，短信验证码发送错误；2，用户不满足要求；3，资料提交错误
     * [花旗] 0，异常；1，用户不满足要求；2，资料提交错误
     * [交通] 0 异常  1 失败递交 2预审不通过 3短信获取异常 4需要图片验证码 5系统已达最大申请量
     *[平安] 0，异常；1，申卡第一步资料提交不通过；2，申卡最后一步资料提交不通过
     * [兴业] 0，异常；1，短信验证码发送错误；2，用户不满足要求；3，资料提交错误 -1 没有找到选择的卡  4 没有获得验证码
     *
     */
    private int ierrortype = -1;//错误类型 -1 未确定  其他定义好的错误类型，添加注释在此
    private String cerrordesc;//错误详细
    private String cphone;//手机号

    public int getIerrortype() {
        return ierrortype;
    }

    public void setIerrortype(int ierrortype) {
        this.ierrortype = ierrortype;
    }

    public String getCerrordesc() {
        return cerrordesc;
    }

    public void setCerrordesc(String cerrordesc) {
        this.cerrordesc = cerrordesc;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public ErrorRequestBean() {
    }

    public ErrorRequestBean(String fileName, Object param, String result, String url) {
        this.fileName = fileName;
        this.param = param;
        this.result = result;
        this.url = url;
    }

    public ErrorRequestBean(String fileName, Object param, String result, String url, int ierrortype, String cerrordesc, String cphone) {
        this.fileName = fileName;
        this.param = param;
        this.result = result;
        this.url = url;
        this.ierrortype = ierrortype;
        this.cerrordesc = cerrordesc;
        this.cphone = cphone;
    }

    public ErrorRequestBean(Object param, String result) {
        this.param = param;
        this.result = result;
        this.fileName = System.currentTimeMillis()+".html";
    }

    public ErrorRequestBean(Object param, String result, String url) {
        this.param = param;
        this.result = result;
        this.url = url;
        this.fileName = System.currentTimeMillis()+".html";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
