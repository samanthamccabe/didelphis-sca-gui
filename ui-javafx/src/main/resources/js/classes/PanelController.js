/* jshint globalstrict: true*/
/* globals $, ace, GoldenLayout, CodeEditor, LogViewer, LexiconViewer, rgbDeparse, rulesForCssText, parseColor, minus, plus, times */
"use strict";

class PanelController {
	constructor(config) {
		let codeEditors    = new Map();
		let lexiconViewers = new Map();	
		let logViewer      = {};

		let layout = new GoldenLayout(config);

		layout.registerComponent("Script Editor", function (container, state) {
			container.getElement().append($("<div/>", {
				id: state.id
			}).addClass("codeEditor"));
			container.on("destroy", () => {
				codeEditors.delete(state.id);
			});
			container.on("resize", () => {
				let editor = codeEditors.get(state.id);
				if (editor) {
					$(document).ready(() => {
						editor.resize();
					});
				}
			});
			$(document).ready(() => {
				codeEditors.set(state.id, new CodeEditor(state.id, container));
			});
		});

		layout.registerComponent("Log View", function (container, state) {
			container.getElement().append($("<div/>", {
				id: state.id
			}).addClass("codeEditor"));
			container.on("resize", () => {
				if (logViewer) {
					$(document).ready(() => {
						logViewer.resize();
					});
				}
			});
			$(document).ready(() => {
				logViewer = new LogViewer(state.id, container);
			});
		});

		layout.registerComponent("Lexicon View", function (container, state) {
			container.getElement().append($("<div/>", {
				id: state.id
			}).addClass("lexiconContainer"));
			container.on("destroy", () => {
				lexiconViewers.delete(state.id);
			});
			container.on("resize", () => {
				let viewer = lexiconViewers.get(state.id);
				if (viewer) {
					$(document).ready(() => {
						viewer.resize();
					});
				}
			});
			$(document).ready(() => {
				lexiconViewers.set(state.id, new LexiconViewer(state.id, container));
			});
		});

		// Add as properties
		this.codeEditors = codeEditors;
		this.lexiconViewers = lexiconViewers;
		this.logViewer = logViewer;
		this.layout = layout;
	}

	clear() {
		this.codeEditors.forEach((editor)    => { editor.close(); });
		this.lexiconViewers.forEach((viewer) => { viewer.close(); });
	}

	addEditor(id) {
		if (!this.codeEditors.has(id)) {
			// console.log(this.layout.root);
			this.layout.root.contentItems[0].addChild({
				
			});
		}
	}

	addViewer(id) {
		if (!this.lexiconViewers.has(id)) {
			this.layout.root.contentItems[0].addChild({
				type: "component",
				componentName: "Lexicon View",
				title: "Lexicon View - ",
				componentState: {
					id: id,
					parent: this.layout.root.contentItems[0]
				}
			});
		}
	}

	resize() {
		if (this.codeEditors) {
			this.codeEditors.forEach((value) => {
				value.resize();
			});
		}

		if (this.lexiconViewers) {
			this.lexiconViewers.forEach((value) => {
				value.resize();
			});
		}

		if (this.logViewer) {
			this.logViewer.resize();
		}
	}

	setTheme(type, theme) {
		this.codeEditors.forEach((value) => {
			value.setTheme(theme);
		});
		this.logViewer.setTheme(theme);

		$("link[href*='goldenlayout-theme']").attr("href", "css/golden/goldenlayout-theme-" + type + ".css");
		$("link[href*='didelphis-theme']").attr("href", "css/didelphis-theme-" + type + ".css");

		let moduleName = "ace/theme/" + theme;
		let module = ace.define.modules[moduleName];
		if (!module) {
			ace.config.loadModule(moduleName, function() {
				module = ace.require(moduleName);
				PanelController.buildCSS(module);
			});
		}
	}

	static generateStyle(styles) {
		let string = "";
		for (let selector in styles) {
			if (styles.hasOwnProperty(selector)) {
				string += selector + " {\n";
				let style = styles[selector];
				for (let property in style) {
					if (style.hasOwnProperty(property)) {
						string += "\t" + property + ": " + style[property] + ";\n";
					}
				}
				string += "}\n";
			}
		}
		return string;
	}

	static contains(string, testsArr) {
		for (let i = 0; i < testsArr.length; i++) {
			if (string.indexOf(testsArr[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	static buildCSS(module) {

		let cssRules = rulesForCssText(module.cssText);

		let bg, // Background
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
				if (selectorText && textColor) {
					if (/^\.ace-[a-z_\-]+$/.test(selectorText)) {
						bg = parseColor(background);
						tx = parseColor(textColor);
					} else if (!fn && PanelController.contains(selectorText, [".ace_function"])) {
						fn = parseColor(textColor);
					} else if (!st && PanelController.contains(selectorText, [".ace_string"])) {
						st = parseColor(textColor);
					} else if (!nm && PanelController.contains(selectorText, [".ace_numeric"])) {
						nm = parseColor(textColor);
					} else if (!vr && PanelController.contains(selectorText, [".ace_variable", ".ace_language"])) {
						vr = parseColor(textColor);
					} else if (!kw && PanelController.contains(selectorText, [".ace_keyword", ".ace_type"])) {
						kw = parseColor(textColor);
					}
				}
			}
		}

		var dif = minus(bg, tx);
		var p75 = minus(bg, times(dif, 0.75));
		var p50 = minus(bg, times(dif, 0.50));
		var p25 = minus(bg, times(dif, 0.25));
		var p10 = minus(bg, times(dif, 0.10));
		var p05 = minus(bg, times(dif, 0.05));

		var style = PanelController.generateStyle({
			"body": {
				"background-color": rgbDeparse(bg),
				"color": rgbDeparse(tx)
			},
			"table.dataTable tbody tr": {
				"background-color": rgbDeparse(bg),
				"color": rgbDeparse(tx)
			},
			".dataTables_wrapper .dataTables_length,.dataTables_wrapper .dataTables_filter,.dataTables_wrapper .dataTables_info,.dataTables_wrapper .dataTables_processing,.dataTables_wrapper .dataTables_paginate": {
				"color": rgbDeparse(fn)
			},
			".dataTables_wrapper .dataTables_paginate .paginate_button": {
				"color": rgbDeparse(fn) + "!important"
			},
			"input, select": {
				"background-color": rgbDeparse(bg),
				"color": rgbDeparse(vr),
				"border-color": rgbDeparse(p10)
			},
			"table.dataTable tbody tr.odd": {
				"background-color": rgbDeparse(p05)
			},
			"td": {
				"border-color": rgbDeparse(p10),
			}
		});
		$("style").first().text(style);
	}
}