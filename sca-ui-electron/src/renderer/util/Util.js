module.exports = {
	trimPath: (path) => {
		return path.replace(/.*\/(.*?)$/, '$1');
	},
	normPath: (path) => {
		return path.replace(/\W+/g, '-');
	}
};