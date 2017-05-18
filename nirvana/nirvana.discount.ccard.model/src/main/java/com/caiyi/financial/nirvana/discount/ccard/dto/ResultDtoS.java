package com.caiyi.financial.nirvana.discount.ccard.dto;

/**
 * Created by lizhijie on 2016/10/25. 返回值的统一格式
 */
public class ResultDtoS {
    public  static  int SUCCESS=1;
    public  static  int FAIL=0; //基本的错误
    public  static  int ERROR_FAIL_404=404;
    public  static  int ERROR_FAIL_500=500; //服务异常

    private int code;
    private String desc;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
