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