function elementText(element) {
    var OBJECT_REPLACEMENT_CHARACTER = "\uFFFC";
    var INLINE_TEXT_ELEMENTS = stringSet([
        "ABBR", "ACRONYM", "B", "BDI", "BDO", "BIG", "CITE", "CODE", "DEL", "DFN", "EM", "FONT", "I",
        "INS", "KBD", "MARK", "NOBR", "Q", "S", "SAMP", "SMALL", "SPAN", "STRIKE", "STRONG", "SUB",
        "SUP", "TIME", "TT", "U", "VAR", "WBR"
    ]);

    return {
        find: function(regexp, callback) {
            var strings = [];
            var elements = [];

            iterateChildren(element, processNode);

            function processNode(node) {
                if (node.nodeType == Node.TEXT_NODE) {
                    strings.push(node.nodeValue);
                    elements.push(node);
                } else if (node.nodeType == Node.ELEMENT_NODE) {
                    if (INLINE_TEXT_ELEMENTS.contains(node.nodeName)) {
                        iterateChildren(node, processNode);
                    } else {
                        strings.push(OBJECT_REPLACEMENT_CHARACTER);
                        elements.push(node);
                    }
                }
            }

            var text = strings.join("");
            var range = element.ownerDocument.createRange();

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
                        if (startIndex > endIndex || endIndex > this.fullString.length) {
                            throw "wrong index";
                        }
                        var start = indexToLocation(globalIndex + (startIndex !== undefined ? startIndex : 0));
                        var end = indexToLocation(globalIndex + (endIndex !== undefined ? endIndex : this.fullString.length));
                        range.setStart(start.element, start.offset);
                        range.setEnd(end.element, end.offset);
                        return range;
                    }
                });
            });

            function indexToLocation(index) {
                if (index > text.length) {
                    throw "wrong index";
                }
                var sum = 0;
                for (var i = 0; i < strings.length; i++) {
                    if (sum + strings[i].length > index) {
                        return {
                            element: elements[i],
                            offset: index - sum
                        }
                        break;
                    }
                    sum += strings[i].length;
                }
                return {
                    element: elements[elements.length - 1],
                    offset: elements[elements.length - 1].length
                };
            }
        }
    }
}