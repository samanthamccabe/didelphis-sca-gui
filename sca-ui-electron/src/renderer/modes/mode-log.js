ace.define("ace/mode/log_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text_highlight_rules"], function (require, exports, module) {
	"use strict";

	let oop = require("../lib/oop");
	let TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

	let LogHighlightRules = function () {
		this.$rules = {
			"start": [{
				token: "invalid",
				regex: /\[ERROR\]/
			}, {
				token: ["text", "support", "text", "constant.numeric"],
				regex: /(\[INFO\]\s+)(.+\s+)((?:compiled|ran) successfully in\s+)(\d+(?:\.\d+)?)/
			}, {
				token: ["text", "constant.numeric", "support", "text"],
				regex: /(line:\s+)(\d+)(\s+[^|]+)(\|)/
			}]
		};
	};

	oop.inherits(LogHighlightRules, TextHighlightRules);

	exports.LogHighlightRules = LogHighlightRules;
});

ace.define("ace/mode/log", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/mode/log_highlight_rules", "ace/range"], function (require, exports, module) {
	"use strict";

	let oop = require("../lib/oop");
	let TextMode = require("./text").Mode;
	let LogHighlightRules = require("./log_highlight_rules").LogHighlightRules;
	let Range = require("../range").Range;

	let Mode = function () {
		this.HighlightRules = LogHighlightRules;
		this.$behaviour = this.$defaultBehaviour;
	};
	oop.inherits(Mode, TextMode);

	(function () {
		this.lineCommentStart = "%";
		this.blockComment = null;
		this.$id = "ace/mode/log";
	}).call(Mode.prototype);

	exports.Mode = Mode;

});

(function () {
	ace.require(["ace/mode/log"], function (m) {
		if (typeof module == "object" && typeof exports == "object" && module) {
			module.exports = m;
		}
	});
})();
