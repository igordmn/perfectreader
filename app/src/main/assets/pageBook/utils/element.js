function iterateChildrenRecursively(node, callback) {
    iterateChildren(node, function(child) {
        callback(child);
        iterateChildren(child, callback);
    });
}

function iterateChildren(node, callback) {
    if (node.hasChildNodes()) {
        var it = node.firstChild;
        do {
            callback(it);
        } while (it = it.nextSibling);
    }
}
