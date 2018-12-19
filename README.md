# Didelphis Grammatekton

An IDE-like user interface for the Didelphis sound-change-applier.

This project is in early development, but as now has a stable architecture
linking the front- and back-end components, so that meaningful develpment will
be possible.

### Project Goals:
 - Syntax highlighting for SCA scripts and feature models
 - Configurable UI with resizeable and drag-and-droppable panels
 - Error checking for scripts and models
 - Compile and run scripts
 - Manage simple lexicons
 - Rule dependency analysis (feed-and-bleed)
 - Manage project files
 - Debug tools to determine what rules change an input word, and identify rules 
   causing unexpected results

### Architecture
 - Java servlet running in Spring Boot
 - Electron front-end
 - Golden Layout windows
 - Ace code editors
 
 ### Notes:
  - It's potentially possible for the server to keep running after the Electron
    layer has been shut down; normally when the application quits, it will issue
    a `GET` command to the  server's `/kill` endpoint. If you need to manually
    kill the process, look for a process with `sca-server.jar` in the command.
    Originally we attempted to use the `tree-kill` module, but `node` seemed
    unable to shut down the process, even while running the same commands in a
    terminal was fine. This suggests a permissions problem, possible.
  - If the server is not shut down when the application is closed, and then the
    application is re-opened, it can re-acquire the server and will not create
    a new instance.