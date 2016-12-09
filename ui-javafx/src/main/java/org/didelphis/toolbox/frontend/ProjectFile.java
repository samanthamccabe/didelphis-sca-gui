/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.toolbox.frontend;

import org.didelphis.soundchange.SoundChangeScript;
import org.didelphis.soundchange.command.io.AbstractIoCommand;
import org.didelphis.soundchange.command.io.ScriptExecuteCommand;
import org.didelphis.soundchange.command.io.ScriptImportCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/22/2016
 */
public class ProjectFile {

	private final String id;
	private final File file;
	private final List<ProjectFile> children;

	public ProjectFile(String id, File file) {
		this.id = id;
		this.file = file;
		children = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public File getFile() {
		return file;
	}

	public List<ProjectFile> getChildren() {
		return children;
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public void collectIds(Collection<ProjectFile> collection) {
		for (ProjectFile child : children) {
			collection.add(child);
			if (child.hasChildren()) {
				child.collectIds(collection);
			}
		}
	}
	
	public void clear() {
		children.clear();
	}
	
	public void populate(SoundChangeScript script) {
		String parent = file.getParent();
		for (Runnable runnable : script.getCommands()) {
			if (runnable instanceof ScriptImportCommand) {
				addChild(parent, (AbstractIoCommand) runnable);
			} else
			if (runnable instanceof ScriptExecuteCommand) {
				addChild(parent, (AbstractIoCommand) runnable);
			}
		}
	}

	private void addChild(String parent, AbstractIoCommand runnable) {
		String path = runnable.getPath();
		File file = new File(parent + "/" + path);
		String id = file.getName();
		children.add(new ProjectFile(id, file));
	}
}
