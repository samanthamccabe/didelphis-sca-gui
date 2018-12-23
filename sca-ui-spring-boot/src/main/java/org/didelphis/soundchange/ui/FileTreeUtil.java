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

package org.didelphis.soundchange.ui;

import lombok.Value;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility Class {@code FileTreeUtil}
 * <p>
 * Provides functionality for converting a list of files paths into a FancyTree
 * JSON object
 */
@UtilityClass
public class FileTreeUtil {

	private final Pattern DIVIDER = Pattern.compile("[/\\\\]+");

	public static Node parsePaths(Collection<String> paths) {
		Node root = new Node("/", "/", new HashSet<>());
		Map<String, Node> nodes = new HashMap<>();
		for (String path : paths) {
			parsePath(splitPath(path), 0, root, nodes);
		}
		return findCommonParent(root);
	}
	
	private Node findCommonParent(Node node) {
		while (true) {
			if (node.getChildren().size() == 1) {
				node = node.getChildren().iterator().next();
			} else {
				return node;
			}
		}
	}

	private static void parsePath(
			List<String> path,
			int index,
			Node parent,
			Map<String, Node> nodes
	) {
		if (index >= path.size()) return;
		List<String> list = new ArrayList<>(path.subList(0, index));
		String folderName = path.get(index);
		list.add(folderName);

		String assembledPath = index == path.size() - 1
				? assemblePath(list, "")
				: assemblePath(list, "/");

		Node node;
		if (nodes.containsKey(assembledPath)) {
			node = nodes.get(assembledPath);
		} else {
			node = index == path.size() - 1 ? new Node(
					assembledPath,
					folderName
			) : new Node(assembledPath, folderName, new HashSet<>());
			parent.add(node);
			nodes.put(assembledPath, node);
		}
		parsePath(path, index + 1, node, nodes);
	}

	private String assemblePath(List<String> path, String suffix) {
		return path.stream().collect(Collectors.joining("/", "", suffix));
	}

	private List<String> splitPath(String path) {
		return new LinkedList<>(Arrays.asList(DIVIDER.split(path)));
	}

	@Value
	public static final class Node {
		String key;
		String title;
		Set<Node> children;
		boolean folder;

		public Node(String key, String title) {
			this.key = key;
			this.title = title;

			children = Collections.emptySet();
			folder = false;
		}

		public Node(String key, String title, Set<Node> children) {
			this.key = key;
			this.title = title;
			this.children = children;

			folder = true;
		}

		public void add(Node child) {
			children.add(child);
		}
	}
}
