package com.scriptslab.api.scheduler;

/**
 * Represents a scheduled task.
 */
public interface ScheduledTask {
    
    /**
     * Gets the task ID.
     * 
     * @return task identifier
     */
    int getTaskId();
    
    /**
     * Checks if the task is cancelled.
     * 
     * @return true if cancelled
     */
    boolean isCancelled();
    
    /**
     * Cancels the task.
     */
    void cancel();
    
    /**
     * Checks if the task is running.
     * 
     * @return true if running
     */
    boolean isRunning();
    
    /**
     * Checks if the task is synchronous.
     * 
     * @return true if sync
     */
    boolean isSync();
    
    /**
     * Gets the task owner.
     * 
     * @return owner object
     */
    Object getOwner();
}
