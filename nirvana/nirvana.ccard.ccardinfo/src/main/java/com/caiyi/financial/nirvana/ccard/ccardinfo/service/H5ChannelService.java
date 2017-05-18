package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.*;
import com.caiyi.financial.nirvana.ccard.ccardinfo.mapper.*;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.constant.BankIcoConstant;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/6/20.
 */
@Service
public class H5ChannelService extends AbstractService {




    @Autowired
    private H5ChannelBeanMapper h5channelBeanMapper;

    @Autowired
    private H5ChannelBannerBeanMapper h5ChannelBannerBeanMapper;

    @Autowired
    private H5ChannelBankBeanMapper h5ChannelBankBeanMapper;

    @Autowired
    private H5ChannelCardUseBeanMapper h5ChannelCardUseBeanMapper;

    @Autowired
    private H5ChannelCardBeanMapper h5ChannelCardBeanMapper;



    @Transactional(rollbackFor=Exception.class)
    public BoltResult selectIndex(Long ichannelid){
        if(ichannelid==null || ichannelid<1){
            return new BoltResult(BoltResult.ERROR,"渠道不存在");
        }

        H5ChannelBean channel =  h5channelBeanMapper.selectOne(ichannelid);

        if(channel == null){
            return new BoltResult(BoltResult.ERROR,"渠道不存在");
        }
        Map<String,Object> data = new HashMap<>();

        //channelUrl
        if(StringUtils.isNoneEmpty(channel.getCchannelurl())){
            data.put("channelUrl",channel.getCchannelurl());
        }

        //banner
        List<H5ChannelBannerBean> bannerList = h5ChannelBannerBeanMapper.selectList(ichannelid);
        List<Map<String,Object>> banner = new ArrayList<>();
        bannerList.forEach(bean->{
            Map<String,Object> map = new HashMap<>();
            map.put("picUrl",bean.getCpicurl());
            map.put("targetUrl",bean.getCurl());
            banner.add(map);
        });
        data.put("baner",banner);

        //合作银行 bank
        List<H5ChannelBankBean> bankBeanList = h5ChannelBankBeanMapper.selectList(ichannelid);
        List<Map<String,Object>> bank = new ArrayList<>();
        bankBeanList.forEach(bean->{
            long ibankid = bean.getIbankid();
            String picUrl = BankIcoConstant.BANK_H5_IC0.get(""+ibankid);

            Map<String,Object> map = new HashMap<>();

            map.put("ibankid",ibankid);
            map.put("bankName",bean.getCbankname());
            map.put("picUrl",picUrl);
            map.put("targetUrl",bean.getCurl());
            map.put("order",bean.getIorder());

            bank.add(map);
        });
        data.put("bank",bank);

        //热门卡主题 bankUse

        List<H5ChannelCardUseBean> useList = h5ChannelCardUseBeanMapper.selectList(ichannelid);
        List<Map<String,Object>> bankUse = new ArrayList<>();
        useList.forEach(bean->{
            Map<String,Object> map = new HashMap<>();
            map.put("iuseid",bean.getIuseid());
            map.put("title",bean.getCusename());
            map.put("picUrl",bean.getPicurl());
            map.put("order",bean.getIorder());
            map.put("csubtitle",bean.getCsubtitle());

            bankUse.add(map);
        });
        data.put("bankUse",bankUse);

        //热门卡片 hotCard


        List<H5ChannelCardBean> hotCartList = h5ChannelCardBeanMapper.selectListHit(ichannelid);

        List<Map<String,Object>> hotCard = new ArrayList<>();
        hotCartList.forEach(bean->{
            Map<String,Object> map = new HashMap<>();
            map.put("picUrl",bean.getPicurl());
            String privilege = bean.getCprivilege();
            if(StringUtils.isNotEmpty(privilege)){
                String[] ss = privilege.split("\\|");
                List<Map<String,String>> privilegeList = new ArrayList();
                for (String s : ss){
                    Map<String,String> p = new HashMap<>();
                    p.put("title",s);
                    privilegeList.add(p);
                }
                map.put("privilege",privilegeList);
            }

            map.put("title",bean.getCname());
            map.put("applynum",bean.getIclicknum());
            map.put("targetUrl",bean.getCurl());
            map.put("icardid",bean.getIcardid());
            map.put("order",bean.getIorder());

            hotCard.add(map);
        });

        data.put("hotCard",hotCard);
        h5channelBeanMapper.iclickChannel(ichannelid);
        return new BoltResult(BoltResult.SUCCESS,"",data);
    }


    public Page selectCard(H5ChannelCardBean cardBean){
        Page page = new Page();
        Page<H5ChannelCardBean> list = h5ChannelCardBeanMapper.selectListByUse(cardBean);
        page.setPageNum(list.getPageNum());
        page.setPageSize(list.getPageSize());
        page.setTotal(list.getTotal());
        page.setPages(list.getPages());
        list.forEach(bean->{
            Map<String,Object> map = new HashMap<>();
            map.put("picUrl",bean.getPicurl());
            String privilege = bean.getCprivilege();
            if(StringUtils.isNotEmpty(privilege)){
                String[] ss = privilege.split("\\|");
                List<Map<String,String>> privilegeList = new ArrayList();
                for (String s : ss){
                    Map<String,String> p = new HashMap<>();
                    p.put("title",s);
                    privilegeList.add(p);
                }
                map.put("privilege",privilegeList);
            }
            map.put("title",bean.getCname());
            map.put("applynum",bean.getIclicknum());
            map.put("targetUrl",bean.getCurl());
            map.put("icardid",bean.getIcardid());
            map.put("order",bean.getIorder());
            page.add(map);
        });

        return page;
    }

    public List<Map<String,Object>> selectProgressUrl(Long ichannelid){
        List<Map<String,Object>> list = h5ChannelBankBeanMapper.selectProgressUrl();
        list.forEach(map->{
            String ibankid = map.get("ibankid").toString();
            String picUrl = BankIcoConstant.BANK_ICO.get(ibankid);
            map.put("picUrl",picUrl);
        });
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public int clickCard(H5ChannelCardBean bean){
        h5ChannelCardBeanMapper.clickBank(bean);
        return h5ChannelCardBeanMapper.clickCard(bean);
    }

    @Transactional(rollbackFor = Exception.class)
    public int clickBank(H5ChannelBankBean bean){
        return h5ChannelBankBeanMapper.clickBank(bean);
    }
}
