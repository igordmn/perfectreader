/* ------------------------------------- До загрузки ------------------------------ */

var loaded = false;
var stylesheet = createStylesheet();
var pRule = addCSSRule(stylesheet, "p");
var divRule = addCSSRule(stylesheet, "div");
var tdRule = addCSSRule(stylesheet, "td");
var dtRule = addCSSRule(stylesheet, "dt");
var ddRule = addCSSRule(stylesheet, "dd");
var brRule = addCSSRule(stylesheet, "br");
var liRule = addCSSRule(stylesheet, "li");
__configure();

/* ------------------------------------- После загрузки ------------------------------ */

window.onload = function(e){
    __javaBridge.setScreenWidth(document.body.clientWidth);
    loaded = true;
    onContentChanged();
    __javaBridge.finishLoad();
}

/* ------------------------------------- Функции ------------------------------------- */

function setPageConfiguration() {
    var mainDiv = document.getElementById("__mainDiv");
    document.body.style.setProperty("margin-top", __pageTopPaddingInPixels + "px", "important");
    document.body.style.setProperty("margin-bottom", __pageBottomPaddingInPixels + "px", "important");
    document.body.style.setProperty("margin-right", "0px", "important");
    document.body.style.setProperty("margin-left", "0px", "important");
    document.body.style.setProperty("padding", "0", "important");
    mainDiv.style.setProperty("margin-top", "0px", "important");
    mainDiv.style.setProperty("margin-bottom", "0px", "important");
    mainDiv.style.setProperty("margin-right", __pageRightPaddingInPixels + "px", "important");
    mainDiv.style.setProperty("margin-left", __pageLeftPaddingInPixels + "px", "important");
    mainDiv.style.setProperty("padding", "0", "important");
    var width = document.body.clientWidth;
    var height = document.body.clientHeight - __pageTopPaddingInPixels - __pageBottomPaddingInPixels;
    document.body.style.setProperty("width", width + "px", "important");
    document.body.style.setProperty("height", height + "px", "important");
    document.body.style.setProperty("-webkit-column-width", width + "px", "important");
    document.body.style.setProperty("-webkit-column-gap", "0px", "important");
    onContentChanged();
}

function setTextAlign() {
    pRule.style.setProperty("text-align", __textAlign, "important");
    divRule.style.setProperty("text-align", __textAlign, "important");
    tdRule.style.setProperty("text-align", __textAlign, "important");
    dtRule.style.setProperty("text-align", __textAlign, "important");
    ddRule.style.setProperty("text-align", __textAlign, "important");
    brRule.style.setProperty("text-align", __textAlign, "important");
    liRule.style.setProperty("text-align", __textAlign, "important");
    onContentChanged();
}

function setFontSize() {
    document.body.style.setProperty("font-size", __fontSizeInPercents + "%", "important");
    onContentChanged();
}

function onContentChanged() {
    if (loaded) {
        __javaBridge.setTotalWidth(document.body.scrollWidth);
    }
}

/* ------------------------------------- Вспомогательные функции --------------------- */

function createStylesheet() {
    var style = document.createElement("style");
    document.head.appendChild(style);
    return document.styleSheets[document.styleSheets.length - 1];
}

function addCSSRule(stylesheet, ruleName) {
    var index = stylesheet.cssRules.length;
    stylesheet.insertRule(ruleName + " { }", index);
    return stylesheet.cssRules[index];
}