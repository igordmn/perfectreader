document.registerElement("ext-pageview", {
    prototype: Object.create(new function() {
        var pageMargin_ = {
            left: 0,
            right: 0,
            top: 0,
            bottom: 0
        };
        var pageSize_ = {
            width: 320,
            height: 240
        };

        var mainDiv_;
        var elements_ = [];

        var globalOffset_ = 0;
        var position_ = null;

        return {
            __proto__: HTMLElement.prototype,

            createdCallback: function() {
                var shadow = this.createShadowRoot();
                mainDiv_ = this.ownerDocument.createElement("div");
                contentDiv_ = this.ownerDocument.createElement("div");
                shadow.appendChild(mainDiv_);
            },

            get pageMargin: function() {
                return pageMargin_;
            },

            set pageMargin: function(value) {
                pageMargin_ = value;
            },

            get pageSize: function() {
                return pageSize_;
            },

            set pageSize: function(value) {
                pageSize_ = value;
            },

            get elements: function() {
                return elements_;
            },

            get position: function() {
                return position_;
            },

            set position: function(value) {
                position_ = value;
            },

            offsetTop: function(element) {
                return element.offsetTop - globalOffset_;
            },

            offsetBottom: function(element) {
                return element.offsetBottom - globalOffset_;
            },

            addFirst: function(element) {
                elements_.unshift(element);
                mainDiv_.insertBefore(element, mainDiv_.firstChild);
                // todo modify globalOffset_
                layout();
            },

            addLast: function(element) {
                elements_.push(element);
                mainDiv_.appendChild(element);
                layout();
            },

            removeFirst: function() {
                if (elements_.length > 0) {
                    var element = elements_.shift();
                    mainDiv_.removeChild(element);
                    layout();
                }
            },

            removeLast: function() {
                if (elements_.length > 0) {
                    var element = elements_.pop();
                    mainDiv_.removeChild(element);
                    layout();
                }
            },

            shift: function(offset) {
                globalOffset_ + = offset;
                layout();
            },
        };
    });
});
