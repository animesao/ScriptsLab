package com.scriptslab.api.event;

/**
 * Base interface for all custom plugin events.
 */
public interface PluginEvent {
    
    /**
     * Gets the event name.
     * 
     * @return event name
     */
    default String getEventName() {
        return getClass().getSimpleName();
    }
    
    /**
     * Checks if this event is cancellable.
     * 
     * @return true if cancellable
     */
    default boolean isCancellable() {
        return this instanceof Cancellable;
    }
    
    /**
     * Gets the timestamp when this event was created.
     * 
     * @return timestamp in milliseconds
     */
    long getTimestamp();
    
    /**
     * Interface for cancellable events.
     */
    interface Cancellable {
        
        /**
         * Checks if the event is cancelled.
         * 
         * @return true if cancelled
         */
        boolean isCancelled();
        
        /**
         * Sets the cancelled state.
         * 
         * @param cancelled new state
         */
        void setCancelled(boolean cancelled);
    }
}
