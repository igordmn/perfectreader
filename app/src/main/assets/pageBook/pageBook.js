window.onload = function() {
    // задержка обновления соседних фреймов (содержащих предыдущий и следующих сегменты)
    // необходима для оптимизации "непрерывного конфигурирования"
    // (например, когда пользователь изменяет размер шрифта, и configure вызывается каждые 100 мс)
    var NEIGHBOR_FRAME_REFRESH_DELAY_MILLIS = 1000;

    var segmentUrls_ = [];
    var segmentCount_ = 0;
    var client_;

    var segmentIndex_;
    var segmentPercent_;

    var bookConfig_ = {
        pageWidth: 100,
        pageHeight: 200,

        paddingTop: 10,
        paddingRight: 10,
        paddingBottom: 10,
        paddingLeft: 10,

        textAlign: "justify",
        fontSizePercents: 100,
        lineHeightPercents: 100,
        hangingPunctuation: true,
        hyphenation: true,
    };

    var currentFrame = document.getElementById("firstFrame");
    var nextFrame = document.getElementById("secondFrame");
    var previewFrame = document.getElementById("thirdFrame");

    window.reader = {
        setClient: function(client) {
            client_ = client;
        },

        load: function(segmentUrls) {
            segmentUrls_ = segmentUrls;
            segmentCount_ = segmentUrls.length;
            segmentIndex_ = undefined;
            segmentPercent_ = undefined;
        },

        configure: function(config) {
            for (var i in config) {
                bookConfig_[i] = config[i];
            }
            refreshSegmentNow(currentFrame);
            refreshSegmentDelayed(nextFrame, NEIGHBOR_FRAME_REFRESH_DELAY_MILLIS);
            refreshSegmentDelayed(previewFrame, NEIGHBOR_FRAME_REFRESH_DELAY_MILLIS);
        },

        goLocation: function(segmentIndex, segmentPercent) { var oldSegmentIndex = segmentIndex_;
            segmentIndex_ = segmentIndex;
            segmentPercent_ = segmentPercent;

            var currentUrl = segmentUrls_[segmentIndex_];
            var nextUrl = segmentIndex_ < segmentCount_ - 1 ? segmentUrls_[segmentIndex_ + 1] : "about:blank";
            var previewUrl = segmentIndex_ > 0 ? segmentUrls_[segmentIndex_ - 1] : "about:blank";
            if (segmentIndex_ == oldSegmentIndex + 1) {
                var oldCurrentFrame = currentFrame;
                currentFrame = nextFrame;
                nextFrame = previewFrame;
                previewFrame = oldCurrentFrame;
                loadSegment(nextFrame, nextUrl);
            } else if (segmentIndex_ == oldSegmentIndex - 1) {
                var oldCurrentFrame = currentFrame;
                currentFrame = previewFrame;
                previewFrame = nextFrame;
                nextFrame = oldCurrentFrame;
                loadSegment(previewFrame, previewUrl);
            } else if (segmentIndex_ != oldSegmentIndex) {
                loadSegment(currentFrame, currentUrl);
                loadSegment(nextFrame, nextUrl);
                loadSegment(previewFrame, previewUrl);
            }
            refreshViewport();
        }
    };

    function refreshViewport() {
        notifyPageCounts();
        var currentStyle = currentFrame.style;
        var nextStyle = nextFrame.style;
        var previewStyle = previewFrame.style;
        currentStyle.width = bookConfig_.pageWidth + "px";
        currentStyle.height = bookConfig_.pageHeight + "px";
        currentStyle.left = 0 + "px";
        nextStyle.width = bookConfig_.pageWidth + "px";
        nextStyle.height = bookConfig_.pageHeight + "px";
        nextStyle.left = -bookConfig_.pageWidth + "px";
        previewStyle.width = bookConfig_.pageWidth + "px";
        previewStyle.height = bookConfig_.pageHeight + "px";
        previewStyle.left = -bookConfig_.pageWidth + "px";
        currentFrame.reader && currentFrame.reader.scrollToPercent(segmentPercent_);
    }

    function notifyPageCounts() {
        var previewPageCounts = previewFrame.reader ? previewFrame.reader.pageCount() : null;
        var currentPageCounts = currentFrame.reader ? currentFrame.reader.pageCount() : null;
        var nextPageCounts = nextFrame.reader ? nextFrame.reader.pageCount() : null;
        client_.notifyPageCounts(previewPageCounts, currentPageCounts, nextPageCounts);
    }

    function refreshSegmentNow(frame) {
        clearTimeout(frame.refreshId);
        frame.reader && frame.reader.refreshConfig();
    }

    function refreshSegmentDelayed(frame, delay) {
        frame.style.visibility = "hidden";
        if (frame.reader) {
            var reader = frame.reader;
            frame.reader = null;
            clearTimeout(frame.refreshId);
            frame.refreshId = setTimeout(function() {
                frame.reader = reader;
                frame.reader.refreshConfig();
                frame.style.visibility = "";
            }, delay);
        }
    }

    function loadSegment(frame, segmentUrl) {
        frame.style.visibility = "hidden";
        frame.reader = null;
        clearTimeout(frame.refreshId);
        frame.onload = function() {
            frame.contentWindow.document.fonts.ready.then(function() {
                initFrame(frame);
                afterFrameLoad(frame);
                frame.style.visibility = "";
            });
        }
        frame.src = segmentUrl;
    }

    function afterFrameLoad(frame) {
        frame.reader.onPageCountChange = function() {
            refreshViewport();
        }
        refreshViewport();
    }

    function initFrame(frame) {
        var win = frame.contentWindow;
        var doc = win.document;

        win.addEventListener("click", function(event) {
            if (isNotInteractiveElement(event.target)) {
                event.stopPropagation();
                event.preventDefault();
                client_.handleTap();
                return false;
            } else {
                return true;
            }

            function isNotInteractiveElement(element) {
                for (var it = element; it != null; it = it.parentNode) {
                    if (["BUTTON", "INPUT", "SELECT", "TEXTAREA"].indexOf(it.nodeName) >= 0) {
                        return false;
                    } else if (it.nodeName == "A" && it.href != "") {
                        return false;
                    }
                }
                return true;
            }
        }, true);

        var stylesheet = createStylesheet(doc);
        var styles = {
            p: addCSSStyle(stylesheet, "p"),
            div: addCSSStyle(stylesheet, "div"),
            td: addCSSStyle(stylesheet, "td"),
            dt: addCSSStyle(stylesheet, "dt"),
            dd: addCSSStyle(stylesheet, "dd"),
            br: addCSSStyle(stylesheet, "br"),
            li: addCSSStyle(stylesheet, "li"),
            fixSpace: addCSSStyle(stylesheet, ".fixSpace", "display: inline-block; width: 0.25em; text-indent: 0; padding: 0; margin: 0")
        };

        var elements = {
            pageDiv: doc.createElement("div"),
            content: doc.createElement("div"),
            touchLayer: doc.createElement("div")
        };

        while (doc.body.firstChild) {
            elements.content.appendChild(doc.body.firstChild);
        }
        elements.pageDiv.appendChild(elements.content);

        // touchLayer necessary to be possible clicking on the end of the segment
        elements.touchLayer.style.width = "100%";
        elements.touchLayer.style.height = "100%";
        elements.touchLayer.style.position = "absolute";
        elements.touchLayer.style.left = "0px";
        elements.touchLayer.style.top = "0px";
        elements.touchLayer.style.zIndex = "-1";
        doc.body.appendChild(elements.touchLayer);

        doc.body.appendChild(elements.pageDiv);

        var frameReader = frame.reader = {
            onPageCountChange: undefined,

            pageCount: function() {
                return Math.round(doc.body.scrollWidth / bookConfig_.pageWidth);
            },

            refreshConfig: function() {
                refreshConfig();
            },

            scrollToPercent: function(percent) {
                var page = parseInt(client_.percentToPage(this.pageCount(), percent));
                currentFrame.contentWindow.scrollTo(page * bookConfig_.pageWidth, 0);
                elements.touchLayer.style.left = page * bookConfig_.pageWidth + "px";
            }
        };

        refreshConfig();

        doc.addEventListener("typoContentSizeChange", function(event) {
            frameReader.onPageCountChange();
        }, false);

        function refreshConfig() {
            refreshPageConfiguration();
            refreshTextAlign();
            refreshFontSize();
            refreshLineHeight();
            refreshHangingPunctuation();
            refreshHyphenation();
        }

        function refreshPageConfiguration() {
            elements.pageDiv.style.setProperty("margin-top", bookConfig_.paddingTop + "px", "important");
            elements.pageDiv.style.setProperty("margin-bottom", bookConfig_.paddingBottom + "px", "important");
            elements.pageDiv.style.setProperty("margin-right", "0px", "important");
            elements.pageDiv.style.setProperty("margin-left", "0px", "important");
            elements.pageDiv.style.setProperty("padding", "0px", "important");

            elements.content.style.setProperty("margin-top", "0px", "important");
            elements.content.style.setProperty("margin-bottom", "0px", "important");
            elements.content.style.setProperty("margin-right", bookConfig_.paddingRight + "px", "important");
            elements.content.style.setProperty("margin-left", bookConfig_.paddingLeft + "px", "important");
            elements.content.style.setProperty("padding", "0px", "important");

            var width = bookConfig_.pageWidth;
            var height = bookConfig_.pageHeight - bookConfig_.paddingTop - bookConfig_.paddingBottom;
            elements.pageDiv.style.setProperty("width", width + "px", "important");
            elements.pageDiv.style.setProperty("height", height + "px", "important");
            elements.pageDiv.style.setProperty("-webkit-column-width", width + "px", "important");
            elements.pageDiv.style.setProperty("-webkit-column-gap", "0px", "important");

            doc.body.style.setProperty("margin", "0px", "important");
            doc.body.style.setProperty("padding", "0px", "important");
        }

        function refreshTextAlign() {
            styles.p.setProperty("text-align", bookConfig_.textAlign, "important");
            styles.div.setProperty("text-align", bookConfig_.textAlign, "important");
            styles.td.setProperty("text-align", bookConfig_.textAlign, "important");
            styles.dt.setProperty("text-align", bookConfig_.textAlign, "important");
            styles.dd.setProperty("text-align", bookConfig_.textAlign, "important");
            styles.br.setProperty("text-align", bookConfig_.textAlign, "important");
            styles.li.setProperty("text-align", bookConfig_.textAlign, "important");
        }

        function refreshFontSize() {
            doc.body.style.setProperty("font-size", bookConfig_.fontSizePercents + "%", "important");
        }

        function refreshLineHeight() {
            styles.p.setProperty("line-height", bookConfig_.lineHeightPercents / 100, "important");
            styles.div.setProperty("line-height", bookConfig_.lineHeightPercents / 100, "important");
            styles.td.setProperty("line-height", bookConfig_.lineHeightPercents / 100, "important");
            styles.dt.setProperty("line-height", bookConfig_.lineHeightPercents / 100, "important");
            styles.dd.setProperty("line-height", bookConfig_.lineHeightPercents / 100, "important");
            styles.br.setProperty("line-height", bookConfig_.lineHeightPercents / 100, "important");
            styles.li.setProperty("line-height", bookConfig_.lineHeightPercents / 100, "important");
        }

        function refreshHangingPunctuation() {
            styles.p.setProperty("-typo-hanging-punctuation", bookConfig_.hangingPunctuation ? "on" : "off", "important");
            styles.div.setProperty("line-height", bookConfig_.hangingPunctuation ? "on" : "off", "important");
            styles.td.setProperty("line-height", bookConfig_.hangingPunctuation ? "on" : "off", "important");
            styles.dt.setProperty("line-height", bookConfig_.hangingPunctuation ? "on" : "off", "important");
            styles.dd.setProperty("line-height", bookConfig_.hangingPunctuation ? "on" : "off", "important");
            styles.br.setProperty("line-height", bookConfig_.hangingPunctuation ? "on" : "off", "important");
            styles.li.setProperty("line-height", bookConfig_.hangingPunctuation ? "on" : "off", "important");
        }

        function refreshHyphenation() {
            styles.p.setProperty("-typo-hyphens", bookConfig_.hyphenation ? "on" : "off", "important");
            styles.div.setProperty("-typo-hyphens", bookConfig_.hyphenation ? "on" : "off", "important");
            styles.td.setProperty("-typo-hyphens", bookConfig_.hyphenation ? "on" : "off", "important");
            styles.dt.setProperty("-typo-hyphens", bookConfig_.hyphenation ? "on" : "off", "important");
            styles.dd.setProperty("-typo-hyphens", bookConfig_.hyphenation ? "on" : "off", "important");
            styles.br.setProperty("-typo-hyphens", bookConfig_.hyphenation ? "on" : "off", "important");
            styles.li.setProperty("-typo-hyphens", bookConfig_.hyphenation ? "on" : "off", "important");
        }
    }
};
