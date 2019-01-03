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

let logView      = null;
let projectTree  = null;
let projectFiles = null;

// Load electron classes -------------------------------------------------------

const {remote} = require('electron');

const {Menu, dialog} = remote;

const currentWindow = remote.getCurrentWindow();

// Load jquery -----------------------------------------------------------------
const $ = require('jquery');
window.jQuery = $;
window.$ = $;

require('jquery-ui');
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

const components = {
	projectTree: () => {
		return {
			title: 'Project Tree',
			type: 'component',
			componentName: 'project-tree',
			componentState: {
				id: 'project-tree'
			},
			isClosable: false
		}
	},
	projectFiles: () => {
		return {
			title: 'Project Files',
			type: 'component',
			componentName: 'project-files',
			componentState: {
				id: 'project-files'
			},
			isClosable: false
		}
	},
	editor: (id) => {
		return {
			title: 'Editor ' + id,
			type: 'component',
			componentName: 'editor',
			componentState: {
				id: 'Editor' + id,
				text: '% Editor ' + id
			}
		}
	},
	logView: () =>  {
		return {
			title: "Message Log",
			type: 'component',
			componentName: 'log-view',
			componentState: {
				id: 'ConsoleLog',
				text: '[INFO] LOG START'
			},
			isClosable: false,
			height: 40
		}
	}
};

let config = {
	content: [{
		type: 'row',
		content: [{
			type: 'column',
			width: 20,
			content: [
				components.projectTree(),
				components.projectFiles(),
			]
		}, {
			type: 'column',
			content: [
				{
					type: 'stack',
					content: [
						components.editor(1),
						components.editor(2),
						components.editor(3)
					]
				},
				components.logView()
			]
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

myLayout.registerComponent('log-view', function (container, state) {
	container.getElement()
		.html(`<div id=${state.id} class=editor>${state.text}</div>`);
	container.on('open', () => {
		let editor = Ace.edit(state.id);
		editor.setTheme(theme_crimson);
		editor.session.setMode(mode_javascript);
		container.editor = editor;
		logView = editor;
	});
});

myLayout.registerComponent('project-tree', function (container, state) {
	container.getElement()
		.html(`<div id="${state.id}" class="project"></div>`);
	container.on('open', () => {
		let tree = $(`#${state.id}`);
		tree.fancytree({
			// checkbox: true,
			source: [],
			activate: function (event, data) {
				LOG.info(data)
			}
		});
		projectTree = tree.fancytree("getTree");
	});
});

myLayout.registerComponent('project-files', function (container, state) {
	container.getElement()
		.html(`<div id="${state.id}" class="project"></div>`);
	container.on('open', () => {
		let tree = $(`#${state.id}`);
		tree.fancytree({
			// checkbox: true,
			source: [],
			activate: function (event, data) {
				LOG.info(data)
			}
		});
		projectFiles = tree.fancytree("getTree");
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
			projectTree.reload(JSON.parse(response));
		},
		error: response => {
			LOG.error(response);
		}
	})
});

function projectFilesToNodes(files) {

	let scripts = [];
	let lexiconsR = [];
	let lexiconsW = [];
	let model = [];

	for (let i = 0; i < files.length; i++) {
		let file = files[i];

		let type = file.fileType;

		let node = {
			key: file.filePath,
			title: file.filePath.replace(/.*\/(.*?)$/,'$1')
		};

		if (type === 'SCRIPT') {
			scripts.push(node);
		} else if (type === 'LEXICON_READ') {
			lexiconsR.push(node);
		} else if (type === 'LEXICON_WRITE') {
			lexiconsW.push(node);
		} else if (type === 'MODEL') {
			model.push(node);
		}
	}

	return [{
		key: 'scripts',
		title: 'Scripts',
		folder: true,
		children: scripts
	}, {
		key: 'lexiconsR',
		title: 'Lexicons (Read)',
		folder: true,
		children: lexiconsR
	}, {
		key: 'lexiconsW',
		title: 'Lexicons (Write)',
		folder: true,
		children: lexiconsW
	}, {
		key: 'model',
		title: 'Model',
		folder: true,
		children: model
	}];
}

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
							let object = JSON.parse(response);
							let files = object.projectFiles;

							let filetree = object.fileTree;
							let projectNodes = projectFilesToNodes(files);

							projectTree.reload(filetree);
							projectFiles.reload(projectNodes);
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