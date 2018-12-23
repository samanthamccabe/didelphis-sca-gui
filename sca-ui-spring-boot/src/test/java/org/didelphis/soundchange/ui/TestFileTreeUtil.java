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
