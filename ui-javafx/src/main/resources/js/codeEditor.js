/* jshint globalstrict: true*/
/* globals ace */
"use strict";

var Range = ace.require("ace/range").Range;

var fontFamilies = ["DejaVu Sans Mono", "Consolas", "Source Code Pro", "monospace"];

var editor = ace.edit("editor");
editor.setTheme("ace/theme/chaos");
editor.setFontSize(14);
editor.session.setMode("ace/mode/didelphissca");
editor.renderer.setOption("fontFamily", fontFamilies);
editor.on("change", function (delta) {
  var annotations = editor.session.getAnnotations();
  var deltaRange = range(delta.start.row, delta.end.row);
  for (var i = 0; i < annotations.length; i++) {
    var annotation = annotations[i];
    var annotationRange = range(annotation.row,annotation.end);
    if (deltaRange.intersects(annotationRange)) {
      annotations.splice(i, 1);
      i--; // step back
    }
  }
  setAnnotations(annotations); 
});

var log = ace.edit("logEditor");
log.container.parentNode.style.display="none";
log.setTheme("ace/theme/chaos");
log.setFontSize(12);
log.session.setMode("ace/mode/javascript");
log.session.setUseWorker(false);
log.session.setUseWrapMode(true);
log.setReadOnly(true);
log.setShowPrintMargin(false);
log.renderer.setOption("fontFamily", fontFamilies);

var errorMarkerClass   = "errorMarker";
var warningMarkerClass = "warningMarker";

var range = function(start, end) {
  return new Range(start, 0, end, 80);
}

var addMarker = function(start, end, markerClass) {
  editor.session.addMarker(range(start,end), markerClass, "fullLine", false);
};

var setAnnotations = function(annotations) {
  clearMarkers();
  for (var i = 0; i < annotations.length; i++) {
    setMarker(annotations[i]);
  }
  editor.session.setAnnotations(annotations);
};

var setMarker = function (annotation) {
  var row  = annotation.row;
  var end  = annotation.end;
  var type = annotation.type;

  if (type === "error") {
    addMarker(row, end, errorMarkerClass);
  } else
  if (type === "warn") {
    addMarker(row, end, warnMarkerClass);
  }
};

var clearMarkers = function () {
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