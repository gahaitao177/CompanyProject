package com.caiyi.financial.nirvana.discount.ccard.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.ccard.bean.Demo;
import com.caiyi.financial.nirvana.discount.ccard.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/4/21.
 */
@Service
public class DemoService extends AbstractService {

    @Autowired
    DemoMapper demoMapper;

    public List<Map<String,Object>> select(){
        List<Map<String,Object>> list = demoMapper.select();
        System.out.println(list);
        return list;
    }

    @Transactional(rollbackFor=Exception.class)
    public int addTest(String t1,String t2) throws Exception {

        int count = demoMapper.addTest(t1,t2);
        if("run".equals(t1)){
            throw new RuntimeException("测试抛出runexception回滚");
        }
        if("error".equals(t1)){
            throw new Exception("测试抛出exception回滚");
        }
        return count;
    }
    public List<Demo> select2(){
        return  demoMapper.select2();
    }
    public List<Demo> select3(){
        return  demoMapper.select3(new Demo());
    }
    public List<Map<String,Object>> select4(){
        return  demoMapper.select4(new Demo());
    }
}
