"use strict";

(function() {
    const modules = {};

    window.require = function(name) {
        console.assert(modules[name], 'Module "' + name + '" doesn\'t exist');
        return modules[name];
    }

    window.define = function(name, initializer) {
        console.assert(!modules[name], 'Module "' + name + '" already exists');
        modules[name] = initializer();
    }
})();
