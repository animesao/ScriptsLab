package com.scriptslab.api.metrics;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Metrics collector for monitoring plugin performance.
 */
public interface MetricsCollector {
    
    /**
     * Records a metric value.
     * 
     * @param name metric name
     * @param value metric value
     */
    void record(String name, double value);
    
    /**
     * Increments a counter metric.
     * 
     * @param name counter name
     */
    void increment(String name);
    
    /**
     * Increments a counter by a specific amount.
     * 
     * @param name counter name
     * @param amount amount to increment
     */
    void increment(String name, long amount);
    
    /**
     * Records timing information.
     * 
     * @param name timer name
     * @param durationMs duration in milliseconds
     */
    void recordTiming(String name, long durationMs);
    
    /**
     * Gets a metric value.
     * 
     * @param name metric name
     * @return metric value
     */
    CompletableFuture<Double> getMetric(String name);
    
    /**
     * Gets all metrics.
     * 
     * @return map of all metrics
     */
    CompletableFuture<Map<String, Double>> getAllMetrics();
    
    /**
     * Resets all metrics.
     */
    void reset();
    
    /**
     * Resets a specific metric.
     * 
     * @param name metric name
     */
    void reset(String name);
}
