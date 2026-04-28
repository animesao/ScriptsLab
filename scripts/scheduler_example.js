/**
 * Example: Task Scheduler Usage
 * Demonstrates using the scheduler from JavaScript
 */

console.log("Setting up scheduled tasks...");

// Run a task after 5 seconds (100 ticks)
API.runLater(function() {
    API.broadcast("&6[Scheduler] &eThis message was delayed by 5 seconds!");
}, 100);

console.log("Delayed task scheduled");

// NOTE: runTimer() and runAsync() removed due to GraalVM multithreading restrictions
// JavaScript callbacks cannot be called from Bukkit scheduler threads

console.log("Scheduler example loaded successfully!");
