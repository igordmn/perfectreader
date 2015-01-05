function vregexp(pattern, flags, variables) {
    for (var variable in variables) {
        pattern = pattern.replace(new RegExp("%" + variable, 'g'), variables[variable]);
    }
    return new RegExp(pattern, flags);
}