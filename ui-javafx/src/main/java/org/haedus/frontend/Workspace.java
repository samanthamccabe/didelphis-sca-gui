package org.haedus.frontend;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/22/2016
 */
public class Workspace {
	
	private final Map<String, Project> projects;
	
	public Workspace() {
		projects = new LinkedHashMap<>();
	}
	
	public Project getProject(String id) {
		return projects.get(id);
	}
	
	public void addProject(String id, Project project) {
		projects.put(id, project);
	}
	
	public Project deleteProject(String id) {
		return projects.remove(id);
	}
}
