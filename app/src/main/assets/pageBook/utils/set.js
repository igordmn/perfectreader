function stringSet(items) {
    var set = {};

    for (var i in items) {
        set[items[i]] = true;
    }

    return {
        contains: function(item) {
            return set[item] === true;
        }
    }
}