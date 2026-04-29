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
     * Can be extended by creating instances with custom levels.
     */
    class EventPriority implements Comparable<EventPriority> {
        private final int level;
        
        // Predefined priorities
        public static final EventPriority LOWEST = new EventPriority(0);
        public static final EventPriority LOW = new EventPriority(1);
        public static final EventPriority NORMAL = new EventPriority(2);
        public static final EventPriority HIGH = new EventPriority(3);
        public static final EventPriority HIGHEST = new EventPriority(4);
        public static final EventPriority MONITOR = new EventPriority(5);
        
        public EventPriority(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
        
        @Override
        public int compareTo(EventPriority other) {
            return Integer.compare(this.level, other.level);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EventPriority that = (EventPriority) obj;
            return this.level == that.level;
        }
        
        @Override
        public int hashCode() {
            return Integer.hashCode(level);
        }
        
        @Override
        public String toString() {
            return "EventPriority{level=" + level + "}";
        }
    }
}
