package com.scriptslab.core.metrics;

import com.scriptslab.api.metrics.MetricsCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Thread-safe metrics collector implementation.
 */
public final class MetricsCollectorImpl implements MetricsCollector {
    
    private final Map<String, DoubleAdder> metrics;
    private final Map<String, AtomicLong> counters;
    private final Map<String, TimingMetric> timings;
    
    public MetricsCollectorImpl() {
        this.metrics = new ConcurrentHashMap<>();
        this.counters = new ConcurrentHashMap<>();
        this.timings = new ConcurrentHashMap<>();
    }
    
    @Override
    public void record(String name, double value) {
        metrics.computeIfAbsent(name, k -> new DoubleAdder()).add(value);
    }
    
    @Override
    public void increment(String name) {
        increment(name, 1);
    }
    
    @Override
    public void increment(String name, long amount) {
        counters.computeIfAbsent(name, k -> new AtomicLong()).addAndGet(amount);
    }
    
    @Override
    public void recordTiming(String name, long durationMs) {
        timings.computeIfAbsent(name, k -> new TimingMetric()).record(durationMs);
    }
    
    @Override
    public CompletableFuture<Double> getMetric(String name) {
        return CompletableFuture.supplyAsync(() -> {
            // Check metrics
            DoubleAdder adder = metrics.get(name);
            if (adder != null) {
                return adder.sum();
            }
            
            // Check counters
            AtomicLong counter = counters.get(name);
            if (counter != null) {
                return (double) counter.get();
            }
            
            // Check timings
            TimingMetric timing = timings.get(name);
            if (timing != null) {
                return timing.getAverage();
            }
            
            return 0.0;
        });
    }
    
    @Override
    public CompletableFuture<Map<String, Double>> getAllMetrics() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Double> result = new HashMap<>();
            
            // Add all metrics
            metrics.forEach((name, adder) -> result.put(name, adder.sum()));
            
            // Add all counters
            counters.forEach((name, counter) -> result.put(name, (double) counter.get()));
            
            // Add all timings
            timings.forEach((name, timing) -> {
                result.put(name + ".avg", timing.getAverage());
                result.put(name + ".min", (double) timing.getMin());
                result.put(name + ".max", (double) timing.getMax());
                result.put(name + ".count", (double) timing.getCount());
            });
            
            return result;
        });
    }
    
    @Override
    public void reset() {
        metrics.clear();
        counters.clear();
        timings.clear();
    }
    
    @Override
    public void reset(String name) {
        metrics.remove(name);
        counters.remove(name);
        timings.remove(name);
    }
    
    /**
     * Timing metric with min/max/avg tracking.
     */
    private static class TimingMetric {
        private final AtomicLong count = new AtomicLong();
        private final AtomicLong total = new AtomicLong();
        private final AtomicLong min = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong max = new AtomicLong(Long.MIN_VALUE);
        
        void record(long duration) {
            count.incrementAndGet();
            total.addAndGet(duration);
            
            // Update min
            long currentMin;
            do {
                currentMin = min.get();
                if (duration >= currentMin) break;
            } while (!min.compareAndSet(currentMin, duration));
            
            // Update max
            long currentMax;
            do {
                currentMax = max.get();
                if (duration <= currentMax) break;
            } while (!max.compareAndSet(currentMax, duration));
        }
        
        double getAverage() {
            long c = count.get();
            return c > 0 ? (double) total.get() / c : 0.0;
        }
        
        long getMin() {
            long m = min.get();
            return m == Long.MAX_VALUE ? 0 : m;
        }
        
        long getMax() {
            long m = max.get();
            return m == Long.MIN_VALUE ? 0 : m;
        }
        
        long getCount() {
            return count.get();
        }
    }
}
