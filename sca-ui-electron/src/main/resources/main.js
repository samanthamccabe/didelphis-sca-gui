/******************************************************************************
 * Didelphis Grammatekton - an graphical development environment for          *
 * constructed language development and sound-change rule application         *
 *                                                                            *
 * Copyright (C) 2016 Samantha F McCabe                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

const electron = require('electron');
const promise  = require('minimal-request-promise');
const path     = require('path');
const url      = require('url');

const { exec, spawn } = require('child_process');

// Module to control application life.
const app = electron.app;

// Module to create native browser window.
const BrowserWindow = electron.BrowserWindow;

const HOST = "localhost";
const PORT = "8080";

const BASE_ENDPOINT   = "http://" + HOST + ":" + PORT + "/";
const STATUS_ENDPOINT = BASE_ENDPOINT + "status";
const KILL_ENDPOINT   = BASE_ENDPOINT + "kill";

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow;

// Used to check operating system
platform = process.platform;

// Part of the server startup configuration
const appPath = app.getAppPath();
const serverOptions = {
  cwd: appPath,
  detached: false
};

function createWindow () {
  // Create the browser window.
  mainWindow = new BrowserWindow({
    width:  800,
    height: 600,
    webPreferences: {
      nodeIntegration: false,
      devTools: true
    }
  });

  // mainWindow.setMenu(null);

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
  promise.get(STATUS_ENDPOINT).then(function (response) {
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
  promise.get(KILL_ENDPOINT).then(function (response) {
        console.log('Server Killed!', response);
      }, function (response) {
        console.log('Waiting for server shutdown...');
        setTimeout(function () {
          shutDown();
        }, 1000);
      });
};

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

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', () => {
  // initialize Java backend

  promise.get(STATUS_ENDPOINT).then(function (response) {
    console.log('Server already running with status' + response + '; continuing normally.');
  }, function () {
    if (platform === 'win32') {
      spawn('cmd.exe', ['/c', 'java', '-jar', 'sca-server.jar'], serverOptions);
    } else {
      exec('java -jar sca-server.jar', serverOptions);
    }
  });
  startUp();
});