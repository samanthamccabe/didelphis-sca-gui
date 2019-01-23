const components = {
	projectTree: () => {
		return {
			title: 'Project Tree',
			type: 'component',
			componentName: 'project-tree',
			componentState: {
				id: 'project-tree'
			},
			isClosable: false
		}
	},
	projectFiles: () => {
		return {
			title: 'Project Files',
			type: 'component',
			componentName: 'project-files',
			componentState: {
				id: 'project-files'
			},
			isClosable: false
		}
	},
	editor: (id) => {
		return {
			title: 'Editor ' + id,
			type: 'component',
			componentName: 'editor',
			componentState: {
				id: id,
				text: '% Editor ' + id
			}
		}
	},
	logView: (height) => {
		return {
			title: "Message Log",
			type: 'component',
			componentName: 'log-view',
			componentState: {
				id: 'ConsoleLog',
				text: '[INFO] LOG START\n'
			},
			isClosable: false,
			height: height
		}
	},
	lexicon: (name, data) => {
		return {
			title: name,
			type: 'component',
			componentName: 'lexicon',
			componentState: {
				id: 'Lexicon' + name,
				text: data
			}
		}
	}
};

module.exports = {
	settings:{
		hasHeaders: true,
		constrainDragToContainer: true,
		reorderEnabled: true,
		selectionEnabled: false,
		popoutWholeStack: false,
		blockedPopoutsThrowError: false,
		closePopoutsOnUnload: true,
		showPopoutIcon: false,
		showMaximiseIcon: true,
		showCloseIcon: false
	},
	labels: {
		close: 'close',
		maximise: 'maximise',
		minimise: 'minimise',
		popout: 'open in new window'
	},
	content: [{
		type: 'row',
		content: [{
			id: 'project',
			isClosable: false,
			type: 'column',
			width: 15,
			content: [
				components.projectTree(),
				components.projectFiles(),
			]
		}, {
			type: 'column',
			content: [
				{
					id: 'editors',
					isClosable: false,
					type: 'row',
					content: []
				},
				components.logView(20)
			]
		}]
	}]
};