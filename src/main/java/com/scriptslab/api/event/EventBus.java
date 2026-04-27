package com.scriptslab.api.event;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Custom event bus for cross-module communication.
 * Thread-safe.
 */
public interface EventBus {
    
    /**
     * Posts an event to all subscribers.
     * 
     * @param event event to post
     * @param <T> event type
     * @return future that completes when all handlers finish
     */
    <T extends PluginEvent> CompletableFuture<T> post(T event);
    
    /**
     * Posts an event asynchronously.
     * 
     * @param event event to post
     * @param <T> event type
     * @return future that completes when all handlers finish
     */
    <T extends PluginEvent> CompletableFuture<T> postAsync(T event);
    
    /**
     * Subscribes to an event type.
     * 
     * @param eventType event class
     * @param handler event handler
     * @param priority handler priority
     * @param <T> event type
     * @return subscription that can be cancelled
     */
    <T extends PluginEvent> EventSubscription subscribe(
            Class<T> eventType,
            Consumer<T> handler,
            EventPriority priority
    );
    
    /**
     * Subscribes with normal priority.
     * 
     * @param eventType event class
     * @param handler event handler
     * @param <T> event type
     * @return subscription
     */
    default <T extends PluginEvent> EventSubscription subscribe(
            Class<T> eventType,
            Consumer<T> handler
    ) {
        return subscribe(eventType, handler, EventPriority.NORMAL);
    }
    
    /**
     * Unsubscribes a subscription.
     * 
     * @param subscription subscription to cancel
     */
    void unsubscribe(EventSubscription subscription);
    
    /**
     * Unsubscribes all handlers for a subscriber.
     * 
     * @param subscriber subscriber object
     */
    void unsubscribeAll(Object subscriber);
    
    /**
     * Gets the number of subscribers for an event type.
     * 
     * @param eventType event class
     * @return subscriber count
     */
    int getSubscriberCount(Class<? extends PluginEvent> eventType);
    
    /**
     * Event priority levels.
     */
    enum EventPriority {
        LOWEST(0),
        LOW(1),
        NORMAL(2),
        HIGH(3),
        HIGHEST(4),
        MONITOR(5);
        
        private final int level;
        
        EventPriority(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }
}
