package com.yy.ana.service.appdata;

import com.yy.ana.domain.Dto;

import java.util.List;

/**
 * Created by User on 2017/5/27.
 */
public interface IAppdataService {
    List<Dto> getProductsStatsDetails(boolean platfrom) throws Exception;

    Dto getProductsStatsTotal() throws Exception;
}
