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
Ace.config.set('modePath', './js/modes');

// Import local objects --------------------------------------------------------
const Project     = require('./js/project/Project');
const ProjectFile = require('./js/project/ProjectFile');

const Logger = require('./js/components/Logger');
const Editor = require('./js/components/Editor');

const Server = require('./js/util/Server');
const Util   = require('./js/util/Util');
const config = require('./js/util/layout-config');

// -----------------------------------------------------------------------------
const theme_default = 'ace/theme/chaos';

const log    = new Logger();
const server = new Server('http://localhost:8080');

let project = new Project();

let layout = new GoldenLayout(config);

(() => {
	layout.registerComponent('editor', function (container, state) {
		let element = container.getElement();

		// Create div
		element.html(`<div id=${state.id} class='editor'>${state.text}</div>`);

		// Assign Behavior
		container.on('open', () => {
			let id = state.id;
			let domElement = element.children(`#${id}`).get(0);

			const aceEditor = Ace.edit(domElement);

			if (project.editors.has(id)) {
				project.editors.get(id).restore(aceEditor);
			} else {
				let options = {
					mode: 'ace/mode/sca',
					theme: theme_default
				};

				let path = state.path;

				let editor = new Editor(path, container, aceEditor, options);
				project.editors.set(id, editor);
			}
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
			let path = state.path;

			const aceEditor = Ace.edit(domElement);
			let editor = new Editor(path, container, aceEditor, options);
			project.lexicons.set(id, editor);
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
			project.logView = editor;
			log.setLogView(editor);
		});
	});
	layout.registerComponent('project-tree', function (container, state) {
		container.getElement().html(`<div id='${state.id}' class='project'></div>`);
		container.on('open', () => {
			let tree = $(`#${state.id}`);
			tree.fancytree({
				// checkbox: true,
				source: [],
				activate: function (event, data) {} // TODO: what tho
			});
			project.projectTree = tree.fancytree('getTree');
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
				activate: function (event, data) {} // TODO: what tho
			});
			project.projectView = tree.fancytree('getTree');
		});
	});
})();

layout.init();

/**
 *
 * @param files
 * @returns {*[]}
 */
function projectFilesToNodes(files) {

	if (typeof files !== 'object' && !files.hasOwnProperty('length')) {
		log.error('Expected an array, but got ' + JSON.stringify(files));
		return [];
	}

	let scripts   = [];
	let lexiconsR = [];
	let lexiconsW = [];
	let model     = [];

	for (let i = 0; i < files.length; i++) {
		let file = files[i];

		let type = file.fileType;

		let node = {
			key:   Util.normPath(file.filePath),
			title: Util.trimPath(file.filePath)
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
		log.error('Unable to locate editor pane.')
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

			if (fileData === null || fileData === undefined) continue;

			let componentState = {
				id: Util.normPath(filePath),
				text: fileData,
				path: filePath
			};

			if (fileType === 'SCRIPT') {
				editorConfigs.push({
					isClosable: false,
					title: Util.trimPath(filePath),
					type: 'component',
					componentName: 'editor',
					componentState: componentState
				});
			} else if (fileType === 'LEXICON_READ') {
				lexiconRConfigs.push({
					isClosable: false,
					title: Util.trimPath(filePath),
					type: 'component',
					componentName: 'lexicon',
					componentState: componentState
				});
			} else if (fileType === 'LEXICON_WRITE') {
				lexiconWConfigs.push({
					isClosable: false,
					title: util.trimPath(filePath),
					type: 'component',
					componentName: 'lexicon',
					componentState: componentState
				});
			} else if (fileType === 'MODEL') {
				// TODO: assign the correct mode
				modelConfigs.push({
					isClosable: false,
					title: Util.trimPath(filePath),
					type: 'component',
					componentName: 'editor',
					componentState: componentState
				});
			}
		}

		let addConfig = (contentList, configs) => {
			if (configs.length > 0) {
				contentList.push({
					type: 'stack',
					content: configs
				});
			}
		};

		let contentList = [];
		addConfig(contentList, editorConfigs  );
		addConfig(contentList, modelConfigs   );
		addConfig(contentList, lexiconRConfigs);
		addConfig(contentList, lexiconWConfigs);

		contentItem.addChild({
			type: 'row',
			content: contentList
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

	project.projectFiles = files;

	let filetree = object.fileTree;
	let projectNodes = projectFilesToNodes(files);

	// Reload the project structure --------------------------------------------
	project.projectTree.reload(filetree);
	project.projectView.reload(projectNodes);

	// Clear and reload editor panes -------------------------------------------
	for (const lexicon of project.lexicons.values()) {
		lexicon.destroy();
	}
	project.lexicons.clear();

	// Clear and reload lexicon panes ------------------------------------------
	for (const editor of project.editors.values()) {
		editor.destroy();
	}
	project.editors.clear();

	// Populate the new editors ------------------------------------------------
	createPanels(files);
}

function assembleProjectFiles(project) {
	let editors = project.editors;
	let lexicons = project.lexicons;

	// TODO: for each editor of each type, create a new ProjectFile and
	//  send the list back to the server
	let list = [];

	for (const editor of editors.values()) {
		let path  = editor.filePath;
		let value = editor.aceEditor.getValue();
		list.push(new ProjectFile("SCRIPT", path, value));
	}

	for (const lexicon of lexicons.values()) {
		let path  = lexicon.filePath;
		let value = lexicon.aceEditor.getValue();
		list.push(new ProjectFile("LEXICON_READ", path, value));
	}

	return list;
}

function afterCompile(response) {

}

function afterExecute(response) {

}

const template = [{
	label: 'Project',
	submenu: [{
		label: 'Open',
		click: () => {
			dialog.showOpenDialog({
				multiSelections: false
			}, paths => {
				server.post('/loadNewProject', paths[0], loadNewProject);
			});
		}
	}, {
		label: 'Save',
		click: () => {
			let list = assembleProjectFiles(project);
			server.post('/saveProject', list);
		}
	}, {
		label: 'Save As',
		click: () => {
			dialog.showSaveDialog({}, paths => log.info(paths));
		}
	}, {
		role: 'quit'
	}]
}, {
	label: 'Script',
	submenu: [{
		label: 'Compile',
		click: () => {
			let list = assembleProjectFiles(project);
			server.post('/compileProject', list, afterCompile);
		}
	}, {
		label: 'Execute',
		click: () => {
			let list = assembleProjectFiles(project);
			server.post('/executeProject', list, afterExecute);
		}
	}]
}, {
	label: 'Analyze',
	submenu: []
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