"use strict";

define("utils/promise", function() {
    return {
        async: function(promiseGenerator) {
            let it = promiseGenerator();
            let next = function (arg) {
                let result = it.next(arg);
                if (result.done) {
                    return Promise.resolve(result.value);
                } else {
                    return result.value.then(next, function(error) { it.throw(error) });
                }
            }
            return next();
        }
    };
});
