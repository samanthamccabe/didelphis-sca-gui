let logView  = null;
let fileTree = null;

const editors = {};

const CONTENT_JSON = "application/json; charset=UTF-8";

const ENDPOINT = "http://localhost:8080";

const LOG = {
	info: (message) => {
		if (logView) {
			let value = logView.getValue();
			value += "[INFO] " + message;
			logView.setValue(value);
		} else {
			console.log("[INFO] UI Console unavailable --- ", message);
		}
	},
	warn: (message) => {
		if (logView) {
			let value = logView.getValue();
			value += "[WARN] " + message;
			logView.setValue(value);
		} else {
			console.log("[WARN] UI Console unavailable --- ", message);
		}
	},
	error: (message) => {
		if (logView) {
			let value = logView.getValue();
			value += "[ERROR] " + message;
			logView.setValue(value);
		} else {
			console.log("[ERROR] UI Console unavailable --- ", message);
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
		let editor = ace.edit(state.id);
		editor.setTheme("ace/theme/crimson_editor");
		editor.session.setMode("ace/mode/didelphissca");
		container.editor = editor;
		editors[state.id] = editor;
	});
});

myLayout.registerComponent('project', function (container, state) {
	container.getElement()
		.html('<div id="tree"></div>');
	container.on('open', () => {
		let $tree = $("#tree");
		
		$tree.fancytree({
			// checkbox: true,
			source: [],
			activate: function(event, data){
				$("#status").text("Activate: " + data.node);
			}
		});
		fileTree = $tree.fancytree("getTree");
	});
});

myLayout.registerComponent('logView', function (container, state) {
	container.getElement()
		.html('<div id=' + state.id + ' class=editor>' + state.text + '</div>');
	container.on('open', () => {
		let editor = ace.edit(state.id);
		editor.setTheme("ace/theme/crimson_editor");
		editor.session.setMode("ace/mode/didelphislog");
		container.editor = editor;
		logView = editor;
	});
});

myLayout.init();

$(document).foundation();

function kill() {
	$.ajax({
		url: ENDPOINT + "/kill",
		method: "GET",
		success: (response) => {
			console.log(response);
		},
		error: (response) => {
			console.log(response);
		}
	})
}

$("#openFile").on("change", function () {
	let file = this.files[0];

	$.ajax({
		url: ENDPOINT + "/loadNewProject",
		method: "POST",
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