package com.scriptslab.core.event;

import com.scriptslab.api.event.EventBus;
import com.scriptslab.api.event.EventSubscription;
import com.scriptslab.api.event.PluginEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe implementation of EventBus.
 * Uses ConcurrentHashMap and CopyOnWriteArrayList for thread safety.
 */
public final class EventBusImpl implements EventBus {
    
    private final Logger logger;
    private final Map<Class<? extends PluginEvent>, List<SubscriptionImpl<?>>> subscribers;
    
    public EventBusImpl() {
        this.logger = Logger.getLogger("EventBus");
        this.subscribers = new ConcurrentHashMap<>();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PluginEvent> CompletableFuture<T> post(T event) {
        return CompletableFuture.supplyAsync(() -> {
            Class<? extends PluginEvent> eventType = event.getClass();
            List<SubscriptionImpl<?>> subs = subscribers.get(eventType);
            
            if (subs == null || subs.isEmpty()) {
                return event;
            }
            
            // Sort by priority
            List<SubscriptionImpl<?>> sortedSubs = new ArrayList<>(subs);
            sortedSubs.sort(Comparator.comparingInt(s -> s.priority.getLevel()));
            
            for (SubscriptionImpl<?> sub : sortedSubs) {
                if (!sub.isActive()) {
                    continue;
                }
                
                try {
                    ((Consumer<T>) sub.handler).accept(event);
                    
                    // Check if event was cancelled
                    if (event.isCancellable() && 
                        event instanceof PluginEvent.Cancellable cancellable &&
                        cancellable.isCancelled()) {
                        break;
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error handling event " + eventType.getSimpleName(), e);
                }
            }
            
            return event;
        });
    }
    
    @Override
    public <T extends PluginEvent> CompletableFuture<T> postAsync(T event) {
        return post(event); // Already async
    }
    
    @Override
    public <T extends PluginEvent> EventSubscription subscribe(
            Class<T> eventType,
            Consumer<T> handler,
            EventPriority priority) {
        
        if (eventType == null || handler == null || priority == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        SubscriptionImpl<T> subscription = new SubscriptionImpl<>(
                eventType, handler, priority, handler);
        
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(subscription);
        
        logger.fine("Subscribed to " + eventType.getSimpleName() + 
                   " with priority " + priority);
        
        return subscription;
    }
    
    @Override
    public void unsubscribe(EventSubscription subscription) {
        if (subscription == null) {
            return;
        }
        
        subscription.cancel();
        
        List<SubscriptionImpl<?>> subs = subscribers.get(subscription.getEventType());
        if (subs != null) {
            subs.remove(subscription);
        }
        
        logger.fine("Unsubscribed from " + subscription.getEventType().getSimpleName());
    }
    
    @Override
    public void unsubscribeAll(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        
        int count = 0;
        for (List<SubscriptionImpl<?>> subs : subscribers.values()) {
            Iterator<SubscriptionImpl<?>> iterator = subs.iterator();
            while (iterator.hasNext()) {
                SubscriptionImpl<?> sub = iterator.next();
                if (sub.subscriber == subscriber) {
                    sub.cancel();
                    iterator.remove();
                    count++;
                }
            }
        }
        
        logger.fine("Unsubscribed " + count + " handlers for " + subscriber);
    }
    
    @Override
    public int getSubscriberCount(Class<? extends PluginEvent> eventType) {
        List<SubscriptionImpl<?>> subs = subscribers.get(eventType);
        return subs != null ? subs.size() : 0;
    }
    
    /**
     * Internal subscription implementation.
     */
    private static class SubscriptionImpl<T extends PluginEvent> implements EventSubscription {
        
        private final Class<T> eventType;
        private final Consumer<T> handler;
        private final EventPriority priority;
        private final Object subscriber;
        private volatile boolean active;
        
        SubscriptionImpl(Class<T> eventType, Consumer<T> handler, 
                        EventPriority priority, Object subscriber) {
            this.eventType = eventType;
            this.handler = handler;
            this.priority = priority;
            this.subscriber = subscriber;
            this.active = true;
        }
        
        @Override
        public Class<? extends PluginEvent> getEventType() {
            return eventType;
        }
        
        @Override
        public EventPriority getPriority() {
            return priority;
        }
        
        @Override
        public boolean isActive() {
            return active;
        }
        
        @Override
        public void cancel() {
            active = false;
        }
        
        @Override
        public Object getSubscriber() {
            return subscriber;
        }
    }
}
