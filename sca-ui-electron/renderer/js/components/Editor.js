module.exports = class ProjectFile {
	constructor(filePath, container, editor, options) {

		this.filePath    = filePath;
		this.container   = container;
		this.aceEditor   = editor;
		this.session     = editor.session;

		if (options) {
			if (options.mode) {
				this.session.setMode(options.mode);
			}
			if (options.theme) {
				editor.setTheme(options.theme);
			}
		}

	}

	close() {
		// this.editor.destroy();
	}

	restore(editor) {
		this.aceEditor = editor;
		this.aceEditor.setSession(this.session);
	}

	setContainer(container) {
		this.container = container;
	}

	hasContainer() {
		return this.container !== undefined;
	}

	destroy() {
		this.aceEditor.destroy();
		if (this.container) {
			this.container.close();
		}
	}
};