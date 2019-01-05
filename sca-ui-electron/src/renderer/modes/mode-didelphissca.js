ace.define("ace/mode/didelphissca_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text_highlight_rules"], function (require, exports, module) {
	"use strict";

	var oop = require("../lib/oop");
	var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

	var DidelphisHighlightRules = function () {

		this.$rules = {
			"start": [{
				token: "comment",
				regex: /%.*$/
			}, {
				token: "string",
				regex: /'[^']*'|"[^"]*"/
			}, {
				token: "constant.numeric",
				regex: /\d/
			}, {
				token: "support.function",
				regex: /mode|load|execute|import|open|write|close|break|reserve|as|or|not/
			}, {
				token: "support.function",
				regex: /MODE|LOAD|EXECUTE|IMPORT|OPEN|WRITE|CLOSE|BREAK|RESERVE|AS|OR|NOT/i
			}, {
				token: "keyword.operator",
				regex: />|=|\/|_/
			}, {
				token: "support.function",
				regex: /\*|\+|\?/
			}, {
				token: "paren.lparen",
				regex: /{|\(/
			}, {
				token: "paren.rparen",
				regex: /\}|\)/
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
				regex: /\]/,
				next: "start"
			}, {
				token: "variable",
				regex: /[^\]]+/,
			}]
		};
	};

	oop.inherits(DidelphisHighlightRules, TextHighlightRules);

	exports.DidelphisHighlightRules = DidelphisHighlightRules;
});

ace.define("ace/mode/didelphissca", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/mode/didelphissca_highlight_rules"], function (require, exports, module) {
	"use strict";

	var oop = require("../lib/oop");
	var TextMode = require("./text").Mode;
	var DidelphisHighlightRules = require("./didelphissca_highlight_rules").DidelphisHighlightRules;

	var Mode = function () {
		this.HighlightRules = DidelphisHighlightRules;
	};
	oop.inherits(Mode, TextMode);

	(function () {
		this.lineCommentStart = "%";
		this.blockComment = null;
		this.$id = "ace/mode/didelphissca";
	}).call(Mode.prototype);

	module.exports = Mode;
});
