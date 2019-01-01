/******************************************************************************
 * Didelphis Grammatekton - an graphical development environment for          *
 * constructed language development and sound-change rule application         *
 *                                                                            *
 * Copyright (C) 2016 Samantha F McCabe                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

let logView = null;
let fileTree = null;

// Load electron classes -------------------------------------------------------

const {remote} = require('electron');

const {Menu, dialog} = remote;

const currentWindow = remote.getCurrentWindow();

// Load jquery -----------------------------------------------------------------
const $ = require('jquery');
window.jQuery = $;
window.$ = $;

require('jquery-ui');
// require('jquery-ui/themes/base/core.css');
require('jquery.fancytree');

const GoldenLayout = require("golden-layout");

const Ace = require('ace-builds/src-noconflict/ace');

Ace.config.set('basePath', '../node_modules/ace-builds/src-noconflict');

const theme_crimson = 'ace/theme/crimson_editor';
const mode_javascript = 'ace/mode/javascript';

const editors = {};

const CONTENT_JSON = 'application/json; charset=UTF-8';

const ENDPOINT = 'http://localhost:8080';

const LOG = {
	info: (message) => {
		if (logView) {
			let value = logView.getValue();
			value += '[INFO] ' + message;
			logView.setValue(value);
		} else {
			console.log('[INFO] UI Console unavailable --- ', message);
		}
	},
	warn: (message) => {
		if (logView) {
			let value = logView.getValue();
			value += '[WARN] ' + message;
			logView.setValue(value);
		} else {
			console.log('[WARN] UI Console unavailable --- ', message);
		}
	},
	error: (message) => {
		if (logView) {
			let value = logView.getValue();
			value += '[ERROR] ' + message;
			logView.setValue(value);
		} else {
			console.log('[ERROR] UI Console unavailable --- ', message);
		}
	}
};

let config = {
	content: [{
		type: 'column',
		content: [{
			type: 'row',
			content: [{
				type: 'component',
				componentName: 'project',
				componentState: {
					id: 'project-tree',
					text: ''
				},
				isClosable: false,
				width: 20
			}, {
				type: 'stack',
				content: [{
					type: 'component',
					componentName: 'editor',
					componentState: {
						id: 'Editor1',
						text: '% Editor 1'
					}
				}]
			}]
		}, {
			title: "Message Log",
			type: 'component',
			componentName: 'logView',
			componentState: {
				id: 'ConsoleLog',
				text: '[INFO] LOG START'
			},
			isClosable: false
		}]
	}]
};

let myLayout = new GoldenLayout(config);

myLayout.registerComponent('editor', function (container, state) {
	container.getElement()
		.html('<div id=' + state.id + ' class=editor>' + state.text + '</div>');
	container.on('open', () => {
		let editor = Ace.edit(state.id);
		editor.setTheme(theme_crimson);
		editor.session.setMode(mode_javascript);
		container.editor = editor;
		editors[state.id] = editor;
	});
});

myLayout.registerComponent('logView', function (container, state) {
	container.getElement()
		.html('<div id=' + state.id + ' class=editor>' + state.text + '</div>');
	container.on('open', () => {
		let editor = Ace.edit(state.id);
		editor.setTheme(theme_crimson);
		editor.session.setMode(mode_javascript);
		container.editor = editor;
		logView = editor;
	});
});

myLayout.registerComponent('project', function (container, state) {
	container.getElement()
		.html('<div id="tree"></div>');
	container.on('open', () => {
		let tree = $("#tree");
		tree.fancytree({
			// checkbox: true,
			source: [],
			activate: function (event, data) {
				$("#status").text('Activate: ' + data.node);
			}
		});
		fileTree = tree.fancytree("getTree");
	});
});

myLayout.init();

$("#openFile").on('change', function () {
	let file = this.files[0];

	$.ajax({
		url: ENDPOINT + '/loadNewProject',
		method: 'POST',
		contentType: CONTENT_JSON,
		data: file.path,
		success: response => {
			// LOG.info(response);
			fileTree.reload(JSON.parse(response));
		},
		error: response => {
			LOG.error(response);
		}
	})
});

const template = [{
	label: 'Project',
	submenu: [
		{
			label: 'Open',
			click() {
				dialog.showOpenDialog({
					multiSelections: false
				}, paths => {
					$.ajax({
						url: ENDPOINT + '/loadNewProject',
						method: 'POST',
						contentType: CONTENT_JSON,
						data: paths[0],
						success: response => {
							fileTree.reload(JSON.parse(response));
						},
						error: response => {
							LOG.error(response);
						}
					})
				});
			}
		},
		{role: 'quit'}
	]
}, {
	label: 'Edit',
	submenu: [
		{role: 'undo'},
		{role: 'redo'},
		{type: 'separator'},
		{role: 'cut'},
		{role: 'copy'},
		{role: 'paste'},
		{role: 'pasteandmatchstyle'},
		{role: 'delete'},
		{role: 'selectall'}
	]
}, {
	label: 'View',
	submenu: [
		{role: 'reload'},
		{role: 'forcereload'},
		{role: 'toggledevtools'},
		{type: 'separator'},
		{type: 'separator'},
		{role: 'togglefullscreen'}
	]
}, {
	role: 'window',
	submenu: [
		{role: 'minimize'},
		{role: 'close'}
	]
}, {
	role: 'help',
	submenu: [{
		label: 'Learn More',
		click() {

		}
	}]
}];

const menu = Menu.buildFromTemplate(template);
currentWindow.setMenu(menu);