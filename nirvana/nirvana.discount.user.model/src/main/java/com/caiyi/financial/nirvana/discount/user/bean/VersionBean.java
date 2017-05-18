package com.caiyi.financial.nirvana.discount.user.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by wenshiliang on 2016/9/12
 * 版本管理bean.
 */
public class VersionBean extends BaseBean {


    private String content;//描述
    private int type;//0:可选升级，1:强制升级
    private String url;//升级链接（可整合用户）
    private boolean update;//true有更新 false没有更新

    public VersionBean() {
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
