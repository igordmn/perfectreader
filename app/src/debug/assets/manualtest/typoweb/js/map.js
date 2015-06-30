function toMap(array, keyFunc, valueFunc) {
    var map = {};
    array.forEach(function(item) {
        map[keyFunc(item)] = valueFunc(item);
    });
    return map;
}