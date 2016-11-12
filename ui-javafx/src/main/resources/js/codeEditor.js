/* jshint globalstrict: true*/
/* globals ace */
"use strict";

var Range = ace.require("ace/range").Range;

var fontFamilies = ["DejaVu Sans Mono", "Consolas", "Source Code Pro", "monospace"];

var editor = ace.edit("editor");
editor.setTheme("ace/theme/monokai");
editor.setFontSize(14);
editor.session.setMode("ace/mode/didelphissca");
editor.renderer.setOption("fontFamily", fontFamilies);

var log = ace.edit("logEditor");
log.container.parentNode.style.display="none";
log.setTheme("ace/theme/monokai");
log.setFontSize(12);
log.session.setMode("ace/mode/javascript");
log.session.setUseWorker(false);
log.session.setUseWrapMode(true);
log.setReadOnly(true);
log.setShowPrintMargin(false);
log.renderer.setOption("fontFamily", fontFamilies);

var errorMarkerClass = "errorMarker";

var addMarker = function(a, b, c, d) {
  editor.session.addMarker(new Range(a, b, c, d), errorMarkerClass, "fullLine", false);
};

var clearMarkers = function () {
  var session = editor.session;
  var markers = session.getMarkers(false);
  for (var key in markers) {
    if (markers.hasOwnProperty(key)) {
      var element = markers[key];
      if (element.clazz === errorMarkerClass) {
        session.removeMarker(element.id);
      }
    }
  }
};