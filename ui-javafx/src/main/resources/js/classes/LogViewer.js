/* jshint globalstrict: true, unused: false */
/* globals ace */
"use strict";

class LogViewer {

	constructor(id, container) {
		this.id = id;
		this.container = container;
		
		let logger = ace.edit(id);
		logger.setFontSize(12);
		logger.session.setMode("ace/mode/didelphislog");
		logger.session.setUseWorker(false);
		logger.session.setUseWrapMode(true);
		logger.setReadOnly(true);
		logger.setShowPrintMargin(false);
		logger.renderer.setOption("fontFamily", [
			"DejaVu Sans Mono",
			"Consolas",
			"Source Code Pro",
			"monospace"
		]);
		this.logger = logger;
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