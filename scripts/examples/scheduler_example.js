// Scheduler API example
Console.log("Scheduler example loaded");

// Run after 3 seconds (60 ticks)
var task1 = Scheduler.runLater(function() {
    Console.log("Delayed task executed!");
}, 60);

// Run every 5 seconds (100 ticks)
var task2 = Scheduler.runTimer(function() {
    Console.log("Repeating task tick");
}, 0, 100);

// Cancel first task after 10 seconds
Scheduler.runLater(function() {
    Scheduler.cancelTask(task1);
    Console.log("Task 1 cancelled");
}, 200);

// Async task (cannot use Bukkit API!)
var task3 = Scheduler.runAsync(function() {
    Console.log("Async task running (no Bukkit API here)");
});

// Example: broadcast message every minute
Scheduler.runTimer(function() {
    var Bukkit = Java.type('org.bukkit.Bukkit');
    Bukkit.broadcastMessage("§6[Server] §7Minute has passed!");
}, 0, 20 * 60);