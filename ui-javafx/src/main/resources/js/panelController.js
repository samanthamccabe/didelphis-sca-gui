/* jshint globalstrict: true*/
/* globals $, GoldenLayout, CodeEditor, ErrorLogger, LexiconViewer, rgbDeparse, rulesForCssText, parseColor, minus, plus, times */
"use strict";

window.onerror = function (errorMsg, url, lineNumber, column, errorObj) {
	alert("[ERROR] " + errorMsg + " Script: " + url + " Line: " + lineNumber + " Column: " + column + " StackTrace: " +  errorObj);
};

// Managed componentState
var codeEditors = {};
var lexiconViewers = {};
var errorLogger;

function contains(string, testsArr) {
	for (var i = 0; i < testsArr.length; i++) {
		if (string.indexOf(testsArr[i]) >= 0) {
			return true;
		}
	}
	return false;
}

function setCSS (theme) {

	$("link[href*='goldenlayout-theme']").attr("href", "css/golden/goldenlayout-theme-" + theme + ".css");
	$("link[href*='didelphis-theme']").attr("href", "css/didelphis-theme-" + theme + ".css");

	// Dynamic theme generation --- deferred
	var cssRules = rulesForCssText(codeEditors.main.editor.renderer.theme.cssText);
	
	var activeLine;
	var bg, // Background
	    tx, // Foreground
	    st, // String
	    kw, // Keyword
	    vr, // Variable
	    nm, // Number
	    fn; // Function

	for (var key in cssRules) {
		if (cssRules.hasOwnProperty(key)) {
			var rule = cssRules[key];
			var selectorText = rule.selectorText;

			if (!rule.style) {
				continue;
			} // get out

			var background = rule.style["background-color"];
			var textColor = rule.style.color;
			if (selectorText && (background || textColor)) {
				// Background color
				if (/^\.ace-[a-z_\-]+$/.test(selectorText)) {
					bg = parseColor(background);
					tx = parseColor(textColor);
				} else if (!activeLine && contains(selectorText, [".ace_active-line"])) {
					activeLine = parseColor(background);
				} else if (!fn && contains(selectorText, [".ace_function"])) {
					fn = parseColor(textColor);
				} else if (!st && contains(selectorText, [".ace_string"])) {
					st = parseColor(textColor);
				} else if (!nm && contains(selectorText, [".ace_numeric"])) {
					nm = parseColor(textColor);
				} else if (!vr && contains(selectorText, [".ace_variable", ".ace_language"])) {
					vr = parseColor(textColor);
				} else if (!kw && contains(selectorText, [".ace_keyword", ".ace_type"])) {
					kw = parseColor(textColor);
				}
			}
		}
	}

	var dif = minus(bg,tx);
	var p75 = minus(bg, times(dif, 0.75));
	var p50 = minus(bg, times(dif, 0.50));
	var p25 = minus(bg, times(dif, 0.25));
	var p10 = minus(bg, times(dif, 0.10));
	var p05 = minus(bg, times(dif, 0.05));

	var style = generateStyle({
		"body": {
			"background-color": rgbDeparse(bg),
			"color":            rgbDeparse(tx)
		},
		"table.dataTable tbody tr": {
			"background-color": rgbDeparse(bg),
			"color":            rgbDeparse(tx)
		},
		".dataTables_wrapper .dataTables_length,.dataTables_wrapper .dataTables_filter,.dataTables_wrapper .dataTables_info,.dataTables_wrapper .dataTables_processing,.dataTables_wrapper .dataTables_paginate": {
			"color":            rgbDeparse(fn)
		},
		".dataTables_wrapper .dataTables_paginate .paginate_button": {
			"color":            rgbDeparse(fn) + "!important"
		},
		"input, select": {
			"background-color": rgbDeparse(bg),
			"color":            rgbDeparse(vr),
			"border-color":     rgbDeparse(p10)
		},
		"table.dataTable tbody tr.odd": {
			"background-color": rgbDeparse(p05)
		},
		"td" : {
			"border-color": rgbDeparse(p10),
		}
	});
	$("style").first().text(style);
}

