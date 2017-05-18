package com.caiyi.financial.nirvana.discount.ccard.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.ccard.bean.Contanct;
import com.caiyi.financial.nirvana.discount.ccard.bean.Tool;
import com.caiyi.financial.nirvana.discount.ccard.dto.ContanctDto;
import com.caiyi.financial.nirvana.discount.ccard.service.ContanctService;
import com.github.pagehelper.Page;
import org.apache.storm.task.TopologyContext;

import java.util.List;
import java.util.Map;

/**
 * Created by heshaohua on 2016/6/6.
 */
@Bolt(boltId = "contanct", parallelismHint = 2, numTasks = 4)
public class ContanctBolt extends BaseBolt {
    private ContanctService contanctService;

    @Override
    protected void _prepare(Map map, TopologyContext topologyContext) {
        contanctService = getBean(ContanctService.class);
    }

    @BoltController
    public List<ContanctDto> weChatList_page(Contanct contanct){
        logger.info("---------------------ContanctBolt weChatList_page");
        return contanctService.query_weChatList_page(contanct);
    }

    @BoltController
    public ContanctDto weChatMsg_operate(Contanct contanct){
        logger.info("---------------------ContanctBolt wechatMsg_operate");
        return contanctService.update_wechatMsg_operate(contanct);
    }
    @BoltController
    public ContanctDto updateView(String contanctId){
        logger.info("---------------------ContanctBolt updateView");
        return  contanctService.updateViews(contanctId);
    }
    @BoltController
    public List<Map<String,String>> queryCategory(Contanct contanct){
        logger.info("---------------------ContanctBolt queryCategory");
        return  contanctService.queryCategory(contanct);
    }
    @BoltController
    public Map<String,List<Map<String, String>>> queryToolArticle(Tool toolBean){
        logger.info("---------------------ContanctBolt queryToolArticle");
        return contanctService.queryToolArticle(toolBean);
    }
    @BoltController
    public Map<String,String> articleClickCount(String articleid){
        logger.info("---------------------ContanctBolt articleClickCount");
        return  contanctService.articleClick(articleid);
    }

    /**
     *
     * @param contanct
     * @return
     */
    @BoltController
    public List<Map<String,String>> queryForTotalsearch(Contanct contanct){
        logger.info("---------------------ContanctBolt queryForTotalsearch");
        return  contanctService.queryForTotalSearch(contanct);
    }
    @BoltController
    public Page<Map<String,Object>> queryContacts(Contanct bean){
        logger.info("---------------ContanctBolt queryContacts-----");
        return  contanctService.queryContacts(bean);
    }
}
