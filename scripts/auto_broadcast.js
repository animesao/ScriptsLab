/**
 * Автоматические объявления
 */

var messages = [
    '§6[Объявление] §eНе забудьте проголосовать за сервер!',
    '§6[Объявление] §eПосетите наш Discord!',
    '§6[Объявление] §eИспользуйте §a/fly §eдля полёта!',
    '§6[Объявление] §eИспользуйте §a/heal §eдля лечения!'
];

var currentIndex = 0;

// Отправлять сообщение каждые 5 минут
Scheduler.runTimer(function() {
    Server.broadcast(messages[currentIndex]);
    currentIndex = (currentIndex + 1) % messages.length;
}, 100, 6000); // 100 тиков задержка, 6000 тиков = 5 минут

Console.log('Автоматические объявления запущены');
