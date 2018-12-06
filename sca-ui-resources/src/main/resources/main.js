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
			componentName: 'logview',
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
	});
});

myLayout.registerComponent('project', function (container, state) {
	container.getElement()
		.html('<div id="tree"></div>');
	container.on('open', () => {
		$("#tree").fancytree({
			checkbox: true,
			source: [],
			activate: function(event, data){
				$("#status").text("Activate: " + data.node);
			}
		});
	});
});

myLayout.registerComponent('logview', function (container, state) {
	container.getElement()
		.html('<div id=' + state.id + ' class=editor>' + state.text + '</div>');
	container.on('open', () => {
		let editor = ace.edit(state.id);
		editor.setTheme("ace/theme/crimson_editor");
		editor.session.setMode("ace/mode/didelphislog");
		container.editor = editor;
	});
});

myLayout.init();

$(document).foundation();

(function () {
	$("#openFile").on("change", function () {
		let file = this.files[0];
		let fileReader = new FileReader();
		fileReader.onload = function () {
			let content = this.result;
			$.ajax({
				url: "http://localhost:8080/loadNewProject",
				method: "POST",
				contentType: "application/json; charset=UTF-8",
				data: JSON.stringify({
					data: content,
					path: file.name
				}),
				success: (response) => {
					console.log(response);
				},
				error: (response) => {
					console.log(response);
				}
			})
		};
		fileReader.readAsText(file);
	});
})();