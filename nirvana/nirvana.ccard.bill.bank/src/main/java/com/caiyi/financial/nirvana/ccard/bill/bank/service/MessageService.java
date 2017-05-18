package com.caiyi.financial.nirvana.ccard.bill.bank.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.mapper.MessageMapper;
import com.caiyi.financial.nirvana.ccard.bill.bean.Message;
import com.caiyi.financial.nirvana.ccard.bill.bean.WxRepayCB;
import com.caiyi.financial.nirvana.ccard.bill.dto.MessageDto;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lizhijie on 2016/9/18.
 */
@Service
public class MessageService extends AbstractService {
    @Autowired
    MessageMapper messageMapper;
    public  Integer saveMessage(Message message){
        return  messageMapper.saveMessage(message);
    }
    public List<MessageDto> getMessageList(String cuserid){
        //-1表示最近一个月的消息列表
       return messageMapper.queryMessageList(cuserid,-1);
    }

    /**
     * 状态 0:待支付,1:支付成功,2:还款成功,3:还款失败
     * @param bean
     * @return
     */
    public int fillMessage(WxRepayCB bean,int state){
        Message message = new Message();
        java.sql.Timestamp jstt = null;
//        message.setUserId(bean.getCuserId());
        int flag=0;
        if(state==2|| state==3) {
            message.setTitle("还款到账通知");
            message.setType(3);
            try {
                jstt = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getRepay_time()).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (state == 2) {
                message.setSubtitle("到账成功！账单已还清噜~");
                message.setTarget("1");
            } else{
                message.setSubtitle("到账失败！试试其他还款方式吧~");
                message.setTarget("0");
            }
            flag=1;
        }else  if(state==1||state==0){
            message.setTitle("微信还款进度");
            message.setType(2);
            try {
                jstt = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getPay_time()).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(state==0){
                message.setSubtitle("付款失败！试试其他付款方式吧~");
                message.setTarget("0");
            }else {
                message.setSubtitle("付款成功！预计5分钟后到账~");
                message.setTarget("1");
            }
            flag=1;
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        if(StringUtils.isNotEmpty(bean.getAmount())) {
            if(jstt.toString().length()>=19) {
                message.setDesc2("还款时间：" + jstt.toString().substring(5,19));
            }else {
                message.setDesc2("还款时间：" + jstt.toString().substring(5));
            }
            message.setDesc3("还款账户：***" + bean.getCard_tail());
            if (flag == 1) {
                if (StringUtils.isNotEmpty(bean.getPartner_id())) {
                    String userid = getUserIdByPartnerId(bean.getPartner_id());
                    if (StringUtils.isNotEmpty(userid)) {
                        message.setUserId(userid);
                        try {
                            message.setDesc1("还款金额：" + decimalFormat.format(Double.parseDouble(bean.getAmount()) / 100) + "元人民币");
                        }catch (Exception e){
                            logger.info("没有没有获得有效的还款金额,用户=", userid);
                            return 0;
                        }
                        saveMessage(message);
                        logger.info("保存消息成功,用户=", userid);
                        return 1;
                    }
                }
            }
        }
        return 0;
    }
    public  String getUserIdByPartnerId(String partnerId){
        return  messageMapper.queryUserIdByPartnerId(partnerId);
    }

    /**
     * 查询近num个是否存在 未读消息 -1 表示近一个月
     * @param cuserid
     * @return
     */
    public JSONObject getValidMessageCount(String cuserid,int num){
        JSONObject json=new JSONObject();
        if(StringUtils.isEmpty(cuserid)){
            json.put("code",0);
            json.put("desc","用户未登录");
            return json;
        }
        Integer count=messageMapper.queryValidMessageCount(cuserid,num);
        if(count>0){
            Integer updateCount=messageMapper.updateValidMessageList(cuserid,num);
            if(updateCount>0){
                logger.info("用户={}更新的近{}个月的未读消息数量为{}",cuserid,num*-1,updateCount);
            }else {
                logger.info("用户={}更新到的近{}个月的未读消息失败",cuserid,num*-1);
            }
            json.put("code",1);
            json.put("desc","查询成功");
            json.put("data",count);
            logger.info("用户={}查询到的近{}个月的未读消息数量为{}",cuserid,num*-1,count);
        }else {
            logger.info("用户={}没有查询到的近{}个月的新的消息",cuserid,num*-1);
            json.put("code",0);
            json.put("desc","没有查询到新的消息");
        }
        return json;
    }
    public  JSONObject deleteMessageByBillId(String userId,String billId){
        JSONObject json=new JSONObject();
        Integer resultCount=messageMapper.deleteMessageByBillId(userId,billId);
        if(resultCount>0){
            json.put("code",1);
            json.put("desc","删除成功");
            logger.info("删除成功,billId={}",billId);
        }else {
            json.put("code",0);
            json.put("desc","删除失败");
            logger.info("删除失败,billId={}",billId);
        }
        return  json;
    }
}
