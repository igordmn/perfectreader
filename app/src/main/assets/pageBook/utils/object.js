function mergeObjects(obj1, obj2){
    var obj3 = {};
    for (var attr in obj1) {
        obj3[attr] = obj1[attr];
    }
    for (var attr in obj2) {
        obj3[attr] = obj2[attr];
    }
    return obj3;
}