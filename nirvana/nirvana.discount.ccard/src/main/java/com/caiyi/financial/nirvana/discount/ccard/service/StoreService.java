package com.caiyi.financial.nirvana.discount.ccard.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Store;
import com.caiyi.financial.nirvana.discount.ccard.dto.CheapDetailDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.CheapDto;
import com.caiyi.financial.nirvana.discount.ccard.mapper.CheapMapper;
import com.caiyi.financial.nirvana.discount.ccard.mapper.StoreMapper;
import com.caiyi.financial.nirvana.discount.exception.CheapException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/5/5.
 */
@Service
public class StoreService extends AbstractService {

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private CheapMapper cheapMapper;

    /**
     * 查询门店列表
     * @param store
     * @return
     */
    public List<Store> storeList(Store store){
        return storeMapper.storeList(store);
    }

    @Transactional(rollbackFor = Exception.class)
    public CheapDetailDto cheap(Cheap bean){
        String istoreid = bean.getIstoreid();
        String ibussinessid = bean.getIbusinessid();
        Double clat = bean.getClat();
        Double clng = bean.getClng();
        Long icityid = bean.getIcityid();
        if(icityid==null){
            throw new CheapException("该城市不存在");
        }
        //存在商户id，依据商户id查询最近门店
        if(StringUtils.isNotEmpty(ibussinessid)){
            if(clat==null || clng==null){
                //查询城市经纬度
                Map<String,String> map = storeMapper.queryClngAndClat(icityid);
                if(map==null || map.size()==0){
                    throw new CheapException("该城市不存在");
                }
                clat = Double.valueOf(map.get("clat"));
                clng = Double.valueOf(map.get("clng"));
            }
            istoreid = storeMapper.queryNearestStoreId(ibussinessid,icityid,clat,clng);
        }
        CheapDetailDto cheapDto = storeMapper.selectCheapDetail(istoreid);
        if(cheapDto==null){
            throw new CheapException("未找到相应门店");
        }

        ibussinessid = cheapDto.getIbussinessid();

        //保留一个电话号
        String ctel = cheapDto.getCtel();
        if(StringUtils.isNotEmpty(ctel)){
            if (ctel.contains(",")) {
                ctel = ctel.split(",")[0];
            }
            cheapDto.setCtel(ctel);
        }
        //取cpicurl2 赋值 cpicurl1
        cheapDto.setCpicurl1(cheapDto.getCpicurl2());
        cheapDto.setCpicurl2(null);


        //是否收藏，点赞
        cheapDto.setIsave(storeMapper.collCount(bean.getCuserId(),istoreid)>0 ? "1":"0");
        cheapDto.setIpraise(storeMapper.praiseCount(bean.getCuserId(),istoreid)>0 ? "1":"0");

        //门店数
        cheapDto.setCount(""+storeMapper.storeCount(ibussinessid,icityid));

        //商户浏览数+1
        storeMapper.addBusinessIpvtimes(ibussinessid);
        StringBuilder ctitle = new StringBuilder();
        List list = new ArrayList<>();

//        List<Map<String,Object>> cheapList = cheapMapper.queryCheap(istoreid);

        List<CheapDto> cheapDtoList = cheapMapper.queryCheap(istoreid);

        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        for(CheapDto cheap : cheapDtoList) {
            ctitle.append(cheap.getIshortname()).append(":").append(cheap.getCtitle()).append("@");

            String ccontent = cheap.getCcontent();
            if (StringUtils.isNotEmpty(ccontent) && !ccontent.contains("免责说明")) {
                ccontent += "<br/>【免责说明】优惠信息来源于互联网，可能存在差异，仅供参考，使用前请咨询门店，具体以银行官网以及各商家实际优惠为准。";
            }
            cheap.setCcontent(ccontent);


            StringBuilder activitytime = new StringBuilder();
            Date starttime = cheap.getCstartdate();
            Date endtime = cheap.getCenddate();

            if(starttime==null && endtime==null){
                activitytime.append("长期有效");
            }else {
                if(starttime==null){
                    activitytime.append("截止时间 ").append(sdf.format(endtime));
                }else if(endtime==null){
                    activitytime.append("起始时间 ").append(sdf.format(starttime));
                }else {
                    activitytime.append(sdf.format(starttime)).append(" — ").append(sdf.format(endtime));
                }
            }


            activitytime.append("   ").append( parseWeek(cheap.getIweek()));
            cheap.setActivitytime(activitytime.toString());
        }
        cheapDto.setRow(cheapDtoList);


        Store store = new Store();
        store.setIcityid(bean.getIcityid());
        store.setIbusinessid(Long.valueOf(ibussinessid));
        store.setPageNum(1);
        store.setPageSize(2);
        List<Store> storeList = storeMapper.storeList(store);

        cheapDto.setStore(storeList);


        if(StringUtils.isEmpty(ctitle)){
            ctitle.append("未找到优惠信息。");
        }
        cheapDto.setCtitle(ctitle.toString());

        return  cheapDto;
    }


    private String parseWeek(int wk) {
        String[] CW = { "一", "二", "三", "四", "五", "六", "日" };
        if (wk > 0 && wk < 127) {
            String w = "每周";
            for (int i = 0, j = CW.length; i < j; i++) {
                if ((wk & new Double(Math.pow(2, i)).intValue()) > 0) {
                    w += CW[i];
                }
            }
            w += "有效";
            return w;
        }
        return "";
    }
}
