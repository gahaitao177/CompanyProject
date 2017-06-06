package com.yy.ana.api.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.ana.domain.Dto;
import com.yy.ana.service.appdata.IAppdataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by User on 2017/5/27.
 */
@Service
public class ApplicationService {
    @Reference
    private IAppdataService appdataService;

    public List<Dto> getProductsStatsDetails(boolean platform) throws Exception {
        return appdataService.getProductsStatsDetails(platform);
    }

    public Dto getProductsStatsTotal() throws Exception {
        return appdataService.getProductsStatsTotal();
    }
}
