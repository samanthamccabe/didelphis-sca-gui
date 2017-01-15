/* jshint globalstrict: true, unused: false */
/* globals ace */
"use strict";

class LogViewer {

	constructor(id, container) {
		this.id = id;
		this.container = container;

		let element = container.getElement().append($("<div/>", {
			id: id
		}).addClass("codeEditor"))[0];

		this.logger = ace.edit(element);
		this.logger.setFontSize(12);
		this.logger.session.setMode("ace/mode/didelphislog");
		this.logger.session.setUseWorker(false);
		this.logger.session.setUseWrapMode(true);
		this.logger.setReadOnly(true);
		this.logger.setShowPrintMargin(false);
		this.logger.renderer.setOption("fontFamily", [
			"DejaVu Sans Mono",
			"Consolas",
			"Source Code Pro",
			"monospace"
		]);
	}

	resize() {
		this.logger.resize();
	}

	setTheme(theme) {
		this.logger.setTheme("ace/theme/" + theme);
	}

	clear() {
		this.logger.setValue("", 0);
	}

	append(data) {
		this.logger.insert(data);
	}
}