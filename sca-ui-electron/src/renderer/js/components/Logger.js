module.exports = class Logger {
    
    getLogView() {
        return this.logView;
    }
    
    setLogView(logView) {
        this.logView = logView;
    }
    
    info(message) {
        const logView = this.logView;
        if (logView) {
            let value = logView.getValue();
            value += '[INFO] ' + message + '\n';
            logView.setValue(value);
        } else {
            console.log('[INFO] UI Console unavailable --- ', message);
        }
    }

    warn(message) {
        const logView = this.logView;
        if (logView) {
            let value = logView.getValue();
            value += '[WARN] ' + message + '\n';
            logView.setValue(value);
        } else {
            console.log('[WARN] UI Console unavailable --- ', message);
        }
    }

    error(message) {
        const logView = this.logView;
        if (logView) {
            let value = logView.getValue();
            value += '[ERROR] ' + message + '\n';
            logView.setValue(value);
        } else {
            console.log('[ERROR] UI Console unavailable --- ', message);
        }
    }
};