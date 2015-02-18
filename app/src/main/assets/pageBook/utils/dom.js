function iterateChildrenRecursively(node, callback) {
    var iterateChildrenCallback = function(child) {
        callback(child);
        iterateChildren(child, iterateChildrenCallback);
    };
    iterateChildren(node, iterateChildrenCallback);
}

function iterateChildren(node, callback) {
    if (node.hasChildNodes()) {
        var it = node.firstChild;
        do {
            callback(it);
        } while (it = it.nextSibling);
    }
}
