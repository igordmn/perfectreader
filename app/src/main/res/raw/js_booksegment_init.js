/* ------------------------------------- До загрузки --------------------------------- */

initPagination();
setFontSize(__javaFontSize);

/* ------------------------------------- После загрузки ------------------------------*/

window.onload = function(e){
    __javaBridge.setScreenWidth(document.body.clientWidth);
    __javaBridge.setTotalWidth(document.body.scrollWidth);
    __javaBridge.finishLoad();
}

/* ------------------------------------- Функции ------------------------------------- */

function setFontSize(value) {
    document.body.style.setProperty("font-size", value, "important");
    __javaBridge.setTotalWidth(document.body.scrollWidth);
}

function initPagination() {
    document.body.style.setProperty("padding", "0", "important");
    document.body.style.setProperty("margin", "0", "important");
    document.body.style.setProperty("width", document.documentElement.clientWidth + "px", "important");
    document.body.style.setProperty("height", document.documentElement.clientHeight + "px", "important");
    document.body.style.setProperty("-webkit-column-width", document.documentElement.clientWidth + "px", "important");
    document.body.style.setProperty("-webkit-column-gap", "0", "important");
}