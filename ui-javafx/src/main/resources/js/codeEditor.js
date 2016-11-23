/* jshint globalstrict: true, unused: false */
/* globals $, ace, Handlebars */
"use strict";
var fontFamilies = ["DejaVu Sans Mono", "Consolas", "Source Code Pro", "monospace"];

var lexiconViewTemplate = Handlebars.compile($("#lexiconViewTemplate").html());

var range = function(start, end) {
	var Range = ace.require("ace/range").Range;
	return new Range(start, 0, end, 80);
};

var errorMarkerClass = "errorMarker";
var warningMarkerClass = "warningMarker";

//TODO: needs to be initialized with a theme???
var CodeEditor = function (id) {
	var self = this;
	self.id = id;
	var editor = ace.edit(id);
	editor = ace.edit(id);
	editor.session.setMode("ace/mode/didelphissca");
	editor.renderer.setOption("fontFamily", fontFamilies);
	editor.on("change", function(delta) {
		var annotations = self.getAnnotations();
		var deltaRange = range(delta.start.row, delta.end.row);
		for (var i = 0; i < annotations.length;) {
			var annotation = annotations[i];
			var annotationRange = range(annotation.row, annotation.end);
			if (deltaRange.intersects(annotationRange)) {
				annotations.splice(i, 1);
			} else {
				i++;
			}
		}
		self.setAnnotations(annotations);
	});

	self.setValue = function (data) {
		editor.setValue(data);
	};

	self.getValue = function () {
		return editor.getValue();
	};

	self.setShowInvisibles = function (b) {
		editor.setShowInvisibles(b);
	};

	self.resize = function () {
		editor.resize();
	};

	self.setNode = function (node) {
		self.node = node;
	};

	self.getNode = function () {
		return self.node;
	};

	self.setTheme = function (theme) {
		editor.setTheme(theme);
	};

	self.setFontSize = function (size) {
		editor.setFontSize(size);
	};

	self.getAnnotations = function () {
		return editor.session.getAnnotations();
	};

	self.addMarker = function (start, end, markerClass) {
		editor.session.addMarker(range(start, end), markerClass, "fullLine", false);
	};

	self.setAnnotations = function (annotations) {
		self.clearMarkers();
		for (var i = 0; i < annotations.length; i++) {
			self.setMarker(annotations[i]);
		}
		editor.session.setAnnotations(annotations);
	};

	self.setMarker = function (annotation) {
		var row  = annotation.row;
		var end  = annotation.end;
		var type = annotation.type;

		if (type === "error") {
			self.addMarker(row, end, errorMarkerClass);
		}
		if (type === "warn") {
			self.addMarker(row, end, warningMarkerClass);
		}
	};

	self.clearMarkers = function () {
		var session = editor.session;
		var markers = session.getMarkers(false);
		for (var key in markers) {
			if (markers.hasOwnProperty(key)) {
				var element = markers[key];
				if (element.clazz === errorMarkerClass || element.clazz === warningMarkerClass) {
					session.removeMarker(element.id);
				}
			}
		}
	};

	self.clearAnnotations = function () {
		editor.session.clearAnnotations();
	};
};

var ErrorLogger = function (id) {
	var self = this;
	self.id = id;
	var logger = ace.edit(id);
	logger.setFontSize(12);
	logger.session.setMode("ace/mode/didelphislog");
	logger.session.setUseWorker(false);
	logger.session.setUseWrapMode(true);
	logger.setReadOnly(true);
	logger.setShowPrintMargin(false);
	logger.renderer.setOption("fontFamily", fontFamilies);
	self.logger = logger;

	self.resize = function () {
		logger.resize();
	};

	self.setNode = function (node) {
		self.node = node;
	};

	self.setTheme = function (theme) {
		logger.setTheme(theme);
	};

	self.clear = function () {
		logger.setValue("", 0);
	};

	self.append = function (data) {
		logger.insert(data);
	};

	self.resize = function () {
		logger.resize();
	};
};

var LexiconViewer = function (id) {
	var self = this;
	self.id = id;

	self.setValue = function (data) {
		var table = $(lexiconViewTemplate(data));
		$("#"+self.id).empty().append(table).find("table").DataTable({
			"dom": "<'top'lf><'tablecontainer't><'bottom'p>",
			// "scrollY": "300px",
			"scrollCollapse": true
		});
	};

	self.setNode = function (node) {
		self.node = node;
	};

	self.getNode = function () {
		return self.node;
	};
};