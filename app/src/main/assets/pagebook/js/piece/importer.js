"use strict";

define("piece/importer", function() {
    const StyleUtils = require("utils/style");
    const ContentInfo = require("piece/content-info");

    const getDefaultDisplay = StyleUtils.getDefaultDisplay.bind(StyleUtils);

    class LightStyle {
        constructor(elementStyle) {
            this.priorities = {};
        }

        setProperty(key, value, priority) {
            this[key] = value;
            this.priorities[key] = priority;
        }

        mergeWith(style) {
            for (let key in style.priorities) {
                const priority = style.priorities[key];
                const value = style[key];
                if (!this.priorities[key] || priority == "important") {
                    this[key] = value == "initial" ? "" : value;
                    this.priorities[key] = priority;
                }
            }
        }

        applyTo(element) {
            element.removeAttribute("style");
            for (let key in this.priorities) {
                element.style[key] = this[key];
            }
        }
    }

    // See http://www.idpf.org/accessibility/guidelines/content/style/reference.php
    const ALLOWED_CSS_PROPERTIES = new Set([
        // CSS 2.1
        "background", "background-attachment", "background-color", "background-image", "background-position",
        "background-repeat", "border", "border-top", "border-right", "border-bottom", "border-left",
        "border-collapse", "border-color", "border-top-color", "border-right-color", "border-bottom-color",
        "border-left-color", "border-spacing", "border-style", "border-top-style", "border-right-style",
        "border-bottom-style", "border-left-style", "border-width", "border-top-width", "border-right-width",
        "border-bottom-width", "border-left-width", "bottom", "left", "right", "top", "caption-side",
        "clear", "clip", "color", "content", "counter-increment", "counter-reset", "cursor",
        "display", "empty-cells", "float", "font", "font-family", "font-size", "font-style", "font-variant",
        "font-weight", "height", "letter-spacing", "line-height", "list-style", "list-style-image",
        "list-style-position", "list-style-type", "margin", "margin-top", "margin-right", "margin-bottom",
        "margin-left", "max-height", "max-width", "min-height", "min-width", "orphans", "widows",
        "outline", "outline-color", "outline-style", "outline-width", "overflow", "padding",
        "padding-top", "padding-right", "padding-bottom", "padding-left", "page-break-after",
        "page-break-before", "page-break-inside", "position", "quotes", "table-layout", "text-align",
        "text-decoration", "text-indent", "text-transform", "vertical-align",
        "visibility", "white-space", "width", "word-spacing", "z-index",

        // CSS Speech Module
        /* "cue", "pause", "rest", "speak", "speak-as", "voice-family", */

        // CSS Text Level 3
        "hyphens", "line-break", "text-align-last", "text-emphasis", "text-emphasis-color",
        "text-emphasis-style", "word-break",

        // CSS Writing Modes Module Level 3
        /* "text-combine-horizontal", "text-combine-mode", "text-orientation", */
        "writing-mode",

        // CSS Multi-column Layout Module
        /* "break-after", "break-before", "break-inside", "column-count", "column-fill", "column-gap",
        "column-rule", "column-rule-color", "column-rule-style", "column-rule-width", "column-span",
        "column-width", "columns", */
    ]);

    const ALLOWED_PSEUDO_ELEMENTS = new Set(["before", "after", "first-letter", "first-line"]);

    const mergedStyleSym = Symbol();

    return {
        importContent: function(doc) {
            overrideURLS(doc.body);
            const body = document.adoptNode(doc.body);

            processDoc();
            updateCSS();

            return body;

            function overrideURLS(root) {
                const walker = document.createTreeWalker(root, NodeFilter.SHOW_ELEMENT);
                while (walker.nextNode()) {
                    const element = walker.currentNode;
                    // for all URL attributes, see http://www.w3.org/html/wg/drafts/html/master/index.html#attributes-1 by searching "URL"
                    overrideURLAttribute(element, "action", ["form"]);
                    overrideURLAttribute(element, "cite", ["blockquote", "del", "ins", "q"]);
                    overrideURLAttribute(element, "data", ["object"]);
                    overrideURLAttribute(element, "formAction", ["button", "input"]);
                    overrideURLAttribute(element, "href", ["a", "area", "link", "base"]);
                    overrideURLAttribute(element, "icon", ["menuitem"]);
                    overrideURLAttribute(element, "poster", ["video"]);
                    overrideURLAttribute(element, "src", ["audio", "embed", "iframe", "img", "input", "script", "source", "track", "video"]);
                }
            }

            function overrideURLAttribute(element, attributeName, elementNames) {
                const nodeName = element.nodeName.toLowerCase();
                if (elementNames.indexOf(nodeName) >= 0) {
                    if (element.hasAttribute(attributeName)) {
                        // src, href, etc properties contains absolute urls, so just use property element[attributeName] for attribute value
                        element.setAttribute(attributeName, element[attributeName]);
                    }
                }
            }

            function processDoc() {
                const sheets = doc.styleSheets;
                for (let i = 0; i < sheets.length; i++) {
                    processSheet(sheets[i]);
                }

                const walker = document.createTreeWalker(body, NodeFilter.SHOW_ELEMENT);
                while (walker.nextNode()) {
                    const element = walker.currentNode;
                    const style = element.style;
                    if (style.length > 0) {
                        putStyle(element, toLightStyle(style));
                    }
                    computeStyle(element);
                }
            }

            function processSheet(sheet) {
                if (!sheet.disabled) {
                    processRules(sheet.cssRules);
                }
            }

            function processRules(rules) {
                for (let i = 0; i < rules.length; i++) {
                    const rule = rules[i];
                    switch (rule.type) {
                        case CSSRule.STYLE_RULE:
                            processStyleRule(rule);
                            break;
                        case CSSRule.IMPORT_RULE:
                            processSheet(rule.styleSheet);
                            break;
                    }
                }
            }

            function processStyleRule(rule) {
                if (rule.style.length > 0) {
                    let elements;
                    try {
                        elements = body.querySelectorAll(rule.selectorText);
                    } catch(e) {
                        elements = [];
                    }

                    if (elements.length > 0) {
                        const lightStyle = toLightStyle(rule.style);
                        for (let i = 0; i < elements.length; i++) {
                            putStyle(elements[i], lightStyle);
                        }
                    }
                }
            }

            function putStyle(element, style) {
                let mergedStyle = element[mergedStyleSym];
                if (!mergedStyle) {
                    mergedStyle = new LightStyle();
                    element[mergedStyleSym] = mergedStyle;
                }
                mergedStyle.mergeWith(style);
            }

            function toLightStyle(nativeStyle) {
                const lightStyle = new LightStyle();
                for (let i = 0; i < nativeStyle.length; i++) {
                    const key = nativeStyle[i];
                    const keyAlias = (key.startsWith("-epub-") ? key.substring("-epub-".length) : key).toLowerCase();

                    if (ALLOWED_CSS_PROPERTIES.has(keyAlias)) {
                        const value = nativeStyle.getPropertyValue(key);
                        const priority = nativeStyle.getPropertyPriority(key);
                        lightStyle.setProperty(keyAlias, value, priority);
                    }
                }
                return lightStyle;
            }

            function updateCSS() {
                const walker = document.createTreeWalker(body, NodeFilter.SHOW_ELEMENT);
                while (walker.nextNode()) {
                    const element = walker.currentNode;
                    element[mergedStyleSym].applyTo(element);
                    delete element[mergedStyleSym];
                }
            }

            /*
             * faster than window.getComputedStyle. contains all needed precomputed styles.
             * if you need some style property, set it in computeStyle function
             */
            function computeStyle(element) {
                const style = element[mergedStyleSym] || {};
                const parentStyle = element.parentNode && element.parentNode[ContentInfo.computedStyle];
                const nodeName = element.nodeName.toLowerCase();

                const computedStyle = {
                    display: style["display"],
                    float: style["float"],
                    position: style["position"],
                    left: style["left"],
                    top: style["top"],
                    transform: style["transform"],
                    whiteSpace: style["white-space"],
                    writingMode: style["writing-mode"],
                    columns: "auto auto",  // not supported yet
                };

                if (computedStyle.display == "inherit") computedStyle.display = parentStyle.display;
                if (computedStyle.float == "inherit") computedStyle.float = parentStyle.float;
                if (computedStyle.position == "inherit") computedStyle.position = parentStyle.position;
                if (computedStyle.left == "inherit") computedStyle.left = parentStyle.left;
                if (computedStyle.top == "inherit") computedStyle.top = parentStyle.top;
                if (computedStyle.transform == "inherit") computedStyle.transform = parentStyle.transform;
                if (computedStyle.whiteSpace == "inherit") computedStyle.whiteSpace = parentStyle.whiteSpace;
                if (computedStyle.writingMode == "inherit") computedStyle.writingMode = parentStyle.writingMode;

                if (!computedStyle.display) computedStyle.display = getDefaultDisplay(element);
                if (!computedStyle.float) computedStyle.float = "none";
                if (!computedStyle.position) computedStyle.position = "static";
                if (!computedStyle.left) computedStyle.left = "0px";
                if (!computedStyle.top) computedStyle.top = "0px";
                if (!computedStyle.transform) computedStyle.transform = "none";
                if (!computedStyle.whiteSpace) computedStyle.whiteSpace = "normal";
                if (!computedStyle.writingMode) computedStyle.writingMode = "lr-tb";

                if (nodeName == "img") {
                    const align = element.getAttribute("align");
                    if (align == "left" || align == "right") {
                        computedStyle.float = align;
                    }
                }

                if (computedStyle.float == "left" || computedStyle.float == "right") {
                    computedStyle.display = "block";
                }

                element[ContentInfo.computedStyle] = computedStyle;
            }
        },
    };
});
