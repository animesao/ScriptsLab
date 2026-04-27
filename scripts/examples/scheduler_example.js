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

// Run a repeating task every 30 seconds
var taskId = API.runTimer(function() {
    var players = API.getOnlinePlayers();
    
    if (players.size() > 0) {
        API.broadcast("&a[Auto-Save] &7Server auto-saving... " + players.size() + " players online");
    }
    
}, 600, 600); // 30 seconds delay, 30 seconds period

console.log("Repeating task started with ID: " + taskId);

// Примечание: runAsync() не поддерживается из-за ограничений GraalVM
// JavaScript Context не может быть использован из разных потоков
// Используйте runLater() или runTimer() для отложенных задач

console.log("Scheduler example loaded successfully!");
