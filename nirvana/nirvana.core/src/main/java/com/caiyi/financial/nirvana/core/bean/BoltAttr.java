package com.caiyi.financial.nirvana.core.bean;

import com.caiyi.financial.nirvana.core.exception.BoltException;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wenshiliang on 2016/4/25.
 * 根据boltattr创建bolt
 * 1.解析application.conf中的dispatcherBolt获得BoltAttr数组
 * 2.解析application.conf中的注解配置获得BoltAttr数组
 * dispatcherBolt写法：
 dispatcherBolt : [
 {
 boltId:"demo",
 className:"com.caiyi.financial.nirvana.discount.ccard.bolts.DemoBolt",
 parallelismHint:1,
 numTasks:1,
 group:"shuffle",
 groupFields:[],
 streamId:""
 }
 ]
 */
public class BoltAttr implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoltAttr.class);
    private String boltId;
    /**
     * 反射创建的bolt类
     */
    private String className;
    private Class<BaseBolt> cls;

    private int parallelismHint;
    private int numTasks;
    /**
     * 从DispatcherBolt分发策略
     * shuffle或者fields
     */
    private String group = "shuffle";
    /**
     * group值为fields的Field值
     */
    private List<String> groupFields;
    /**
     * steam流名称，默认为boltId
     */
    private String streamId;

    public static void main(String[] args) {
    }

    @Override
    public String toString() {
        return "BoltAttr{" +
                "boltId='" + boltId + '\'' +
                ", className='" + className + '\'' +
                ", parallelismHint=" + parallelismHint +
                ", numTasks=" + numTasks +
                ", group='" + group + '\'' +
                ", groupFields=" +groupFields +
                ", streamId='" + streamId + '\'' +
                '}';
    }

    public String getBoltId() {
        return boltId;
    }

    public void setBoltId(String boltId) {
        if(StringUtils.isEmpty(boltId)){
            throw new BoltException("初始化bolt异常 boltName 不能为空！");
        }
        this.boltId = boltId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
        cls = (Class<BaseBolt>) ClassUtil.loadClass(this.className);
    }

    public int getParallelismHint() {
        return parallelismHint;
    }

    public void setParallelismHint(int parallelismHint) {
        this.parallelismHint = parallelismHint;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(int numTasks) {
        this.numTasks = numTasks;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getGroupFields() {
        return groupFields;
    }

    public void setGroupFields(List<String> groupFields) {
        this.groupFields = groupFields;
    }

    public String getStreamId() {
        if(StringUtils.isEmpty(streamId)){
            return boltId;
        }
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public BaseBolt newInstance(){
        try {
            return cls.newInstance();
        } catch (Exception e) {
            LOGGER.error("bolt class初始化异常",e);
            throw new BoltException(e);
        }
    }

    public Class<BaseBolt> getCls() {
        return cls;
    }

    public void setCls(Class<BaseBolt> cls) {
        this.cls = cls;
        this.className = cls.getName();
    }
}
