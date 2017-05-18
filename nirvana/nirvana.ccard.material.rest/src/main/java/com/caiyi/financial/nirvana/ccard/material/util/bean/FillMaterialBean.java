package com.caiyi.financial.nirvana.ccard.material.util.bean;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.DataUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lizhijie on 2016/7/26.
 */
public class FillMaterialBean {
    private  static Logger log= LoggerFactory.getLogger(FillMaterialBean.class);
    public  static Integer getMaterialBean(MaterialBean materialBean,HttpServletRequest request){
        if(materialBean==null) {
            log.info("参数为空");
            return 0;
        }
        String data=request.getParameter("data");
        if (StringUtils.isEmpty(data)){
            materialBean.setBusiErrCode(0);
            materialBean.setBusiErrDesc("参数不能为空");
            log.info("参数为空");
            return 0;
        }
        materialBean.setData(data);
        try {
             Integer z=DataUtil.dencrypt_data(materialBean,request);
            if (z==0){
                materialBean.setBusiErrCode(0);
                materialBean.setBusiErrDesc("解密失败");
                log.info("解密失败");
                return 0;
            }
        }catch (Exception e){
            materialBean.setBusiErrCode(0);
            materialBean.setBusiErrDesc("解密异常");
            log.info("解密异常,{}",e.toString());
            return  0;
        }
        MaterialModel materialModel=new MaterialModel();
        materialModel.initMaterialModel(materialBean.getData());
        materialBean.setModel(materialModel);
        log.info("解密成功");
        return  1;
    }
    public  static MaterialModel getMaterialModel(HttpServletRequest request){
        MaterialModel materialModel=new MaterialModel();
        String data=request.getParameter("data");
        JSONObject object=JSONObject.parseObject(data);
        String cstartinfo=object.getString("startInfo");
        String idegree=object.getString("degree");
        String maritalstatus=object.getString("maritalStatus");
        String inatureofunit=object.getString("natureOfUnit");
        String idepartment=object.getString("post");
        String itimeinjob=object.getString("timeInJob");
        String residencestatus=object.getString("residenceStatus");

        materialModel.setIcityid(object.getString("icityid"));
        materialModel.setCtheme(object.getString("theme"));
        materialModel.setIbankid(object.getString("ibankid"));
        materialModel.setIdegree(idegree);
        materialModel.setCstartinfo(cstartinfo);
        materialModel.setMaritalstatus(maritalstatus);
        materialModel.setInatureofunit(inatureofunit);
        materialModel.setIdepartment(idepartment);
        materialModel.setItimeinjob(itimeinjob);
        materialModel.setResidencestatus(residencestatus);

        return materialModel;
    }
}
