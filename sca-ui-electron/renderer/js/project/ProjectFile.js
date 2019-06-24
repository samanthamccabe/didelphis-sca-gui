module.exports = class ProjectFile {
	constructor(type, path, data) {
		this.fileType = type;
		this.filePath = path;
		this.fileData = data;
	}
};