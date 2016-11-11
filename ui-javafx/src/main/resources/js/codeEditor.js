var editor = ace.edit("editor");
editor.setTheme("ace/theme/monokai");
editor.setFontSize(14);
editor.session.setMode("ace/mode/didelphissca");

var log = ace.edit("logEditor");
log.container.parentNode.style.display='none';
log.setTheme("ace/theme/monokai");
log.setFontSize(12);
log.session.setMode("ace/mode/javascript");
log.session.setUseWorker(false);
log.session.setUseWrapMode(true);
log.setReadOnly(true);
log.setShowPrintMargin(false);