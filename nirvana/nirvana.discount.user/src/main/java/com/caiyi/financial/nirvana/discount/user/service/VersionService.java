package com.caiyi.financial.nirvana.discount.user.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.user.bean.VersionBean;
import com.caiyi.financial.nirvana.discount.user.mapper.VersionMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wenshiliang on 2016/9/12.
 */
@Service
public class VersionService extends AbstractService {

    @Autowired
    private VersionMapper versionMapper;

    public VersionBean queryVersion(VersionBean bean){
        PageHelper.startPage(1,1);
        VersionBean versionBean = versionMapper.queryVersion(bean);
        if(versionBean!=null){
            versionBean.setUpdate(true);
        }else{
            versionBean = new VersionBean();
            versionBean.setUpdate(false);
        }
        return versionBean;
    }
}
