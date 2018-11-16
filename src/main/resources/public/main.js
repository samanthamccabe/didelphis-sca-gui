let config = {
  content: [{
    type: 'column',
    content: [{
      type: 'stack',
      content: [{
        type: 'component',
        componentName: 'editor',
        componentState: {
          id: 'Editor1',
          text: '% Editor 1'
        }
      }]
    }, {
      title: "Message Log",
      type: 'component',
      componentName: 'logview',
      componentState: {
        id: 'ConsoleLog',
        text: '[INFO] LOG START'
      },
      isClosable: false
    }]
  }]
};

let myLayout = new GoldenLayout(config);

myLayout.registerComponent('editor', function(container, state) {
  container.getElement()
    .html('<div id=' + state.id + ' class=editor>' + state.text + '</div>');
  container.on('open', () => {
    let editor = ace.edit(state.id);
    editor.setTheme("ace/theme/crimson_editor");
    editor.session.setMode("ace/mode/didelphissca");
    container.editor = editor;
  });
});

myLayout.registerComponent('logview', function(container, state) {
  container.getElement()
    .html('<div id=' + state.id + ' class=editor>' + state.text + '</div>');
  container.on('open', () => {
    let editor = ace.edit(state.id);
    editor.setTheme("ace/theme/crimson_editor");
    editor.session.setMode("ace/mode/didelphislog");
    container.editor = editor;
  });
});

myLayout.init();