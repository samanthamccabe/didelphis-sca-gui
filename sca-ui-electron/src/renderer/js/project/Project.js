module.exports = class Project {

	constructor() {
		this.logView      = null;
		this.projectTree  = null;
		this.projectView = null;

		this.editors  = new Map();
		this.lexicons = new Map();

		this.projectFiles = null;
	}

};