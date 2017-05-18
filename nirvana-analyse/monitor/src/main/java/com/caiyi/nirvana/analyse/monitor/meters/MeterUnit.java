package com.caiyi.nirvana.analyse.monitor.meters;

import com.caiyi.nirvana.analyse.model.MonitorEvent;
import com.codahale.metrics.Meter;

/**
 * Created by pc on 2017/3/9.
 */
public class MeterUnit {

    private Meter meter;                    //计量
    private MonitorEvent monitorEvent;      //事件内容
    private long lastTime;                  //最后计数时间戳
    private long repeatedCount;             //已告警次数

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public MonitorEvent getMonitorEvent() {
        return monitorEvent;
    }

    public void setMonitorEvent(MonitorEvent monitorEvent) {
        this.monitorEvent = monitorEvent;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getRepeatedCount() {
        return repeatedCount;
    }

    public void setRepeatedCount(long repeatedCount) {
        this.repeatedCount = repeatedCount;
    }

    @Override
    public String toString() {
        return "MeterUnit{" +
                ", monitorEvent=" + monitorEvent +
                ", lastTime=" + lastTime +
                ", repeatedCount=" + repeatedCount +
                '}';
    }
}
