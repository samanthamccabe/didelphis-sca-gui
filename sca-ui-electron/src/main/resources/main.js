const electron = require('electron');
// Module to control application life.
const app = electron.app;
// Module to create native browser window.
const BrowserWindow = electron.BrowserWindow;

const path = require('path');
const url = require('url');

const STATUS_ENDPOINT = "http://localhost:8080/status";
const KILL_ENDPOINT = "http://localhost:8080/kill";

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow;

platform = process.platform;

// Check operating system
if (platform === 'win32') {
  serverProcess = require('child_process')
      .spawn('cmd.exe', ['/c', 'java', '-jar', 'sca-server.jar'],
          {cwd: app.getAppPath()});
} else {
  serverProcess = require('child_process')
      .spawn(app.getAppPath() + 'java -jar sca-server.jar');
}

function createWindow () {
  // Create the browser window.
  mainWindow = new BrowserWindow({
    width:  800,
    height: 600,
    webPreferences: {
      nodeIntegration: false
    }
  });

  mainWindow.setMenu(null);

  // and load the index.html of the app.
  mainWindow.loadURL(url.format({
    pathname: path.join(__dirname, 'index.html'),
    protocol: 'file:',
    slashes: true
  }));

  // Open the DevTools.
  // mainWindow.webContents.openDevTools()

  // Emitted when the window is closed.
  mainWindow.on('closed', function () {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    mainWindow = null
  });
}

const startUp = function () {
  const requestPromise = require('minimal-request-promise');

  requestPromise.get(STATUS_ENDPOINT).then(function (response) {
    console.log('Server started!');
    createWindow();
  }, function (response) {
    console.log('Waiting for the server start...');

    setTimeout(function () {
      startUp();
    }, 1000);
  });
};

const shutDown = function () {
  require('minimal-request-promise')
      .get(KILL_ENDPOINT)
      .then(function (response) {
        console.log('Server Killed!', response);
      }, function (response) {
        console.log('Waiting for server shutdown...');
        setTimeout(function () {
          shutDown();
        }, 1000);
      });
};

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
// app.on('ready', );

// Quit when all windows are closed.
app.on('window-all-closed', function () {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    shutDown();
    app.quit()
  }
});

app.on('activate', function () {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (mainWindow === null) {
    createWindow()
  }
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.

startUp();