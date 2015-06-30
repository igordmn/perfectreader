window.debugTimer = create(function() {
    var lastTime;

    return {
        start: function(message) {
            lastTime = new Date().getTime();
            if (message !== undefined) {
                console.next("debugTimer. time: 0. " + message);
            }
        },

        next: function(message) {
            var nowTime = new Date().getTime();
            var diff = nowTime - lastTime;
            lastTime = nowTime;
            if (message !== undefined) {
                console.log("debugTimer. time: " + diff + ". " + message);
            }
        }
    }
});