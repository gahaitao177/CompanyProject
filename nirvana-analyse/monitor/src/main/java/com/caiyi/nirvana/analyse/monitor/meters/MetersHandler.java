package com.caiyi.nirvana.analyse.monitor.meters;

import com.caiyi.nirvana.analyse.model.MonitorEvent;
import com.codahale.metrics.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by rongkang on 2017-03-08.
 */
public class MetersHandler {

    public Logger logger = LogManager.getLogger(getClass());

    static final MetricRegistry metrics = new MetricRegistry();
    static Map<String, MeterUnit> meterUnitMap = new ConcurrentHashMap();//记录告警信息

    private int thresholdCount; //次数阈值
    private int thresholdTime;  //时间间隔阈值
    private int[] triggerRules; //触发规则
    private MetersCallback<MeterUnit> metersCallback;
    private ReporterCallback reporterCallback;

    public MetersHandler(int thresholdCount, int thresholdTime, int[] triggerRules) {
        this.thresholdCount = thresholdCount;
        this.thresholdTime = thresholdTime;
        this.triggerRules = triggerRules;

        reporterCallback = new ReporterCallback() {
            @Override
            public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, com.codahale.metrics.Timer> timers) {
                if (!meters.isEmpty()) {
                    for (Map.Entry<String, Meter> entry : meters.entrySet()) {
                        //判断是否满足告警条件
                        String meterKey = entry.getKey();
                        Meter meter = entry.getValue();
                        double minuteRate = meter.getMeanRate();

                        logger.debug("key:" + meterKey +
                                ";速率阈值：" + thresholdCount / (thresholdTime * 60D) +
                                ";次数" + meter.getCount() +
                                ";速率:MeanRate：" + meter.getMeanRate() +
                                ";OneMinuteRate：" + meter.getOneMinuteRate() +
                                ";FiveMinuteRate：" + meter.getFiveMinuteRate() +
                                ";FifteenMinuteRate：" + meter.getFifteenMinuteRate());

                        if (minuteRate > thresholdCount / (thresholdTime * 60D) && meter.getCount() >= thresholdCount) {
                            MeterUnit meterUnit = meterUnitMap.get(meterKey);
                            meterUnit.setRepeatedCount(meterUnit.getRepeatedCount() + 1);//增加统计次数
                            if (rulesContains((int) meterUnit.getRepeatedCount())) {
                                //告警逻辑
                                metersCallback.report(meterUnit);
                            }
                            metrics.remove(meterKey);//移除此次计量
                        }
                    }
                }

                Set<String> keySet = meterUnitMap.keySet();
                for (String key : keySet) {
                    MeterUnit meterUnit = meterUnitMap.get(key);
                    if (meterUnit.getLastTime() < System.currentTimeMillis() - (thresholdTime * 60 * 1000)) {
                        meterUnitMap.remove(key);
                        metrics.remove(key);
                        logger.info("移除超时计量：" + meterUnit.toString());
                    }
                }
            }
        };

        ExtendReporter reporter = ExtendReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .convertReporterCallbackTo(reporterCallback)
                .build();
        reporter.start(1, TimeUnit.SECONDS);//告警频率

//        new Thread(() -> {
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    Set<String> keySet = meterUnitMap.keySet();
//                    for(String key:keySet){
//                        MeterUnit meterUnit =meterUnitMap.get(key);
//                        if(meterUnit.getLastTime()< System.currentTimeMillis() - (thresholdTime * 60 * 1000)){
//                            meterUnitMap.remove(key);
//                            metrics.remove(key);
//                            logger.info("移除超时计量：" + meterUnit.toString());
//                        }
//                    }
//                }
//            };
//            Timer timer = new Timer();
//            timer.schedule(task, 0,30 * 1000);//一分钟执行一次   清除超过thresholdTime时间间隔的计量
//
//        }).start();
    }

    /**
     * 计量 计数
     *
     * @param monitorEvent 告警事件
     */
    public void mark(MonitorEvent monitorEvent) throws Exception {
        if (monitorEvent.getKey() == null || "".equals(monitorEvent.getKey())) {
            throw new IllegalArgumentException("Parameters  of \"key\" cannot be empty");
        }
        String meterKey = monitorEvent.getKey();
        Meter meter = metrics.meter(meterKey);
        if (meterUnitMap.containsKey(meterKey)) {
            MeterUnit meterUnit = meterUnitMap.get(meterKey);
            meterUnit.setLastTime(System.currentTimeMillis());
            meterUnit.setMonitorEvent(monitorEvent);
            meterUnit.setMeter(meter);
        } else {
            MeterUnit meterUnit = new MeterUnit();
            meterUnit.setLastTime(System.currentTimeMillis());
            meterUnit.setMonitorEvent(monitorEvent);
            meterUnit.setMeter(meter);
            meterUnit.setRepeatedCount(0);
            meterUnitMap.put(meterKey, meterUnit);
        }
        meter.mark();

    }

    private boolean rulesContains(int num) {
        for (int item : triggerRules)
            if (num == item)
                return true;
        return false;

    }


    public int getThresholdCount() {
        return thresholdCount;
    }

    public void setThresholdCount(int thresholdCount) {
        this.thresholdCount = thresholdCount;
    }

    public int getThresholdTime() {
        return thresholdTime;
    }

    public void setThresholdTime(int thresholdTime) {
        this.thresholdTime = thresholdTime;
    }

    public MetersCallback<MeterUnit> getMetersCallback() {
        return metersCallback;
    }

    public void setMetersCallback(MetersCallback<MeterUnit> metersCallback) {
        this.metersCallback = metersCallback;
    }
}
