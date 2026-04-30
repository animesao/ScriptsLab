// Storage API example
Console.log("Storage example loaded");

// Open a storage namespace
var db = Storage.open('example_data');

// Set some values
db.set('player_count', 0);
db.set('last_winner', 'No one yet');
db.set('game.scores.player1', 100);
db.set('game.scores.player2', 200);

// Get values
var count = db.get('player_count');
Console.log("Player count: " + count);

// Increment
db.increment('player_count');
Console.log("New count: " + db.get('player_count'));

// Check if exists
if (db.has('last_winner')) {
    Console.log("Last winner: " + db.get('last_winner'));
}

// Nested paths
db.set('config.difficulty', 'hard');
db.set('config.max_players', 10);

// Get all keys
var keys = db.keys();
Console.log("Top-level keys: " + Array.from(keys));

// Save to disk (auto-saves on shutdown, but you can force)
db.save();

// Example: player data
Commands.register('mystats', function(sender) {
    if (!sender.isPlayer()) return;
    
    var playerName = sender.getName();
    var playerDb = Storage.open('player_' + playerName);
    
    var playCount = playerDb.getOrDefault('play_count', 0);
    playerDb.increment('play_count');
    
    sender.sendMessage('§aYou have played ' + (playCount + 1) + ' times');
});

// Example: leaderboard
Commands.register('leaderboard', function(sender) {
    var leaderboard = Storage.open('leaderboard');
    var scores = leaderboard.toMap();
    
    sender.sendMessage('§6=== Leaderboard ===');
    for (var key in scores) {
        if (scores.hasOwnProperty(key)) {
            sender.sendMessage('§7' + key + ': §e' + scores[key]);
        }
    }
});