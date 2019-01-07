module.exports = class Editor {
	constructor(id, container, editor, options) {
		this.id = id;
		this.container = container;
		this.editor = editor;

		if (options) {
			if (options.mode) {
				editor.session.setMode(options.mode);
			}
			if (options.theme) {
				editor.setTheme(options.theme);
			}
		}
	}

	destroy() {
		this.editor.destroy();
		this.container.close();
	}
};