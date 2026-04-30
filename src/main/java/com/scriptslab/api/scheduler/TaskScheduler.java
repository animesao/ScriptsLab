package com.scriptslab.api.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Task scheduler for running tasks on the server.
 * Wrapper around BukkitScheduler with better API.
 */
public interface TaskScheduler {
    
    /**
     * Runs a task on the main thread.
     * 
     * @param task task to run
     * @return scheduled task
     */
    ScheduledTask runTask(Runnable task);
    
    /**
     * Runs a task asynchronously.
     * 
     * @param task task to run
     * @return scheduled task
     */
    ScheduledTask runTaskAsync(Runnable task);
    
    /**
     * Runs a task later on the main thread.
     * 
     * @param task task to run
     * @param delay delay before execution
     * @param unit time unit
     * @return scheduled task
     */
    ScheduledTask runTaskLater(Runnable task, long delay, TimeUnit unit);
    
    /**
     * Runs a task later asynchronously.
     * 
     * @param task task to run
     * @param delay delay before execution
     * @param unit time unit
     * @return scheduled task
     */
    ScheduledTask runTaskLaterAsync(Runnable task, long delay, TimeUnit unit);
    
    /**
     * Runs a repeating task on the main thread.
     * 
     * @param task task to run
     * @param delay initial delay
     * @param period repeat period
     * @param unit time unit
     * @return scheduled task
     */
    ScheduledTask runTaskTimer(Runnable task, long delay, long period, TimeUnit unit);
    
    /**
     * Runs a repeating task asynchronously.
     * 
     * @param task task to run
     * @param delay initial delay
     * @param period repeat period
     * @param unit time unit
     * @return scheduled task
     */
    ScheduledTask runTaskTimerAsync(Runnable task, long delay, long period, TimeUnit unit);
    
    /**
     * Runs a task and returns a CompletableFuture.
     * 
     * @param task task to run
     * @param <T> result type
     * @return future with result
     */
    <T> CompletableFuture<T> supply(java.util.function.Supplier<T> task);
    
    /**
     * Runs a task asynchronously and returns a CompletableFuture.
     * 
     * @param task task to run
     * @param <T> result type
     * @return future with result
     */
    <T> CompletableFuture<T> supplyAsync(java.util.function.Supplier<T> task);
    
    /**
     * Cancels all tasks for a specific owner.
     * 
     * @param owner task owner
     */
    void cancelTasks(Object owner);
    
    /**
     * Cancels all tasks.
     */
    void cancelAllTasks();
    
    /**
     * Gets the number of active tasks.
     * 
     * @return task count
     */
    int getActiveTaskCount();
}
