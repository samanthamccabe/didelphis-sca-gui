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


// non-constant globals --------------------------------------------------------
let logView      = null;
let projectTree  = null;
let projectFiles = null;

// mutable globals -------------------------------------------------------------
// maintain references to ace editor and fancytree objects
const editors  = new Map();
const lexicons = new Map();

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

const GoldenLayout = require('golden-layout');

const Ace = require('ace-builds/src-noconflict/ace');
Ace.config.set('basePath', '../node_modules/ace-builds/src-noconflict');
Ace.config.set('modePath', './modes');

// Import local objects --------------------------------------------------------
const Logger = require('./components/Logger');
const Editor = require('./components/Editor');
const Server = require('./util/Server');
const util   = require('./util/Util');
const config = require('./util/layout-config');

// -----------------------------------------------------------------------------
const theme_default = 'ace/theme/chaos';

const LOG    = new Logger(logView);
const SERVER = new Server('http://localhost:8080');

let layout = new GoldenLayout(config, $('#display'));

layout.registerComponent('editor', function (container, state) {
	let element = container.getElement();

	// Create div
	element.html(`<div id=${state.id} class='editor'>${state.text}</div>`);

	// Assign Behavior
	container.on('open', () => {
		let id = state.id;
		let domElement = element.children(`#${id}`).get(0);

		const editor = Ace.edit(domElement);

		if (editors.has(id)) {
			editors.get(id).restore(editor);
		} else {
			let options = {
				mode: 'ace/mode/sca',
				theme: theme_default
			};
			editors.set(id, new Editor(container, editor, options));
		}
	});

	container.on('close', () => {
		// TODO:
	});
});

layout.registerComponent('lexicon', function (container, state) {
	const element = container.getElement();

	// Create div
	element.html(`<div id=${state.id} class='editor'>${state.text}</div>`);

	// Assign behavior
	container.on('open', () => {
		let id = state.id;
		let domElement = element.children(`#${id}`).get(0);
		let options = {
			mode: 'ace/mode/text',
			theme: theme_default
		};
		const editor = Ace.edit(domElement);
		lexicons.set(id, new Editor(container, editor, options));
	});
});

layout.registerComponent('log-view', function (container, state) {
	const element = container.getElement();

	// Create div
	element.html(`<div id=${state.id} class='editor'>${state.text}</div>`);

	// Assign behavior
	container.on('open', () => {
		let editor = Ace.edit(state.id);
		editor.setTheme(theme_default);
		editor.session.setMode('ace/mode/log');
		container.editor = editor;
		logView = editor;
		LOG.setLogView(logView);
	});
});

layout.registerComponent('project-tree', function (container, state) {
	container.getElement().html(`<div id='${state.id}' class='project'></div>`);
	container.on('open', () => {
		let tree = $(`#${state.id}`);
		tree.fancytree({
			// checkbox: true,
			source: [],
			activate: function (event, data) {
				LOG.info(JSON.stringify(data));
			}
		});
		projectTree = tree.fancytree('getTree');
	});
});

layout.registerComponent('project-files', function (container, state) {
	container.getElement()
		.html(`<div id="${state.id}" class='project'></div>`);
	container.on('open', () => {
		let tree = $(`#${state.id}`);
		tree.fancytree({
			// checkbox: true,
			source: [],
			activate: function (event, data) {
				LOG.info(JSON.stringify(data));
			}
		});
		projectFiles = tree.fancytree('getTree');
	});
});

layout.init();

function projectFilesToNodes(files) {

	let scripts   = [];
	let lexiconsR = [];
	let lexiconsW = [];
	let model     = [];

	for (let i = 0; i < files.length; i++) {
		let file = files[i];

		let type = file.fileType;

		let node = {
			key:   util.normPath(file.filePath),
			title: util.trimPath(file.filePath)
		};

		switch (type) {
			case 'SCRIPT':
				scripts.push(node);
				break;
			case 'LEXICON_READ':
				lexiconsR.push(node);
				break;
			case 'LEXICON_WRITE':
				lexiconsW.push(node);
				break;
			case 'MODEL':
				model.push(node);
				break;
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

/**
 * Creates new editor panels from an array of project files
 * @param files an array of project files returned by the server
 */
function createPanels(files) {
	let items = layout.root.getItemsById('editors');
	if ((items.length < 1)) {
		LOG.error('Unable to locate editor pane.')
	} else {
		const editorConfigs   = [];
		const modelConfigs    = [];
		const lexiconRConfigs = [];
		const lexiconWConfigs = [];

		let contentItem = items[0];
		for (const file of files) {
			const fileType = file.fileType;
			const filePath = file.filePath;
			const fileData = file.fileData;

			const id = util.normPath(filePath);

			if (fileType === 'SCRIPT') {
				editorConfigs.push({
					isClosable: false,
					title: util.trimPath(filePath),
					type: 'component',
					componentName: 'editor',
					componentState: {
						id: id,
						text: fileData,
					}
				});
			} else if (fileType === 'LEXICON_READ') {
				lexiconRConfigs.push({
					isClosable: false,
					title: util.trimPath(filePath),
					type: 'component',
					componentName: 'lexicon',
					componentState: {
						id: id,
						text: fileData,
					}
				});
			} else if (fileType === 'LEXICON_WRITE') {
				lexiconWConfigs.push({
					isClosable: false,
					title: util.trimPath(filePath),
					type: 'component',
					componentName: 'lexicon',
					componentState: {
						id: id,
						text: fileData,
					}
				});
			} else if (fileType === 'MODEL') {
				modelConfigs.push({
					isClosable: false,
					title: util.trimPath(filePath),
					type: 'component',
					componentName: 'editor',
					// TODO: assign the correct mode
					componentState: {
						id: id,
						text: fileData,
					}
				});
			}
		}

		contentItem.addChild({
			type: 'row',
			content: [{
				type: 'stack',
				content: editorConfigs
			}, {
				type: 'stack',
				content: lexiconRConfigs
			}, {
				type: 'stack',
				content: lexiconWConfigs
			}]
		});
	}
}

/**
 * Updates the UI state for a new project
 * @param projectJSON project data returned from the server
 */
function loadNewProject(projectJSON) {
	let object = JSON.parse(projectJSON);
	let files = object.projectFiles;

	let filetree = object.fileTree;
	let projectNodes = projectFilesToNodes(files);

	// Reload the project structure --------------------------------------------
	projectTree.reload(filetree);
	projectFiles.reload(projectNodes);

	// Clear and reload editor panes -------------------------------------------
	for (const lexicon of lexicons.values()) {
		lexicon.destroy();
	}
	lexicons.clear();

	for (const editor of editors.values()) {
		editor.destroy();
	}
	editors.clear();

	// Populate the new editors ------------------------------------------------
	createPanels(files);
}

const template = [{
	label: 'Project',
	submenu: [
		{
			label: 'Open',
			click: () => {
				dialog.showOpenDialog({
					multiSelections: false
				}, paths => {
					SERVER.post('/loadNewProject', paths[0], loadNewProject);
				});
			}
		}, {
			label: 'Save',
			click: () => {
				// TODO
			}
		}, {
			label: 'Save As',
			click: () => {
				dialog.showSaveDialog({}, paths => LOG.info(paths));
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
}];

const menu = Menu.buildFromTemplate(template);
currentWindow.setMenu(menu);