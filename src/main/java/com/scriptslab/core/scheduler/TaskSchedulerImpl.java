package com.scriptslab.core.scheduler;

import com.scriptslab.api.scheduler.ScheduledTask;
import com.scriptslab.api.scheduler.TaskScheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Implementation of TaskScheduler wrapping BukkitScheduler.
 * Thread-safe.
 */
public final class TaskSchedulerImpl implements TaskScheduler {
    
    private final Plugin plugin;
    private final BukkitScheduler scheduler;
    private final Map<Integer, ScheduledTaskImpl> tasks;
    private final AtomicInteger taskCounter;
    
    public TaskSchedulerImpl(Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = Bukkit.getScheduler();
        this.tasks = new ConcurrentHashMap<>();
        this.taskCounter = new AtomicInteger(0);
    }
    
    @Override
    public ScheduledTask runTask(Runnable task) {
        BukkitTask bukkitTask = scheduler.runTask(plugin, task);
        return wrapTask(bukkitTask, task, true);
    }
    
    @Override
    public ScheduledTask runTaskAsync(Runnable task) {
        BukkitTask bukkitTask = scheduler.runTaskAsynchronously(plugin, task);
        return wrapTask(bukkitTask, task, false);
    }
    
    @Override
    public ScheduledTask runTaskLater(Runnable task, long delay, TimeUnit unit) {
        long ticks = toTicks(delay, unit);
        BukkitTask bukkitTask = scheduler.runTaskLater(plugin, task, ticks);
        return wrapTask(bukkitTask, task, true);
    }
    
    @Override
    public ScheduledTask runTaskLaterAsync(Runnable task, long delay, TimeUnit unit) {
        long ticks = toTicks(delay, unit);
        BukkitTask bukkitTask = scheduler.runTaskLaterAsynchronously(plugin, task, ticks);
        return wrapTask(bukkitTask, task, false);
    }
    
    @Override
    public ScheduledTask runTaskTimer(Runnable task, long delay, long period, TimeUnit unit) {
        long delayTicks = toTicks(delay, unit);
        long periodTicks = toTicks(period, unit);
        BukkitTask bukkitTask = scheduler.runTaskTimer(plugin, task, delayTicks, periodTicks);
        return wrapTask(bukkitTask, task, true);
    }
    
    @Override
    public ScheduledTask runTaskTimerAsync(Runnable task, long delay, long period, TimeUnit unit) {
        long delayTicks = toTicks(delay, unit);
        long periodTicks = toTicks(period, unit);
        BukkitTask bukkitTask = scheduler.runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
        return wrapTask(bukkitTask, task, false);
    }
    
    @Override
    public <T> CompletableFuture<T> supply(Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runTask(() -> {
            try {
                future.complete(task.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
    
    @Override
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runTaskAsync(() -> {
            try {
                future.complete(task.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
    
    @Override
    public void cancelTasks(Object owner) {
        tasks.values().removeIf(task -> {
            if (task.getOwner() == owner) {
                task.cancel();
                return true;
            }
            return false;
        });
    }
    
    @Override
    public void cancelAllTasks() {
        tasks.values().forEach(ScheduledTask::cancel);
        tasks.clear();
    }
    
    @Override
    public int getActiveTaskCount() {
        return (int) tasks.values().stream()
                .filter(task -> !task.isCancelled())
                .count();
    }
    
    /**
     * Converts time to Minecraft ticks (20 ticks = 1 second).
     */
    private long toTicks(long time, TimeUnit unit) {
        long millis = unit.toMillis(time);
        return millis / 50; // 1 tick = 50ms
    }
    
    /**
     * Wraps a BukkitTask into our ScheduledTask.
     */
    private ScheduledTask wrapTask(BukkitTask bukkitTask, Runnable task, boolean sync) {
        int id = taskCounter.incrementAndGet();
        ScheduledTaskImpl scheduledTask = new ScheduledTaskImpl(
                id, bukkitTask, task, sync);
        tasks.put(id, scheduledTask);
        return scheduledTask;
    }
    
    /**
     * Internal task implementation.
     */
    private class ScheduledTaskImpl implements ScheduledTask {
        
        private final int taskId;
        private final BukkitTask bukkitTask;
        private final Object owner;
        private final boolean sync;
        
        ScheduledTaskImpl(int taskId, BukkitTask bukkitTask, Object owner, boolean sync) {
            this.taskId = taskId;
            this.bukkitTask = bukkitTask;
            this.owner = owner;
            this.sync = sync;
        }
        
        @Override
        public int getTaskId() {
            return taskId;
        }
        
        @Override
        public boolean isCancelled() {
            return bukkitTask.isCancelled();
        }
        
        @Override
        public void cancel() {
            bukkitTask.cancel();
            tasks.remove(taskId);
        }
        
        @Override
        public boolean isRunning() {
            return !isCancelled();
        }
        
        @Override
        public boolean isSync() {
            return sync;
        }
        
        @Override
        public Object getOwner() {
            return owner;
        }
    }
}
