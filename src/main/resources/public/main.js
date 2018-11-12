var config = {
    content: [{
        type: 'row',
        content: [
            {
                type:'component',
                componentName: 'example',
                componentState: { 
                    id: 'Component1',
                    text: 'Component 1'
                }
            },
            {
                type:'component',
                componentName: 'example',
                componentState: {
                    id: 'Component2',
                    text: 'Component 2' 
                }
            },
            {
                type:'component',
                componentName: 'example',
                componentState: {
                    id: 'Component3',
                    text: 'Component 3'
                }
            }
        ]
    }]
};

var myLayout = new GoldenLayout( config );

myLayout.registerComponent('example', function (container, state) {
    container.getElement()
        .html('<div id=' + state.id + ' class=editor>' + state.text + '</div>');
    
    container.on('open', function () {
        var editor = ace.edit(state.id);
        editor.setTheme("ace/theme/vibrant_ink");
        editor.session.setMode("ace/mode/java");  
        container.editor = editor;
    });
});

myLayout.init();