package com.yy.ana.service.appdata;

import com.yy.ana.domain.Dto;

import java.util.List;

/**
 * Created by User on 2017/5/27.
 */
public interface IAppdataService {
    /**
     * @param distinguishPlatform
     * @param days
     * @return
     * @throws Exception
     */
    List<Dto> getProductSummaryData(boolean distinguishPlatform, String days, String products) throws Exception;

    /**
     * @param distinguishPlatform
     * @param day
     * @return
     * @throws Exception
     */
    List<Dto> getProductsTotalData(boolean distinguishPlatform, String day) throws Exception;
}
