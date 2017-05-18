package com.caiyi.financial.nirvana.discount.ccard.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.ccard.bean.Contanct;
import com.caiyi.financial.nirvana.discount.ccard.bean.Tool;
import com.caiyi.financial.nirvana.discount.ccard.dto.ContanctDto;
import com.caiyi.financial.nirvana.discount.ccard.mapper.ContanctMapper;
import com.caiyi.financial.nirvana.discount.util.BankConst;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heshaohua on 2016/6/3.
 */

@Service
public class ContanctService extends AbstractService {
    @Autowired
    ContanctMapper contanctMapper;

    /**
     * 微信文章列表 身边有料 收藏列表
     * @param contanct
     * @return
     */
    public List<ContanctDto> query_weChatList_page(Contanct contanct){
        String ftype = contanct.getFunc();
        HashMap<String, String> maps = new HashMap<String, String>();
        String cuserid = contanct.getCuserId();
        String func = "";
        if ("9".equalsIgnoreCase(ftype)) {// 收藏列表
            maps.put("cuserid", cuserid);
        } else {
            String ibanks = contanct.getIbankids();// 银行ids
            String icityid = contanct.getIcityids();// 城市ids
            String order = contanct.getOrder();// 排序规则1时间;2收藏数
            String sqlwhere = "";

            if ("1".equals(order)) {
                order = " cpubdate desc, imsgid ";
            } else {
                order = " IPRAISENUM desc,ICOLLECTNUM desc, imsgid ";
            }

            if (!StringUtils.isEmpty(icityid)) {
                String[] ics = icityid.split(",");
                sqlwhere += " and (icityid=0 ";
                for (String i : ics) {
                    sqlwhere += " or icityid=" + i;
                }
                sqlwhere += " )";
            }
            if (!StringUtils.isEmpty(ibanks)) {
                String[] ibs = ibanks.split(",");
                sqlwhere += " and (1=0";
                for (String i : ibs) {
                    sqlwhere += " or ibankid=" + i;
                }
                sqlwhere += " )";
            }

            maps.put("order", order);
            maps.put("sqlwhere", sqlwhere);
        }

        if (contanct.getPs() <= 0) {
            contanct.setPs(5);
        }

        if (contanct.getPn() <= 0) {
            contanct.setPn(1);
        }

        int startNum = (contanct.getPn()-1)*contanct.getPs();
//        int endNum = contanct.getPn()*contanct.getPs();

        List<ContanctDto> list = null;
        PageHelper.offsetPage(startNum,contanct.getPs());
        if ("9".equalsIgnoreCase(ftype)) {// 收藏列表
            list = contanctMapper.query_coll_wechatList_page(maps.get("cuserid"));
        } else {
            list = contanctMapper.query_wechatList_page(maps.get("sqlwhere"), maps.get("order"));
        }
        if(null != list && list.size() > 0){
            for(ContanctDto dto : list){
                String bankname = BankConst.Bank.get(dto.getIbankid());
                dto.setBankname(bankname);
                int ipraise = 0;
                int icollect = 0;
                if(!StringUtils.isEmpty(cuserid)){
                    //收藏
                    if (!"9".equalsIgnoreCase(ftype)) {
                        int c = contanctMapper.query_userColl(dto.getImsgid(), cuserid);
                        if(c > 0){
                            icollect  =1;
                        }
                    }else{
                        icollect  =1;
                    }
                    //点赞
                    int z = contanctMapper.query_userSpraise(dto.getImsgid(), cuserid);
                    if(z > 0){
                        ipraise  =1;
                    }
                }
                dto.setIpraise(String.valueOf(ipraise));
                dto.setIcollect(String.valueOf(icollect));
            }
        }
        return list;
    }

    /**
     * 文章点赞收藏
     * @param contanct
     * @return
     */
    public ContanctDto update_wechatMsg_operate(Contanct contanct){
        ContanctDto dto = new ContanctDto();

        String cuserid = contanct.getCuserId();
        int imsgid = contanct.getImsgid();// 文章id
        int actionType = contanct.getType();// 1点赞，2收藏
        int active = contanct.getActive() == null ? 0 : contanct.getActive();// 0取消 1添加
        String isql = null, ssql=null, dsql=null,usql=null;
        if (imsgid < 1 || (actionType != 1 && actionType != 2) || (active != 1 && active != 0)) {
            dto.setBusiErrCode("0");
            dto.setBusiErrDesc("参数错误");
            return dto;
        }

        String retstr="";
        int ret = 0;
        if(actionType == 1){// 点赞
            retstr="点赞";
            if(active == 1){
                int c = contanctMapper.query_userSpraise(imsgid+"",cuserid);

                if(c > 0){// 重复点赞
                    ret = 1;
                    retstr="重复"+retstr;
                }else{
                    ret = contanctMapper.insert_userSpraise(cuserid,imsgid+"");
                    if(ret==1){
                        contanctMapper.update_userAPraise(imsgid+"");
                    }
                }
            }else{
                ret = contanctMapper.del_userSpraise(imsgid+"", cuserid);
                if(ret == 1){
                    contanctMapper.update_userDPraise(imsgid+"");
                    retstr="取消"+retstr;
                }
            }
        }else{//收藏
            retstr="收藏";
            if(active == 1){
                int c = contanctMapper.query_userColl(imsgid + "", cuserid);
                if(c > 0){// 重复点赞
                    ret = 1;
                    retstr="重复"+retstr;
                }else{
                    ret = contanctMapper.insert_userIcoll(cuserid,imsgid+"");
                    if(ret==1){
                        contanctMapper.update_userAcoll(imsgid+"");
                    }
                }
            }else{
                ret = contanctMapper.del_userColl(imsgid + "", cuserid);
                if(ret == 1){
                    contanctMapper.update_userDcoll(imsgid + "");
                    retstr="取消"+retstr;
                }
            }
        }

        dto.setBusiErrCode(ret+"");
        dto.setBusiErrDesc(retstr);
        return dto;
    }
    /**
     * 更新访问量
     * @param contactId
     * @return
     */
    public ContanctDto updateViews(String  contactId){
        ContanctDto contanctDto=new ContanctDto();
        int re=contanctMapper.update_viewCount(contactId);
        if(re>0){
            contanctDto.setBusiErrCode("1");
            contanctDto.setBusiErrDesc("访问量更新成功");
            logger.info("访问量更新成功:"+contactId);
        }else {
            contanctDto.setBusiErrCode("0");
            contanctDto.setBusiErrDesc("访问量更新失败");
            logger.info("访问量更新失败:"+contactId);
        }
        return contanctDto;
    }
    /**
     * 分页查询接口,根据bean的 type值查询数据,查询的是已经发布的数据(表TB_CONTACT的IPUBLISHE字段的值是1)
     * @param contanct
     */
    public  List<Map<String,String>>  queryCategory(Contanct contanct){
        int start=(contanct.getPn()-1)*contanct.getPs();
//        int end =contanct.getPn()* contanct.getPs();
        List<Map<String, String>> contanctDtoList=null;
        try {
            PageHelper.offsetPage(start,contanct.getPs());
            contanctDtoList = contanctMapper.queryCatetory(contanct.getCategory());
            logger.info("已发布文章查询成功");
        }catch (Exception e){
            contanctDtoList=null;
            logger.info("已发布文章查询异常");
        }
        return contanctDtoList;
    }

