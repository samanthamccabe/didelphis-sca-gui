// Modules to control application life and create native browser window
const {
  app,
  BrowserWindow
} = require('electron')

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow;
let serverProcess;

function createWindow() {

  let platform = process.platform;

  let appUrl = 'http://localhost:8080';

  // Check operating system
  if (platform === 'win32') {
    serverProcess = require('child_process')
      .spawn('cmd.exe', ['/c', 'run.bat'], {
        cwd: app.getAppPath(),
        detached: true
      });
  } else {
    serverProcess = require('child_process')
      .spawn(app.getAppPath() + '/run.sh', {
        detached: true
      });
  }

  serverProcess.on('error', err => {
    console.log(err);
  });
  serverProcess.on('message', info => {
    console.log(info);
  });

  const openWindow = () => {
    // Create the browser window.
    mainWindow = new BrowserWindow({
      width: 800,
      height: 600
    })

    // and load the index.html of the app.
    mainWindow.loadURL(appUrl);

    // Open the DevTools.
    mainWindow.webContents.openDevTools()

    // Emitted when the window is closed.
    mainWindow.on('closed', function(e) {
      // Dereference the window object, usually you would store windows
      // in an array if your app supports multi windows, this is the time
      // when you should delete the corresponding element.
      mainWindow = null;

      if (serverProcess) {
        console.log(`Server running with pid ${serverProcess.pid}`);

        e.preventDefault();

        // kill Java executable
        const kill = require('tree-kill');
        kill(serverProcess.pid, function() {
          console.log('Server process killed');
          serverProcess = null;
        });
      }
    });
  };

  const startUp = function() {
    const requestPromise = require('minimal-request-promise');

    requestPromise.get(appUrl).then(function(response) {
      console.log('Server started!');
      openWindow();
    }, function(response) {
      console.log('Waiting for the server start...');

      setTimeout(function() {
        startUp();
      }, 1000);
    });
  };

  // Start Application
  startUp();
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow)

// Quit when all windows are closed.
app.on('window-all-closed', function() {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  // if (process.platform !== 'darwin') {
  app.quit()
    // }
});

app.on('activate', function() {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (mainWindow === null) {
    createWindow()
  }
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.