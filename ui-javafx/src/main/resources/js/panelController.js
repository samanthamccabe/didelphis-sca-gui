/* jshint globalstrict: true*/
/* globals $, GoldenLayout, CodeEditor, ErrorLogger, LexiconViewer */
"use strict";

window.onerror = function (errorMsg, url, lineNumber, column, errorObj) {
	alert("Error: " + errorMsg + " Script: " + url + " Line: " + lineNumber + " Column: " + column + " StackTrace: " +  errorObj);
};

// Managed Components
var codeEditors = {};
var lexiconViewers = {};
var errorLogger;

var setCSS = function (theme) {
	var selector = $("link[href*='goldenlayout-theme']");
	selector.attr("href", "css/golden/goldenlayout-theme-" + theme + ".css");
};

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
					id: "main"
				}
			},{
				type: "component",
				componentName: "Log View",
				isClosable: false,
				height: 20,
				componentState: {
					id: "logView"
				}
			}]
//		},{
//			type: "column",
//			content:[{
//				type: "component",
//				componentName: "Lexicon View",
//				componentState: {
//					id: "mainviewer"
//				}
//			}]
		}]
	}]
});

layout.registerComponent( "Script Editor", function( container, state ){
	container.getElement().append($("<div/>", {id: state.id}).addClass("codeEditor"));
	$(document).ready(function () {
		var editor = new CodeEditor(state.id);
		editor.setTheme("ace/theme/chaos");
		codeEditors[state.id] = editor;
	});
});

layout.registerComponent( "Log View", function( container, state ){
	container.getElement().append($("<div/>", {id: state.id}).addClass("codeEditor"));
	$(document).ready(function () {
		var logger = new ErrorLogger(state.id);
		logger.setTheme("ace/theme/chaos");
		errorLogger = logger;
	});
});

layout.registerComponent( "Lexicon View", function( container, state ){
	container.getElement().append($("<div/>", {id: state.id}));
	lexiconViewers[state.id] = new LexiconViewer(state.id);
});

layout.init();
var resize = function () {
	// Resize all editors
	for (var key in codeEditors) {
		if (codeEditors.hasOwnProperty(key)) { codeEditors[key].resize(); }
	}
	if (errorLogger) {errorLogger.resize();}
};
window.onresize = resize;
$(document).ready(function () {resize();});

function addViewer (id) {
	layout.root.contentItems[ 0 ].addChild({
		type: "component",
		componentName: "Lexicon View",
		title: "Lexicon View - ",
		componentState: {
			id: id
		}
	});
}
