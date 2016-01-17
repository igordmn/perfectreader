"use strict";

define("utils/style", function() {
    /* See https:// developer.mozilla.org/en-US/docs/Web/HTML/Element and doc-files/showElements.html */

    const elementToDisplay = new Map();

    // inline
    elementToDisplay.set("a", "inline");
    elementToDisplay.set("abbr", "inline");
    elementToDisplay.set("acronym", "inline");
    elementToDisplay.set("applet", "inline");
    elementToDisplay.set("area", "inline");
    elementToDisplay.set("audio", "inline");
    elementToDisplay.set("b", "inline");
    elementToDisplay.set("base", "inline");
    elementToDisplay.set("basefont", "inline");
    elementToDisplay.set("bdi", "inline");
    elementToDisplay.set("bdo", "inline");
    elementToDisplay.set("bgsound", "inline");
    elementToDisplay.set("big", "inline");
    elementToDisplay.set("blink", "inline");
    elementToDisplay.set("br", "inline");
    elementToDisplay.set("canvas", "inline");
    elementToDisplay.set("cite", "inline");
    elementToDisplay.set("code", "inline");
    elementToDisplay.set("content", "inline");
    elementToDisplay.set("data", "inline");
    elementToDisplay.set("decorator", "inline");
    elementToDisplay.set("del", "inline");
    elementToDisplay.set("dfn", "inline");
    elementToDisplay.set("element", "inline");
    elementToDisplay.set("em", "inline");
    elementToDisplay.set("embed", "inline");
    elementToDisplay.set("font", "inline");
    elementToDisplay.set("i", "inline");
    elementToDisplay.set("iframe", "inline");
    elementToDisplay.set("img", "inline");
    elementToDisplay.set("ins", "inline");
    elementToDisplay.set("isindex", "inline");
    elementToDisplay.set("kbd", "inline");
    elementToDisplay.set("label", "inline");
    elementToDisplay.set("map", "inline");
    elementToDisplay.set("mark", "inline");
    elementToDisplay.set("menuitem", "inline");
    elementToDisplay.set("nobr", "inline");
    elementToDisplay.set("noscript", "inline");
    elementToDisplay.set("object", "inline");
    elementToDisplay.set("output", "inline");
    elementToDisplay.set("picture", "inline");
    elementToDisplay.set("q", "inline");
    elementToDisplay.set("rp", "inline");
    elementToDisplay.set("rt", "inline");
    elementToDisplay.set("ruby", "inline");
    elementToDisplay.set("s", "inline");
    elementToDisplay.set("samp", "inline");
    elementToDisplay.set("shadow", "inline");
    elementToDisplay.set("small", "inline");
    elementToDisplay.set("source", "inline");
    elementToDisplay.set("spacer", "inline");
    elementToDisplay.set("span", "inline");
    elementToDisplay.set("strike", "inline");
    elementToDisplay.set("strong", "inline");
    elementToDisplay.set("sub", "inline");
    elementToDisplay.set("sup", "inline");
    elementToDisplay.set("time", "inline");
    elementToDisplay.set("track", "inline");
    elementToDisplay.set("tt", "inline");
    elementToDisplay.set("u", "inline");
    elementToDisplay.set("var", "inline");
    elementToDisplay.set("video", "inline");
    elementToDisplay.set("wbr", "inline");
    elementToDisplay.set("svg", "inline");

    // block
    elementToDisplay.set("address", "block");
    elementToDisplay.set("article", "block");
    elementToDisplay.set("aside", "block");
    elementToDisplay.set("blockquote", "block");
    elementToDisplay.set("body", "block");
    elementToDisplay.set("center", "block");
    elementToDisplay.set("dd", "block");
    elementToDisplay.set("details", "block");
    elementToDisplay.set("dir", "block");
    elementToDisplay.set("div", "block");
    elementToDisplay.set("dl", "block");
    elementToDisplay.set("dt", "block");
    elementToDisplay.set("fieldset", "block");
    elementToDisplay.set("figcaption", "block");
    elementToDisplay.set("figure", "block");
    elementToDisplay.set("footer", "block");
    elementToDisplay.set("form", "block");
    elementToDisplay.set("frame", "block");
    elementToDisplay.set("frameset", "block");
    elementToDisplay.set("h1", "block");
    elementToDisplay.set("h2", "block");
    elementToDisplay.set("h3", "block");
    elementToDisplay.set("h4", "block");
    elementToDisplay.set("h5", "block");
    elementToDisplay.set("h6", "block");
    elementToDisplay.set("header", "block");
    elementToDisplay.set("hgroup", "block");
    elementToDisplay.set("hr", "block");
    elementToDisplay.set("html", "block");
    elementToDisplay.set("legend", "block");
    elementToDisplay.set("listing", "block");
    elementToDisplay.set("main", "block");
    elementToDisplay.set("menu", "block");
    elementToDisplay.set("nav", "block");
    elementToDisplay.set("ol", "block");
    elementToDisplay.set("optgroup", "block");
    elementToDisplay.set("option", "block");
    elementToDisplay.set("p", "block");
    elementToDisplay.set("plaintext", "block");
    elementToDisplay.set("pre", "block");
    elementToDisplay.set("section", "block");
    elementToDisplay.set("summary", "block");
    elementToDisplay.set("ul", "block");
    elementToDisplay.set("xmp", "block");

    // inline-block
    elementToDisplay.set("button", "inline-block");
    elementToDisplay.set("input", "inline-block");
    elementToDisplay.set("keygen", "inline-block");
    elementToDisplay.set("marquee", "inline-block");
    elementToDisplay.set("meter", "inline-block");
    elementToDisplay.set("progress", "inline-block");
    elementToDisplay.set("select", "inline-block");
    elementToDisplay.set("textarea", "inline-block");

    // table-caption
    elementToDisplay.set("caption", "table-caption");

    // table-column
    elementToDisplay.set("col", "table-column");

    // table-column-group
    elementToDisplay.set("colgroup", "table-column-group");

    // none
    elementToDisplay.set("datalist", "none");
    elementToDisplay.set("dialog", "none");
    elementToDisplay.set("head", "none");
    elementToDisplay.set("link", "none");
    elementToDisplay.set("meta", "none");
    elementToDisplay.set("noframes", "none");
    elementToDisplay.set("param", "none");
    elementToDisplay.set("script", "none");
    elementToDisplay.set("style", "none");
    elementToDisplay.set("template", "none");
    elementToDisplay.set("title", "none");

    // list-item
    elementToDisplay.set("li", "list-item");

    // table
    elementToDisplay.set("table", "table");

    // table-row-group
    elementToDisplay.set("tbody", "table-row-group");

    // table-cell
    elementToDisplay.set("td", "table-cell");
    elementToDisplay.set("th", "table-cell");

    // table-footer-group
    elementToDisplay.set("tfoot", "table-footer-group");

    // table-header-group
    elementToDisplay.set("thead", "table-header-group");

    // table-row
    elementToDisplay.set("tr", "table-row");

    return {
        getDefaultDisplay: function(element) {
            const nodeName = element.nodeName.toLowerCase();
            return elementToDisplay.get(nodeName) || "block";
        },
    };
});
