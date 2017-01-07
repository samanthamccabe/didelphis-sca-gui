/* jshint globalstrict: true*/
/* globals $, ace, PanelController */
"use strict";

window.onerror = function(errorMsg, url, lineNumber, column, errorObj) {
	alert("[ERROR] " + errorMsg + " Script: " + url + " Line: " + lineNumber +
		" Column: " + column + " StackTrace: " + errorObj);
};

var controller;

let config = {
	settings: {
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
		borderWidth: 10,
		minItemHeight: 10,
		minItemWidth: 10,
		headerHeight: 20,
		dragProxyWidth: 300,
		dragProxyHeight: 200
	},
	labels: {
		close: "close",
		maximise: "maximise",
		minimise: "minimise"
	},
	content: [{
		type: "row",
		id: "mainRow",
		content: [{
			type: "column",
			id: "mainColumn",
			content: [{
				type: "stack",
				id: "editorStack",
				content: [{
					type: "component",
					componentName: "Script Editor",
					isClosable: true,
					componentState: {
						id: "main"
					}
				}]
			}, {
				type: "component",
				componentName: "Log View",
				isClosable: false,
				height: 20,
				componentState: {
					id: "logView"
				}
			}]
		}]
		// },{
		// 	type: "stack",
		// 	id: "lexiconStack",
		// 	width: 0,
		// 	content: []
		// }]
	}]
};

window.onload = function () {
	controller = new PanelController(config);
	controller.layout.init();
	window.onresize = controller.resize;
};