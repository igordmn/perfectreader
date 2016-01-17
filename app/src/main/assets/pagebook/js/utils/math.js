"use strict";

define("utils/math", function() {
    return {
        clamp: function(value, min, max) {
            return Math.min(Math.max(value, min), max);
        }
    };
});
