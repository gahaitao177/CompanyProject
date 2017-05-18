package com.caiyi.financial.nirvana.discount.tools.dto;

/**
 * Created by lichuanshun on 2017/3/15.
 */
public class IosPackageInfoDto {
    private String packageName;
    private String iconUrl;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    private String downloadUrl;
}
