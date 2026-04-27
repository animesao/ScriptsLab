package com.scriptslab.api.event;

/**
 * Represents a subscription to an event.
 * Can be cancelled to stop receiving events.
 */
public interface EventSubscription {
    
    /**
     * Gets the event type this subscription is for.
     * 
     * @return event class
     */
    Class<? extends PluginEvent> getEventType();
    
    /**
     * Gets the priority of this subscription.
     * 
     * @return priority
     */
    EventBus.EventPriority getPriority();
    
    /**
     * Checks if this subscription is active.
     * 
     * @return true if active
     */
    boolean isActive();
    
    /**
     * Cancels this subscription.
     */
    void cancel();
    
    /**
     * Gets the subscriber object.
     * 
     * @return subscriber
     */
    Object getSubscriber();
}
