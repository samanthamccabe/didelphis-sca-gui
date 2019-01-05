define("ace/mode/didelphislog_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
	"use strict";

	var oop = require("../lib/oop");
	var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

	var DidelphisLogHighlightRules = function() {

		this.$rules = {
			"start": [{
				token: "comment",
				regex: /%.*$/
			},{
				token: "invalid",
				regex: /\[ERROR\]/
			},{
				token: ["text", "support", "text", "constant.numeric"],
				regex: /(\[INFO\]\s+)(.+\s+)((?:compiled|ran) successfully in\s+)(\d+(?:\.\d+)?)/
			},{
				token: ["text", "constant.numeric", "support", "text"],
				regex: /(line:\s+)(\d+)(\s+[^\|]+)(|)/
			}]
		};
	};

	oop.inherits(DidelphisLogHighlightRules, TextHighlightRules);

	exports.DidelphisLogHighlightRules = DidelphisLogHighlightRules;
});

define("ace/mode/didelphislog",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/didelphislog_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var DidelphisLogHighlightRules = require("./didelphislog_highlight_rules").DidelphisLogHighlightRules;

var Mode = function() {
    this.HighlightRules = DidelphisLogHighlightRules;
};
oop.inherits(Mode, TextMode);

(function() {
    this.lineCommentStart = "%";
    this.blockComment = null;
    this.$id = "ace/mode/didelphislog";
}).call(Mode.prototype);

exports.Mode = Mode;
});
