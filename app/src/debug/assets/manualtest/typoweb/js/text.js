var OBJECT_REPLACEMENT_CHARACTER = "\uFFFC";
var INLINE_TEXT_ELEMENTS = stringSet([
    "ABBR", "ACRONYM", "B", "BDI", "BDO", "BIG", "CITE", "CODE", "DEL", "DFN", "EM", "FONT", "I",
    "INS", "KBD", "MARK", "NOBR", "Q", "S", "SAMP", "SMALL", "SPAN", "STRIKE", "STRONG", "SUB",
    "SUP", "TIME", "TT", "U", "VAR", "WBR"
]);

function isInlineElement(element) {
    return INLINE_TEXT_ELEMENTS.contains(element.nodeName) ||
        element.nodeName == "A" && element.src == undefined;
}

function elementText(element) {
    var strings = [];
    var elements = [];

    iterateChildren(element, processNode);

    function processNode(node) {
        if (node.nodeType == Node.TEXT_NODE) {
            strings.push(node.nodeValue);
            elements.push(node);
        } else if (node.nodeType == Node.ELEMENT_NODE) {
            if (isInlineElement(node)) {
                iterateChildren(node, processNode);
            } else {
                strings.push(OBJECT_REPLACEMENT_CHARACTER);
                elements.push(node);
            }
        }
    }

    var text = strings.join("");

    var range = element.ownerDocument.createRange();

    return {
        asString: function() {
            return text;
        },

        range: function(startIndex, endIndex) {
            var start = indexToLocation(startIndex);
            var end = indexToLocation(endIndex, startIndex);
            start.isObject ? range.setStartAfter(start.element) : range.setStart(start.element, start.offset);
            end.isObject ? range.setEndBefore(end.element) : range.setEnd(end.element, end.offset);
            return range;
        },

        find: function(regexp, callback) {
            var results = [];
            var result;
            while (result = regexp.exec(text)) {
                results.push(result);
                if (!regexp.global) {
                    break;
                }
            }
            results.reverse().forEach(function(result) {
                var globalIndex = result.index;
                callback({
                    fullString: result[0],
                    subStrings: result.slice(1),
                    range: function(startIndex, endIndex) {
                        if (startIndex === undefined) { startIndex = 0; }
                        if (endIndex === undefined) { endIndex = this.fullString.length; }
                        if (startIndex > endIndex || startIndex < 0 || endIndex > this.fullString.length) {
                            throw "wrong index";
                        }
                        var start = indexToLocation(globalIndex + startIndex);
                        var end = indexToLocation(globalIndex + endIndex, globalIndex + startIndex);
                        start.isObject ? range.setAfter(start.element) : range.setStart(start.element, start.offset);
                        start.isObject ? range.setBefore(end.element) : range.setEnd(end.element, end.offset);
                        return range;
                    }
                });
            });
        }
    }

    // todo переделать
    // templates:
    // <div></div>
    // <div><span></span></div>
    // <div><span>a</span></div>
    // <div>d<span>a</span></div>
    // <div>d<span>a</span>c</div>
    // <div>d<span></span>c</div>
    // <div>d<span><span>a</span></span>c</div>
    // <div>d<span>a<span>a</span></span>c</div>
    // <div>d<span><span></span><span>a</span></span>c</div>
    // <div>d<span><br><span>a</span></span>c</div>
    // <div>d<span><br><br><span>a</span></span>c</div>
    // <div>d<span><br><br><span><br></span></span>c</div>
    function indexToLocation(index, startIndex) {
        if (index < 0 || index > text.length) {
            throw "wrong index";
        }
        var begin = 0;
        for (var i = 0; i < strings.length; i++) {
            var end = begin + strings[i].length ;
            if ((startIndex === undefined && end > index) || (startIndex !== undefined && end >= index && end > startIndex)) {
                return {
                    element: elements[i],
                    offset: index - begin,
                    isObject: strings[i] == OBJECT_REPLACEMENT_CHARACTER
                };
            }
            begin = end;
        }
        return {
            element: elements[elements.length - 1],
            offset: strings[elements.length - 1].length,
            isObject: strings[elements.length - 1] == OBJECT_REPLACEMENT_CHARACTER
        };
    }
}