/* jshint globalstrict: true, unused: false */
/* globals $, ace, Handlebars */
"use strict";
var fontFamilies = ["DejaVu Sans Mono", "Consolas", "Source Code Pro", "monospace"];

var lexiconTableTemplate = Handlebars.compile($("#lexiconTableTemplate").html());
var lexiconRowTemplate   = Handlebars.compile($("#lexiconRowTemplate").html());

function range(start, end) {
	var Range = ace.require("ace/range").Range;
	return new Range(start, 0, end, 80);
}

var errorMarkerClass = "errorMarker";
var warningMarkerClass = "warningMarker";

function CodeEditor(id) {
	var self = this;
	self.id = id;
	self.editor = ace.edit(id);
	self.editor.session.setMode("ace/mode/didelphissca");
	self.editor.renderer.setOption("fontFamily", fontFamilies);
	self.editor.on("change", function(delta) {
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
		self.editor.setValue(data);
	};

	self.getValue = function () {
		return self.editor.getValue();
	};

	self.setShowInvisibles = function (b) {
		self.editor.setShowInvisibles(b);
	};

	self.resize = function () {
		self.editor.resize();
	};

	self.setNode = function (node) {
		self.node = node;
	};

	self.getNode = function () {
		return self.node;
	};

	self.setTheme = function (theme) {
		self.editor.setTheme(theme);
	};

	self.setFontSize = function (size) {
		self.editor.setFontSize(size);
	};

	self.getAnnotations = function () {
		return self.editor.session.getAnnotations();
	};

	self.addMarker = function (start, end, markerClass) {
		self.editor.session.addMarker(range(start, end), markerClass, "fullLine", false);
	};

	self.setAnnotations = function (annotations) {
		self.clearMarkers();
		for (var i = 0; i < annotations.length; i++) {
			self.setMarker(annotations[i]);
		}
		self.editor.session.setAnnotations(annotations);
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
		var session = self.editor.session;
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
		self.editor.session.clearAnnotations();
	};
}

function ErrorLogger(id) {
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
}

function LexiconViewer(id) {
	var self = this;
	self.id = id;

	self.createTable = function (data) {
		var table = $(lexiconTableTemplate(data));
		$("#" + self.id).empty().append(table);
	};

	self.initialize = function () {
		$("#" + self.id).find("table").DataTable({
			// "dom": "<'top'lf><'tablecontainer't><'bottom'p>",
			"dom": "<'dtHead'lf>tp",
			"scrollY": "300px",
			"lengthMenu": [ 25, 50, 100, 200 ],
			"pageLength": 50,
			"scrollCollapse": true
		});
		$(document).ready( function () { self.resize(); });
	};

	self.addData = function (data) {
		var body = $("#" + self.id).find("tbody");
		var row  = $(lexiconRowTemplate(data));
		body.append(row);
	};

	self.resize = function () {
		var container = $("#" + self.id);
		if (container.find("table").length) {
			var currHeightTotal = container.children().first().height();
			var parentHeight = container.parent().height();
			var delta = parentHeight - currHeightTotal;
			var scroller = container.find(".dataTables_scrollBody");
			if (scroller) {
				var maxHeight = parseInt(scroller.css("max-height").replace("px",""));
				scroller.css("max-height", "" + (maxHeight + delta - 20) + "px");
			}
		}
	};

	self.setNode = function (node) {
		self.node = node;
	};

	self.getNode = function () {
		return self.node;
	};
}