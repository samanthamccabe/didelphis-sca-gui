/* jshint globalstrict: true*/
/* globals $, GoldenLayout, CodeEditor, ErrorLogger, LexiconViewer, rgbDeparse, rulesForCssText, parseColor */
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

/*
	// Dynamic theme generation --- deferred
	var cssRules = rulesForCssText(codeEditors.main.editor.renderer.theme.cssText);
	
	var bg,      // Background
	    tx,      // Foreground
	    st,      // String
	//  vr,      // Variable
	    nm,      // Number
	    fn = []; // Function

	for (var key in cssRules) {
		if (cssRules.hasOwnProperty(key)) {
			var rule = cssRules[key];
			var selectorText = rule.selectorText;

			if (!rule.style) { continue; } // get out

			var background   = rule.style["background-color"];
			var textColor    = rule.style.color;

			if (selectorText && (textColor || background)) {
				// Background color
				if (/^\.ace-[a-z_\-]+\s*$/.test(selectorText)) {
					bg = parseColor(background);
					tx = parseColor(textColor);
				} else if (contains(selectorText, [".ace_function"])) {
					fn = parseColor(textColor);
				} else if (contains(selectorText, [".ace_string"])) {
					tx = parseColor(textColor);
				} else if (contains(selectorText, [".ace_numeric"])) {
					nm = parseColor(textColor);
				}
			}
		}
	}
	*/
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

layout.init();

window.onresize = resize;
$(document).ready(function () {
	resize();
});