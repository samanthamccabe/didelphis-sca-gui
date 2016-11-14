package org.didelphis.toolbox.components;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by samantha on 11/13/16.
 */
public class LexiconViewer extends StackPane {

	private final Map<String, Map<String, List<String>>> data;

	private final TabPane tabPane;

	public LexiconViewer() {
		data = new LinkedHashMap<>();
		tabPane = new TabPane();
		getChildren().add(tabPane);
	}

	public void setContent(Map<String, Map<String, List<String>>> map) {
		clear();
		data.putAll(map);
		for (String key : map.keySet()) {
			View view = new View(key);
			tabPane.getTabs().add(view);
		}

	}

	public void clear() {
		data.clear();
		getChildren().clear();
	}

	private class View extends Tab {
		private final TableView<String> table;

		private View(String name) {
			super(name);
			table = new TableView<>();
			getChildren().add(table);
		}
	}
}
