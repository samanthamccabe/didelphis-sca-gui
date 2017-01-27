/* jshint globalstrict: true, unused: false */
/* globals ace */
"use strict";

const errorMarkerClass = "errorMarker";
const warningMarkerClass = "warningMarker";

class CodeEditor {
	constructor(id, container) {
		this.id = id;
		this.container = container;

		let element = container.getElement().append($("<div/>", {
			id: id
		}).addClass("codeEditor"))[0];

		this.editor = ace.edit(element);
		this.editor.session.setMode("ace/mode/didelphissca");
		this.editor.renderer.setOption("fontFamily", [
			"DejaVu Sans Mono",
			"Consolas",
			"Source Code Pro",
			"monospace"
		]);
		this.editor.on("change", (delta) => {
				if (this.editor) {
					let annotations = this.editor.session.getAnnotations();
					let deltaRange = CodeEditor.range(delta.start.row, delta.end.row);
					for (let i = 0; i < annotations.length;) {
						let annotation = annotations[i];
						let annotationRange = CodeEditor.range(annotation.row, annotation.end);
						if (deltaRange.intersects(annotationRange)) {
							annotations.splice(i, 1);
						} else {
							i++;
						}
					}
					let session = this.editor.session;
					let markers = session.getMarkers(false);
					for (let key in markers) {
						if (markers.hasOwnProperty(key)) {
							let element = markers[key];
							if (element.clazz === errorMarkerClass || element.clazz === warningMarkerClass) {
								session.removeMarker(element.id);
							}
						}
					}
					for (let i = 0; i < annotations.length; i++) {
						this.setMarker(annotations[i]);
					}
					this.editor.session.setAnnotations(annotations);
				}
			});
	}

	close() {
		this.editor.destroy();
		this.container.close();
	}

	// Get/Set --- Value
	setValue(data) {
		this.editor.setValue(data);
	}

	getValue() {
		return this.editor.getValue();
	}

	// Get/Set ---Annotations
	getAnnotations() {
		return this.editor.session.getAnnotations();
	}

	setAnnotations(annotations) {
		this.clearMarkers();
		for (let i = 0; i < annotations.length; i++) {
			this.setMarker(annotations[i]);
		}
		this.editor.session.setAnnotations(annotations);
	}

	addAnnotations(annotation) {
		this.setMarker(annotation);
		this.editor.session.getAnnotatoins().push(annotation);
	}

	// General Methods
	setShowInvisibles(b) {
		this.editor.setShowInvisibles(b);
	}

	resize() {
		this.editor.resize();
	}

	setTheme(theme) {
		this.editor.setTheme("ace/theme/" + theme);
	}

	setFontSize(size) {
		this.editor.setFontSize(size);
	}

	addMarker(start, end, markerClass) {
		this.editor.session.addMarker(CodeEditor.range(start, end), markerClass, "fullLine", false);
	}

	setMarker(annotation) {
		let row = annotation.row;
		let end = annotation.end;
		let type = annotation.type;

		if (type === "error") {
			this.addMarker(row, end, errorMarkerClass);
		}
		if (type === "warn") {
			this.addMarker(row, end, warningMarkerClass);
		}
	}

	clearMarkers() {
		let session = this.editor.session;
		let markers = session.getMarkers(false);
		for (let key in markers) {
			if (markers.hasOwnProperty(key)) {
				let element = markers[key];
				if (element.clazz === errorMarkerClass || element.clazz === warningMarkerClass) {
					session.removeMarker(element.id);
				}
			}
		}
	}

	clearAnnotations() {
		this.editor.session.clearAnnotations();
	}

	// STATIC METHODS =========================================================
	static range(start, end) {
		let Range = ace.require("ace/range").Range;
		return new Range(start, 0, end, 80);
	}
}
