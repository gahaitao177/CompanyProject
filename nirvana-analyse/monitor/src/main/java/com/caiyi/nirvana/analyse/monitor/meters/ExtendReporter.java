package com.caiyi.nirvana.analyse.monitor.meters;

import com.codahale.metrics.*;

import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 2017/3/10.
 */
public class ExtendReporter extends ScheduledReporter {


    private ReporterCallback reporterCallback;

    protected ExtendReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit, ReporterCallback reporterCallback) {
        super(registry, name, filter, rateUnit, durationUnit);
        this.reporterCallback = reporterCallback;

    }

    protected ExtendReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit, ScheduledExecutorService executor, ReporterCallback reporterCallback) {
        super(registry, name, filter, rateUnit, durationUnit, executor);
        this.reporterCallback = reporterCallback;
    }

    public static ExtendReporter.Builder forRegistry(MetricRegistry registry) {
        return new ExtendReporter.Builder(registry);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ReporterCallback reporterCallback;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }


        public ExtendReporter.Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public ExtendReporter.Builder convertReporterCallbackTo(ReporterCallback reporterCallback) {
            this.reporterCallback = reporterCallback;
            return this;
        }

        public ExtendReporter.Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public ExtendReporter.Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }


        public ExtendReporter build() {
            return new ExtendReporter(registry, "extend-reporter",
                    filter,
                    rateUnit,
                    durationUnit, reporterCallback);
        }
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {
        reporterCallback.report(gauges, counters, histograms, meters, timers);

    }

}
