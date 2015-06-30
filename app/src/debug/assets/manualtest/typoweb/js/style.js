function createStylesheet(document) {
    var style = document.createElement("style");
    style.appendChild(document.createTextNode(""));  // WebKit hack
    document.head.appendChild(style);
    return style.sheet;
}

function addCSSStyle(stylesheet, ruleName, content) {
    var index = stylesheet.cssRules.length;
    stylesheet.insertRule(ruleName + " {" + (content ? content : "") + "}", index);
    return stylesheet.cssRules[index].style;
}