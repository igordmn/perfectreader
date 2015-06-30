function vregexp(pattern, flags, variables) {
    for (var variable in variables) {
        if (variables.hasOwnProperty(variable)) {
            pattern = pattern.replace(new RegExp("%" + variable, 'g'), escapeRegExp(variables[variable]));
        }
    }
    return new RegExp(pattern, flags);
}

function escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}