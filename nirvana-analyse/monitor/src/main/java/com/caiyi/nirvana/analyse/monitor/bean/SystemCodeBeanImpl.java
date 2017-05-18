package com.caiyi.nirvana.analyse.monitor.bean;

import com.caiyi.nirvana.analyse.enums.SystemEnum;
import com.caiyi.nirvana.analyse.monitor.MonitorService;
import com.rbc.frame.ServiceContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by pc on 2017/3/13.
 */
public class SystemCodeBeanImpl {

    public int change(SystemCodeBean bean, ServiceContext context, HttpServletRequest request,
                      HttpServletResponse response) {

        Map<String, Boolean> enableMap = MonitorService.enableMap;
        for (String key : enableMap.keySet()) {
            Boolean bool = enableMap.get(key);
            if (SystemEnum.ACCOUNT.getCode().equals(key)) {
                if (bean.getAccount() != null && !bool.equals(bean.getAccount()))
                    enableMap.put(SystemEnum.ACCOUNT.getCode(), !bool);
                bean.setAccount(enableMap.get(key));
            } else if (SystemEnum.CREDIT_CARD.getCode().equals(key)) {
                if (bean.getCreditCard() != null && !bool.equals(bean.getCreditCard()))
                    enableMap.put(SystemEnum.CREDIT_CARD.getCode(), !bool);
                bean.setCreditCard(enableMap.get(key));
            } else if (SystemEnum.PROVIDENT_FUND.getCode().equals(key)) {
                if (bean.getProvidenFund() != null && !bool.equals(bean.getProvidenFund()))
                    enableMap.put(SystemEnum.PROVIDENT_FUND.getCode(), !bool);
                bean.setProvidenFund(enableMap.get(key));
            } else if (SystemEnum.SOCIAL_SECURITY.getCode().equals(key)) {
                if (bean.getSocialSecurity() != null && !bool.equals(bean.getSocialSecurity()))
                    enableMap.put(SystemEnum.SOCIAL_SECURITY.getCode(), !bool);
                bean.setSocialSecurity(enableMap.get(key));
            } else if (SystemEnum.LOAN.getCode().equals(key)) {
                if (bean.getLoan() != null && !bool.equals(bean.getLoan()))
                    enableMap.put(SystemEnum.LOAN.getCode(), !bool);
                bean.setLoan(enableMap.get(key));
            }
        }
        return 1;
    }
}
