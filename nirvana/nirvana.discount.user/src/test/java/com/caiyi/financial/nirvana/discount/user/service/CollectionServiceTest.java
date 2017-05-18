package com.caiyi.financial.nirvana.discount.user.service;

import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.CheapDto;
import com.caiyi.financial.nirvana.discount.user.dto.MarketCheapDto;
import com.caiyi.financial.nirvana.discount.user.mapper.UserMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by wenshiliang on 2016/9/5.
 */
public class CollectionServiceTest extends TestSupport{


    @Autowired
    private UserMapper userMapper;

//    private CheapMapper mapper;


    @Autowired
    private CollectionService collectionService;

    @Test
//    @Rollback(false)
    public void cheapCollectDel() throws Exception {
//        User user = new User();
//        user.setCuserId("84d45b82c303");
//        user.setCollectType("1");
//        user.setStoreId("615");
//        int size = collectionService.cheapCollectDel(user);
//        System.out.println(size);

        User user = new User();
        user.setCuserId("84d45b82c303");
        user.setCollectType("0");
        user.setStoreId("81773,82437");
        int size = collectionService.cheapCollectDel(user);
        System.out.println(size);


    }

    @Test
    public void testQuery_store_cheap(){


        /*
    

         */
        start();
        String[] str = {"3598688","80594","82609", "3598688", "82568","83087","82636","82232", "80031", "80001","82743", "2003840","83317", "82830"};
//        for (String storeId : str) {
//            userMapper.query_store_cheap(storeId);
//
//        }
//        List<CheapDto> list =  mapper.selectCheapByistoreid(Arrays.asList(str));
//        list.forEach((cheap)->{
//            System.out.println(cheap);
//        });
        end();
    }

    @Test
    public void testQuery() throws Exception {
        start();
//        collectionService.query();
        end();
    }

    @Test
    public void testSelectCheapDto() throws Exception {
        List<CheapDto> list  = collectionService.selectCheapDto("5a537aba379");
        list.forEach(dto->{
            logger.info(dto.getClogo());
        });
    }

    @Test
    public void testSelectMarketCheapDto() throws Exception {
        List<MarketCheapDto> list = collectionService.selectMarketCheapDto("5a537aba379");
        DateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        list.forEach(dto->{
            System.out.println(dto.getCadddate());
            System.out.println(dto.getCenddate());
            System.out.println(df.format(dto.getCadddate()));
            System.out.println(df.format(dto.getCenddate()));
        });
    }
}