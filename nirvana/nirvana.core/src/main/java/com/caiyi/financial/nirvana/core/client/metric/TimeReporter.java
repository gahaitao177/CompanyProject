package com.caiyi.financial.nirvana.core.client.metric;

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2017/3/7.
 */
public class TimeReporter extends ScheduledReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeReporter.class);

    private MetricRegistry registry = null;
    private String name;
    private boolean everyTimeToRemove = true;


    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    public MetricRegistry getRegistry(){
        return registry;
    }


    protected TimeReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit
            durationUnit, ScheduledExecutorService executor, boolean everyTimeToRemove) {
        super(registry, name, filter, rateUnit, durationUnit, executor);
        this.registry = registry;
        this.everyTimeToRemove = true;
        this.name = name;
    }



    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        if (!counters.isEmpty()) {
            StringBuilder builder = new StringBuilder(128);
            builder.append(this.name).append(":\n*************************************\n");
            for (Map.Entry<String, Counter> entry : counters.entrySet()) {
//                System.out.println(entry.getKey()+"---"+ entry.getValue().getCount());
                builder.append(entry.getKey()).append(":    ").append(entry.getValue().getCount()).append("\n");
            }
            builder.append("*************************************");
            LOGGER.info(builder.toString());
        }
        registry.removeMatching(MetricFilter.ALL);


    }



    public static class Builder {
        private final MetricRegistry registry;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean everyTimeToRemove;
        private String name;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.executor = null;
            this.everyTimeToRemove = true;
            this.name = "my-reporter";
        }

        /**
         * 每次report完成后，清空 registry
         * @param everyTimeToRemove
         * @return
         */
        public Builder everyTimeToRemove(boolean everyTimeToRemove) {
            this.everyTimeToRemove = everyTimeToRemove;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        /**
         * Specifies the executor to use while scheduling reporting of metrics.
         * Default value is null.
         * Null value leads to executor will be auto created on start.
         *
         * @param executor the executor to use while scheduling reporting of metrics.
         * @return {@code this}
         */
        public Builder scheduleOn(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }







        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Builds a {@link ConsoleReporter} with the given properties.
         *
         * @return a {@link ConsoleReporter}
         */
        public TimeReporter build() {
            return new TimeReporter(registry,
                    name,
                    filter,
                    rateUnit,
                    durationUnit,
                    executor,
                    everyTimeToRemove);
        }
    }
}
