package com.yy.ana.api.action;

import com.yy.ana.api.service.ApplicationService;
import com.yy.ana.framework.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by User on 2017/5/27.
 */
@Controller()
public class ApplicationAction extends BaseAction {
    @Autowired
    private ApplicationService applicationService;

    @RequestMapping("/get_products_stats_details")
    @ResponseBody
    public Map<String, Object> getProductsStatsDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean platform = WebUtils.getPraramsAsDto(request).containsKey("platform");

        Map<String, Object> result = getReturnObject(response);
        result.put("data", applicationService.getProductsStatsDetails(platform));

        return result;

    }

    @RequestMapping("/get_products_stats_total")
    @ResponseBody
    public Map<String, Object> get_products_stats_total(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> result = getReturnObject(response);
        result.put("data", applicationService.getProductsStatsTotal());

        return result;

    }
}
