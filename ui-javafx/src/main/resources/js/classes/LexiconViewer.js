/* jshint globalstrict: true, unused: false */
/* globals $, Handlebars */
"use strict";

const tableTemplate = Handlebars.compile($("#lexiconTableTemplate").html());
const rowTemplate = Handlebars.compile($("#lexiconRowTemplate").html());

class LexiconViewer {
	constructor (id, container) {
		this.id = id;
		this.container = container;
		this.table = undefined;
	}

	close() {
		if (this.table) { this.table.destroy() }
		this.container.destroy();
	}
	
	createTable (data) {
		$("#" + this.id).empty().append($(tableTemplate(data)));
	}

	initialize () {
		this.table = $("#" + this.id).find("table").DataTable({
			"dom": "<'dtHead'lf>tp",
			"scrollY": "300px",
			"lengthMenu": [25, 50, 100, 200],
			"pageLength": 50,
			"scrollCollapse": true
		});
		$(document).ready(() => {
			this.resize();
		});
	}

	addData (data) {
		let body = $("#" + this.id).find("tbody");
		let row = $(rowTemplate(data));
		body.append(row);
	}

	resize () {
		let container = $("#" + this.id);
		if (container.find("table").length) {
			let currHeightTotal = container.children().first().height();
			let parentHeight = container.parent().height();
			let delta = parentHeight - currHeightTotal;
			let scroller = container.find(".dataTables_scrollBody");
			if (scroller) {
				let maxHeight = parseInt(scroller.css("max-height").replace("px", ""));
				scroller.css("max-height", "" + (maxHeight + delta - 20) + "px");
			}
		}
	}
}