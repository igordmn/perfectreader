"use strict";

define("utils/test", function() {
    let seed = 1;

    return {
        runTest: function(testName, test) {
            console.log("_______________ TEST " + testName);
            test();
            console.log("OK");
        },

        runAsyncTest: function(testName, test) {
            console.log("_______________ TEST " + testName);
            test(function() {
                console.log("OK");
            });
        },

        assertEquals: function(expected, actual, message) {
            if (expected != actual) {
                throw message + " failed\nexpected: " + expected + "\nactual: " + actual;
            }
        },

        assertFloatEquals: function(expected, actual, precision, message) {
            if (expected.toFixed(precision) != actual.toFixed(precision)) {
                throw message + " failed\nexpected: " + expected.toFixed(precision) + "\nactual: " + actual.toFixed(precision);
            }
        },

        assertNotNull: function(value, message) {
            if (value == null) {
                throw message + " failed\nvalue: " + value;
            }
        },

        bench: function(name, func) {
            document.body.clientWidth;
            var t1 = new Date().getTime();
            func();
            document.body.clientWidth;
            var t2 = new Date().getTime();
            console.log(name + " time: " + (t2-t1));
        },

        pseudoRandom: function() {
            let x = Math.sin(seed++) * 10000;
            return x - Math.floor(x);
        },
    };
});
