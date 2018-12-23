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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.didelphis.soundchange.ui.FileTreeUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Class {@code TestFileTreeUtil}
 *
 */
class TestFileTreeUtil {
	
	@Test
	void testParse() {
		List<String> list = new ArrayList<>();
		
		list.add("x/y/z/file1");
		list.add("x/y/file2");
		list.add("x/file3");
		list.add("file4");

		Node root = parsePaths(list);
		
		assertEquals(2, root.getChildren().size());
	}

	@Test
	void testParseJSON() throws JsonProcessingException {
		List<String> list = new ArrayList<>();

		list.add("x/y/z/file1");
		list.add("x/y/file2");
		list.add("x/file3");
		list.add("file4");

		Node root = parsePaths(list);

		ObjectMapper objectMapper = new ObjectMapper();

		String string = objectMapper.writeValueAsString(root);
		
		assertFalse(string.isEmpty());
	}
}
