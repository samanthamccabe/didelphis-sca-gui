package org.haedus.frontend;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/22/2016
 */
public class Project {
	
	// ID
	private final String id;
	// FILE
	private final File file;
	
	// LEXICONS
	
	// SCRIPT
	private final String script;
	
	public Project(File scriptFile) throws IOException {
		file = scriptFile;
		id = scriptFile.getName();
		script = FileUtils.readFileToString(file, "UTF-8");
	}
	
}
