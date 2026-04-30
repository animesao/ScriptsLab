package com.scriptslab.core.script;

import com.scriptslab.ScriptsLabPlugin;
import org.graalvm.polyglot.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Thread-safe scheduler for JavaScript scripts.
 * Executes JS callbacks on the main server thread via BukkitScheduler.
 *
 * Usage in JS:
 *   var taskId = Scheduler.runLater(function() { ... }, 20);
 *   var taskId = Scheduler.runTimer(function() { ... }, 0, 20);
 *   Scheduler.cancel(taskId);
 */
public final class ScriptScheduler {

    private final ScriptsLabPlugin plugin;
    private final Logger logger;
    private final Map<Integer, org.bukkit.scheduler.BukkitTask> activeTasks;
    private final AtomicInteger taskIdCounter;

    public ScriptScheduler(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger("ScriptScheduler");
        this.activeTasks = new ConcurrentHashMap<>();
        this.taskIdCounter = new AtomicInteger(0);
    }

    /**
     * Runs a JS function after a delay on the main thread.
     *
     * @param callback JS function (Value or executable)
     * @param delayTicks delay in ticks (20 ticks = 1 second)
     * @return task ID (use to cancel)
     */
    public int runLater(Object callback, long delayTicks) {
        int id = taskIdCounter.incrementAndGet();
        org.bukkit.scheduler.BukkitTask task = plugin.getServer().getScheduler()
                .runTaskLater(plugin, () -> {
                    activeTasks.remove(id);
                    invokeCallback(callback, "runLater#" + id);
                }, delayTicks);
        activeTasks.put(id, task);
        return id;
    }

    /**
     * Runs a JS function repeatedly on the main thread.
     *
     * @param callback JS function
     * @param delayTicks initial delay in ticks
     * @param periodTicks period in ticks
     * @return task ID (use to cancel)
     */
    public int runTimer(Object callback, long delayTicks, long periodTicks) {
        int id = taskIdCounter.incrementAndGet();
        org.bukkit.scheduler.BukkitTask task = plugin.getServer().getScheduler()
                .runTaskTimer(plugin, () -> invokeCallback(callback, "runTimer#" + id),
                        delayTicks, periodTicks);
        activeTasks.put(id, task);
        return id;
    }

    /**
     * Runs a JS function asynchronously (NOT on main thread).
     * WARNING: Cannot call Bukkit API from async context!
     *
     * @param callback JS function
     * @return task ID
     */
    public int runAsync(Object callback) {
        int id = taskIdCounter.incrementAndGet();
        org.bukkit.scheduler.BukkitTask task = plugin.getServer().getScheduler()
                .runTaskAsynchronously(plugin, () -> {
                    activeTasks.remove(id);
                    invokeCallback(callback, "runAsync#" + id);
                });
        activeTasks.put(id, task);
        return id;
    }

    /**
     * Cancels a scheduled task.
     *
     * @param taskId task ID returned by runLater/runTimer/runAsync
     */
    public void cancel(int taskId) {
        org.bukkit.scheduler.BukkitTask task = activeTasks.remove(taskId);
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Cancels all active script tasks.
     */
    public void cancelAll() {
        activeTasks.values().forEach(org.bukkit.scheduler.BukkitTask::cancel);
        activeTasks.clear();
    }

    /**
     * Returns number of active tasks.
     */
    public int getActiveCount() {
        return activeTasks.size();
    }

    private void invokeCallback(Object callback, String context) {
        try {
            if (callback instanceof Value v) {
                if (v.canExecute()) {
                    v.executeVoid();
                } else {
                    logger.warning("[ScriptScheduler] Callback is not executable in " + context);
                }
            } else {
                logger.warning("[ScriptScheduler] Unknown callback type in " + context
                        + ": " + (callback == null ? "null" : callback.getClass().getName()));
            }
        } catch (Exception e) {
            logger.warning("[ScriptScheduler] Error in scheduled callback " + context + ": " + e.getMessage());
        }
    }
}
