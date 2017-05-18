package com.caiyi.financial.nirvana.core.util;

import com.caiyi.financial.nirvana.core.annotation.StormConfig;
import com.caiyi.financial.nirvana.core.bean.BoltAttr;
import com.caiyi.financial.nirvana.core.bean.StormConfigBean;
import com.caiyi.financial.nirvana.core.constant.ApplicationConstant;
import com.caiyi.financial.nirvana.core.exception.ConfigException;
import com.caiyi.financial.nirvana.core.service.DispatcherBolt;
import com.caiyi.financial.nirvana.core.util.impl.DefaultClassScanner;
import org.apache.storm.Config;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wenshiliang on 2016/4/25.
 */
public class StormUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(StormUtil.class);

//    static List<String> STREAM_IDS;

    public static void buildTopolpoly(TopologyBuilder builder, DRPCSpout drpcSpout, DrpcConfig conf) {
        SpringFactory.clear();

//        STREAM_IDS = new ArrayList<>(conf.boltAttrs.size());
//        Set<String> streamIds = new  HashSet<>(conf.boltAttrs.size());
//        for (BoltAttr attr : conf.boltAttrs) {
//            LOGGER.info("boltid：{}",attr.getStreamId());
//            streamIds.add(attr.getStreamId());
//        }

        String dispatchId = "dispatch";
        builder.setSpout("drpcSpout", drpcSpout, conf.getDrcpSpoutNum());
        //分发bolt
        builder.setBolt(dispatchId, new DispatcherBolt(conf.getBoltAttrs()), conf.getDispatchBoltNum())
                .shuffleGrouping("drpcSpout");

        //返回 bolt設置
        builder.setBolt("return", new ReturnResults(), conf.getReturnBoltNum()).shuffleGrouping("dispatch");

        //业务bolt
//        /**
//         * 根据BoltAttr生成业务bolt
//         */
//        for (BoltAttr attr : conf.boltAttrs) {
//            BoltDeclarer boltDeclarer = builder.setBolt(attr.getBoltId(), attr.newInstance(), attr
// .getParallelismHint()).setNumTasks(attr.getNumTasks());
//            if ("fields".equals(attr.getGroup())) {
//                boltDeclarer.fieldsGrouping("dispatch", attr.getStreamId(), new Fields(attr.getGroupFields()));
//            } else if ("shuffle".equals(attr.getGroup())) {
//                boltDeclarer.shuffleGrouping("dispatch", attr.getStreamId());
//            }
//            //返回
//            returnBoltDeclarer.shuffleGrouping(attr.getBoltId());
//        }
    }

//    public static List<String> getStreamIds() {
//        return STREAM_IDS;
//    }


    public static class DrpcConfig extends Config {
        private List<BoltAttr> boltAttrs;
        private String drcpService;
        private int returnBoltNum = 1;
        private int drcpSpoutNum = 1;
        private int dispatchBoltNum = 1;

        public void setDrcpService(String drcpService) {
            this.drcpService = drcpService;
        }

        public int getDrcpSpoutNum() {
            return drcpSpoutNum;
        }

        public void setDrcpSpoutNum(int drcpSpoutNum) {
            this.drcpSpoutNum = drcpSpoutNum;
        }

        public int getDispatchBoltNum() {
            return dispatchBoltNum;
        }

        public void setDispatchBoltNum(int dispatchBoltNum) {
            this.dispatchBoltNum = dispatchBoltNum;
        }

        private DrpcConfig() {
            init();
        }

        public List<BoltAttr> getBoltAttrs() {
            return boltAttrs;
        }

        public String getDrcpService() {
            return drcpService;
        }

        public int getReturnBoltNum() {
            return returnBoltNum;
        }

        public void setReturnBoltNum(int returnBoltNum) {
            this.returnBoltNum = returnBoltNum;
        }


        private void init() {
            int numWorkers = SystemConfig.getInt(ApplicationConstant.STORMClass.NUM_WORKERS);
            try {
                setReturnBoltNum(SystemConfig.getInt(ApplicationConstant.STORMClass.RETURN_BOLT_NUM));
            } catch (Exception e) {
                LOGGER.info("未设置return bolt num,默认为 1");
            }
            try {
                setDrcpSpoutNum(SystemConfig.getInt(ApplicationConstant.STORMClass.DRPC_SPOUT_NUM));
            } catch (Exception e) {
                LOGGER.info("未设置drpc spout num,默认为 1");
            }
            try {
                setDispatchBoltNum(SystemConfig.getInt(ApplicationConstant.STORMClass.DISPATCH_BOLT_NUM));
            } catch (Exception e) {
                LOGGER.info("未设置dispatch bolt num,默认为 1");
            }
            setDrcpService(SystemConfig.get(ApplicationConstant.DRPC_SERVICE));

            boltAttrs = new ArrayList<>();

            this.setNumWorkers(numWorkers);
            this.setNumAckers(0);//drpc storm 设置acker为0
            String packageName = SystemConfig.get(ApplicationConstant.ANNOTATION_SCAN);

            List<Class<?>> classList = new DefaultClassScanner().getClassListByAnnotation(packageName, StormConfig
                    .class);

            List<StormConfigBean> stormConfigBeenList = new ArrayList<>(classList.size());
            classList.forEach(clazz -> {
                try {

                    StormConfigBean bean = new StormConfigBean();

                    Object obj = clazz.newInstance();
                    StormConfig stormConfig = clazz.getAnnotation(StormConfig.class);
                    String initMethod = stormConfig.initMethod();
                    int order = stormConfig.order();
                    Method method = clazz.getMethod(initMethod, DrpcConfig.class);

                    bean.setInitMethod(method);
                    bean.setTarget(obj);
                    bean.setOrder(order);
                    stormConfigBeenList.add(bean);
                } catch (Exception e) {

                }

            });

            Collections.sort(stormConfigBeenList, (b1, b2) -> b1.getOrder() - b2.getOrder());

            stormConfigBeenList.forEach(bean -> {
                try {
                    bean.getInitMethod().invoke(bean.getTarget(), this);
                } catch (Exception e) {
                    throw new ConfigException("初始化config异常", e);
                }
            });

        }

        public static DrpcConfig newInstance() {
            return Instance.CONFIG;
        }

        static class Instance {
            static DrpcConfig CONFIG = new DrpcConfig();
        }

    }
}
