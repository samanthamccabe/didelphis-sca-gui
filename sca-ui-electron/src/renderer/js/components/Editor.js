module.exports = class Editor {
	constructor(container, editor, options) {

		this.container = container;
		this.editor    = editor;
		this.session   = editor.session;

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
		this.editor = editor;
		this.editor.setSession(this.session);
	}

	setContainer(container) {
		this.container = container;
	}

	hasContainer() {
		return this.container !== undefined;
	}

	destroy() {
		this.editor.destroy();
		if (this.container) {
			this.container.close();
		}
	}
};