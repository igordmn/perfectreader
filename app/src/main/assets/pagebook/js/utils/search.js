"use strict";

define("utils/search", function() {
    return {
        binarySearch: function(low, high, criteria, funcValue, sortedFunc) {
            assert(this.isComparable(funcValue), "Illegal funcValue: " + funcValue);
            let index = -1;
            while (low <= high) {
                const mid = (low + high) >> 1;
                const midValue = sortedFunc(mid);
                assert(typeof midValue == typeof funcValue, "Illegal midValue: " + midValue);

                if (midValue < funcValue) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }

                if (criteria(midValue, funcValue)) {
                    index = mid;
                }
            }
            return index;
        },

        isComparable: function(value) {
            return typeof value == "number" || typeof value == "boolean" || typeof value == "string";
        },

        isUpper: function(val1, val2) {
            return val1 > val2;
        },

        isUpperOrEquals: function(val1, val2) {
            return val1 >= val2;
        },

        isLower: function(val1, val2) {
            return val1 < val2;
        },

        isLowerOrEquals: function(val1, val2) {
            return val1 <= val2;
        },
    }
});