function generateStyle(styles) {
	var string = "";
	for (var selector in styles) {
		if (styles.hasOwnProperty(selector)) {
			string += selector + " {\n";

			var style = styles[selector];
			for (var property in style) {
				if (style.hasOwnProperty(property)) {
					string += "\t"+property + ": " + style[property] + ";\n";
				}
			}
			string += "}\n";
		}
	}
	return string;
}

function addViewer (id) {
	if (!lexiconViewers[id]) {
		layout.root.contentItems[ 0 ].addChild({
			type: "component",
			componentName: "Lexicon View",
			title: "Lexicon View - ",
			componentState: {
				id: id
			}
		});
	}
}

function resize() {
	// Resize all editors
	for (var k1 in codeEditors) {
		if (codeEditors.hasOwnProperty(k1)) { codeEditors[k1].resize(); }
	}
	for (var k2 in lexiconViewers) {
		if (lexiconViewers.hasOwnProperty(k2)) { lexiconViewers[k2].resize(); }
	}
	if (errorLogger) {errorLogger.resize();}
}

// ============================================================================
// DEFINE LAYOUT
// ============================================================================
var layout = new GoldenLayout({
	settings:{
		hasHeaders: true,
		constrainDragToContainer: true,
		reorderEnabled: true,
		selectionEnabled: false,
		popoutWholeStack: false,
		blockedPopoutsThrowError: true,
		closePopoutsOnUnload: true,
		showPopoutIcon: false,
		showMaximiseIcon: true,
		showCloseIcon: true
	},
	dimensions: {
		borderWidth: 5,
		minItemHeight: 10,
		minItemWidth: 10,
		headerHeight: 20,
		dragProxyWidth: 300,
		dragProxyHeight: 200
	},
	labels: {
		close: "close",
		maximise: "maximise",
		minimise: "minimise",
		popout: "open in new window"
	},
	content: [{
		type: "row",
		content:[{
			type: "column",
			content:[{
				type: "component",
				componentName: "Script Editor",
				isClosable: false,
				componentState: {
					id: "main",
					theme: "ace/theme/chrome"
				}
			},{
				type: "component",
				componentName: "Log View",
				isClosable: false,
				height: 20,
				componentState: {
					id: "logView",
					theme: "ace/theme/chrome"
				}
			}]
		}]
	}]
});

layout.registerComponent( "Script Editor", function( container, state ){
	container.getElement().append($("<div/>", {id: state.id}).addClass("codeEditor"));
	$(document).ready(function () {
		var editor = new CodeEditor(state.id);
		editor.setTheme(state.theme);
		codeEditors[state.id] = editor;
	});
	container.on("destroy", function () {
		delete codeEditors[state.id];
	});
	container.on("resize", function () {
		var editor = codeEditors[state.id];
		if (editor) {
			$(document).ready( function () {
				editor.resize();
			});
		}
	});
});

layout.registerComponent( "Log View", function( container, state ){
	container.getElement().append($("<div/>", {id: state.id}).addClass("codeEditor"));
	$(document).ready(function () {
		var logger = new ErrorLogger(state.id);
		logger.setTheme(state.theme);
		errorLogger = logger;
	});
	container.on("resize", function () {
		if (errorLogger) {
			$(document).ready( function () {
				errorLogger.resize();
			});
		}
	});
});

layout.registerComponent( "Lexicon View", function( container, state ){
	container.getElement().append($("<div/>", {id: state.id}).addClass("lexiconContainer"));
	lexiconViewers[state.id] = new LexiconViewer(state.id);
	container.on("destroy", function () {
		delete lexiconViewers[state.id];
	});
	container.on("resize", function () {
		var viewer = lexiconViewers[state.id];
		if (viewer) {
			$(document).ready( function () {
				viewer.resize();
			});
		}
	});
});




window.onresize = resize;
$(document).ready(function () {
	layout.init();
	resize();
});