/**
 * Example: Simple Command Registration
 * Shows how to register a basic command from JavaScript
 */

// Register a simple /hello command
API.log("Registering /hello command...");

// Note: Command registration from scripts requires additional implementation
// This is a demonstration of the intended API

// For now, we can use the scheduler and player management
API.log("Script loaded: simple_command.js");

// Example of using the API
var players = API.getOnlinePlayers();
API.log("Current online players: " + players.size());
