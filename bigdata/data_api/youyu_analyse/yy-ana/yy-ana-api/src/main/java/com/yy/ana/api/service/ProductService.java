package com.yy.ana.api.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.ana.domain.Dto;
import com.yy.ana.service.appdata.IAppdataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by User on 2017/6/2.
 */
@Service
public class ProductService {
    @Reference
    private IAppdataService appdataService;

    /**
     * @param distinguishPlatform
     * @param days
     * @param products
     * @return
     * @throws Exception
     */
    public List<Dto> getProductSummaryData(boolean distinguishPlatform, String days, String products) throws Exception {
        return appdataService.getProductSummaryData(distinguishPlatform, days, products);
    }

    /**
     * @param distinguishPlatform
     * @param date
     * @param products
     * @return
     * @throws Exception
     */
    public List<Dto> getProductsTotalData(boolean distinguishPlatform, String date, String products) throws Exception {
        return appdataService.getProductsTotalData(distinguishPlatform, date, products);
    }
}
