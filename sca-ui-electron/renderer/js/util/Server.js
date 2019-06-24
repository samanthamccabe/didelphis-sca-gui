const CONTENT_JSON = 'application/json; charset=UTF-8';

module.exports = class Server {

	constructor(endpoint, logger) {
		this.endpoint = endpoint;
		this.logger   = logger;
	}

	post(path, data, success) {
		let request = typeof data === 'object' ? JSON.stringify(data) : data;
		$.ajax({
			url: this.endpoint + path,
			method: 'POST',
			contentType: CONTENT_JSON,
			data: request,
			success: response => {
				if (success) success(response)
			},
			error: response => {
				if (this.logger) this.logger.error(response)
			}
		})
	}
};