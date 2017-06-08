package com.yy.ana.api.action;

import com.yy.ana.api.service.ProductService;
import com.yy.ana.domain.Dto;
import com.yy.ana.framework.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by sunqm on 2017/6/2.
 */
@Controller
public class ProductAction extends BaseAction {

    @Autowired
    private ProductService productService;

    /**
     * 查询产品数据列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/get_product_summary_data")
    @ResponseBody
    public Map<String, Object> getProductsSummaryData(HttpServletRequest request, HttpServletResponse response) {
        Dto params = WebUtils.getPraramsAsDto(request);
        Map<String, Object> result = getReturnObject(response);
        if (!WebUtils.checkProperties(params, "date_list")) {
            result.put("ret", 1);
            result.put("msg", "缺少必要参数: date_list");
            return result;
        }
        //是否区分平台
        boolean distinguishPlatform = false;
        try {
            distinguishPlatform = params.containsKey("distinguish_platform") ? params.getAsBoolean("distinguish_platform") : false;
        } catch (Exception e) {
        }
        //products:产品列表
        String products = params.containsKey("products") ? params.getAsString("products") : null;

        try {
            List<Dto> list = productService.getProductSummaryData(distinguishPlatform, params.getAsString("date_list"), products);
            result.put("data", list);
        } catch (Exception e) {
            result.put("ret", 1);
        }

        return result;
    }

    @RequestMapping("/get_product_total_data")
    @ResponseBody
    public Map<String, Object> getProductsTotalData(HttpServletRequest request, HttpServletResponse response) {
        Dto params = WebUtils.getPraramsAsDto(request);
        Map<String, Object> result = getReturnObject(response);
        if (!WebUtils.checkProperties(params, "date")) {
            result.put("ret", 1);
            result.put("msg", "缺少必要参数: date");
            return result;
        }
        //是否区分平台
        boolean distinguishPlatform = false;
        try {
            distinguishPlatform = params.containsKey("distinguish_platform") ? params.getAsBoolean("distinguish_platform") : false;
        } catch (Exception e) {
        }

        try {
            List<Dto> list = productService.getProductsTotalData(distinguishPlatform, params.getAsString("date"), params.containsKey("products") ? params.getAsString("products") : "");
            result.put("data", list);
        } catch (Exception e) {
            result.put("ret", 1);
        }

        return result;
    }
}