    public Map<String,List<Map<String, String>>> queryToolArticle(Tool toolBean){
        String toolid=toolBean.getToolid();
        String bversion=toolBean.getBversion();
        String sorce=toolBean.getCsource();
        String typeid=toolBean.getTypeid();
        Map<String, List<Map<String, String>>> resultMap=new HashMap<String, List<Map<String,String>>>();
        if(StringUtils.isEmpty(toolid)) {
            logger.info("请求文章的参数错误:"+toolid);
            return null;
        }
        List<Map<String,String>> categoryList=null;
        List<Map<String,String>> versionList=null;
        List<Map<String,String>> articleList=null;
        try {
            categoryList = contanctMapper.queryToolCategoryById(toolid);
            resultMap.put("Type",categoryList);
            logger.info("请求文章的类型成功:"+toolid);
        }catch (Exception e){
            logger.info("请求文章类型异常:"+toolid);
            categoryList=null;
        }

        if(!StringUtils.isEmpty(bversion)){
            try {
                Map<String,String> para=new HashMap<>();
                para.put("bversion",bversion);
                para.put("toolid",toolid);
                para.put("source",sorce);
                versionList = contanctMapper.queryToolSpread(para);
                logger.info("请求文章的版本成功:"+toolid);
            }catch (Exception e){
                logger.info("请求文章版本异常:"+bversion);
                articleList=null;
            }
            resultMap.put("B",versionList);
        }
        try {
            articleList = contanctMapper.queryToolArticle(toolid,typeid);
            logger.info("请求文章成功:"+toolid);
        }catch (Exception e){
            logger.info("请求文章异常:"+toolid);
            articleList=null;
        }
        resultMap.put("Article",articleList);
        return resultMap;
    }
    /**
     * 更新文章的点击量
     * @param articleid
     * @return
     */
    public Map<String,String> articleClick(String  articleid){
        Map<String,String> clickResult=new HashMap<String,String>();
        clickResult.put("code","0");
        clickResult.put("desc","更新文章查看量出错");
        logger.info("更新文章观看出错:"+articleid);
        int re=0;
        if(articleid!=null) {
            re = contanctMapper.articleClickCount(articleid);
        }
        if(re>0){
            clickResult.put("code","1");
            clickResult.put("desc","更新文章查看量成功");
            logger.info("访问量更新成功:"+articleid);
        }
        return clickResult;
    }


    /**
     * 分页查询接口,根据bean的 type值查询数据,查询的是已经发布的数据(表TB_CONTACT的IPUBLISHE字段的值是1)
     * @param contanct
     */
    public List<Map<String,String>>  queryForTotalSearch(Contanct contanct){
        int start=(contanct.getPn()-1)*contanct.getPs();
//        int end =contanct.getPn()* contanct.getPs();
        List<Map<String, String>> contanctDtoList=null;
        try {
            PageHelper.offsetPage(start,contanct.getPs());
            String sqlWhere = "(ctitle like '%" + contanct.getTitle()  + "%' or csummary like '%" + contanct.getTitle() + "%') ";
            logger.info("queryForTotalSearch sqlWhere:" + sqlWhere);
            contanctDtoList = contanctMapper.queryForTotalsearch(sqlWhere);
        }catch (Exception e){
            contanctDtoList=null;
            logger.info("已发布文章查询异常");
        }
        return contanctDtoList;
    }
    public Page<Map<String,Object>> queryContacts(Contanct bean){
        if (bean.getPs() <= 0) {
            bean.setPs(25);
        }
        String level = bean.getPosition();
        if (StringUtils.isEmpty(level) || !("0".equals(level) || "1".equals(level) || "2".equals(level))) {
            bean.setPosition("99");
        }
        logger.debug(" level is " + bean.getPosition());
        Map<String,String> para=new HashMap<>();
        para.put("position",bean.getPosition());
        para.put("type",String.valueOf(bean.getType()));
        PageHelper.offsetPage(bean.getPs()*(bean.getPn()-1),bean.getPs());
        Page<Map<String,Object>> result=contanctMapper.queryContacts(para);
        return result;
    }
}
