package com.caiyi.nirvana.analyse.monitor.meters;

import com.codahale.metrics.*;

import java.util.SortedMap;

/**
 * Created by pc on 2017/3/9.
 */
public interface ReporterCallback {

    void report(SortedMap<String, Gauge> gauges,
                SortedMap<String, Counter> counters,
                SortedMap<String, Histogram> histograms,
                SortedMap<String, Meter> meters,
                SortedMap<String, Timer> timers);

}
