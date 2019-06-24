module.exports = class Util {

	static trimPath(path) {
		return path.replace(/.*\/(.*?)$/, '$1');
	}

	static normPath(path) {
		return path.replace(/\W+/g, '-');
	}

};