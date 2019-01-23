ace.define("ace/mode/sca_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text_highlight_rules"], function (require, exports, module) {
	"use strict";

	let oop = require("../lib/oop");
	let TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

	let ScaHighlightRules = function () {
		// let keywords = "abort|else|new|return|abs|elsif|not|reverse|abstract|end|null|accept|entry|select|" +
		// 	"access|exception|of|separate|aliased|exit|or|some|all|others|subtype|and|for|out|synchronized|" +
		// 	"array|function|overriding|at|tagged|generic|package|task|begin|goto|pragma|terminate|" +
		// 	"body|private|then|if|procedure|type|case|in|protected|constant|interface|until|" +
		// 	"|is|raise|use|declare|range|delay|limited|record|when|delta|loop|rem|while|digits|renames|with|do|mod|requeue|xor";
		//
		// let builtinConstants = (
		// 	"true|false|null"
		// );
		//
		// let builtinFunctions = (
		// 	"count|min|max|avg|sum|rank|now|coalesce|main"
		// );
		//
		// let keywordMapper = this.createKeywordMapper({
		// 	"support.function": builtinFunctions,
		// 	"keyword": keywords,
		// 	"constant.language": builtinConstants
		// }, "identifier", true);

		this.$rules = {
			"start": [{
				token: "comment",
				regex: /%.*$/
			}, {
				token: "string",
				regex: /'[^']*'|"[^"]*"/
			}, {
				token: "constant.numeric",
				regex: /\d+/
			}, {
				token: "support.function",
				regex: /mode|load|execute|import|open|write|close|break|reserve|as|or|not/i
			}, {
				token: "keyword.operator",
				regex: /[=/_>]/
			}, {
				token: "support.function",
				regex: /[*+?]/
			}, {
				token: "paren.lparen",
				regex: /[{(]/
			}, {
				token: "paren.rparen",
				regex: /[})]/
			}, {
				token: "text",
				regex: /\[/,
				next: "sqbracket"
			}, {
				token: ["variable", "variable", "constant.numeric"],
				regex: /(\$)([^\d]*)(\d+)/
			}],
			"sqbracket": [{
				token: "text",
				regex: /]/,
				next: "start"
			}, {
				token: "variable",
				regex: /[^\]]+/,
			}]
		};
	};

	oop.inherits(ScaHighlightRules, TextHighlightRules);

	exports.ScaHighlightRules = ScaHighlightRules;
});

ace.define("ace/mode/sca", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/mode/sca_highlight_rules", "ace/range"], function (require, exports, module) {
	"use strict";

	let oop = require("../lib/oop");
	let TextMode = require("./text").Mode;
	let ScaHighlightRules = require("./sca_highlight_rules").ScaHighlightRules;
	let Range = require("../range").Range;

	let Mode = function () {
		this.HighlightRules = ScaHighlightRules;
		this.$behaviour = this.$defaultBehaviour;
	};
	oop.inherits(Mode, TextMode);

	(function () {
		this.lineCommentStart = "%";
		this.blockComment = null;

		this.$id = "ace/mode/sca";
	}).call(Mode.prototype);

	exports.Mode = Mode;

});

(function () {
	ace.require(["ace/mode/sca"], function (m) {
		if (typeof module == "object" && typeof exports == "object" && module) {
			module.exports = m;
		}
	});
})();
