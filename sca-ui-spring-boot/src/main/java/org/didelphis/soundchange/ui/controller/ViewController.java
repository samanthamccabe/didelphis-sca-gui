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

package org.didelphis.soundchange.ui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.didelphis.io.DiskFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.soundchange.ErrorLogger;
import org.didelphis.soundchange.parser.FileType;
import org.didelphis.soundchange.parser.ProjectFile;
import org.didelphis.soundchange.parser.ScriptParser;
import org.didelphis.soundchange.ui.FileTreeUtil;
import org.didelphis.utilities.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ViewController {

	private static final Logger LOG = Logger.create(ViewController.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@RequestMapping (
			value    = "/loadNewProject",
			method   = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	public String compile(@RequestBody String mainPath) {
		FeatureType<?> type = IntegerFeature.INSTANCE;
		FileHandler handler = new DiskFileHandler("UTF-8");
		ErrorLogger errorLogger = new ErrorLogger();
		try {
			String data = handler.read(mainPath);
			ScriptParser<?> scriptParser = new ScriptParser<>(
					mainPath,
					type,
					data,
					handler,
					errorLogger
			);
			scriptParser.parse();

			ProjectFile mainFile = new ProjectFile();
			mainFile.setFileType(FileType.SCRIPT);
			mainFile.setFileData(data);
			mainFile.setFilePath(mainPath);

			List<ProjectFile> files = new ArrayList<>();
			files.add(mainFile);
			files.addAll(scriptParser.getProjectFiles());

			List<String> paths = files.stream()
					.map(file -> file.getFilePath())
					.collect(Collectors.toList());

			FileTreeUtil.Node node = FileTreeUtil.parsePaths(paths);

			Map<String, Object> map = new HashMap<>();
			map.put("fileTree", node);
			map.put("projectFiles", files);

			return OBJECT_MAPPER.writeValueAsString(map);
		} catch (IOException e) {
			LOG.error("Failed to read from {}", mainPath, e);
		} catch (ParseException e) {
			LOG.error("Failed to compile from file {}", mainPath, e);
		}

		try {
			return OBJECT_MAPPER.writeValueAsString(errorLogger);
		} catch (JsonProcessingException e) {
			LOG.error("Failed to serialize error logger {}", errorLogger.toString(), e);
		}
		return "FAILED";
	}

	@RequestMapping (
			value    = "/status",
			method   = RequestMethod.GET
	)
	public String status() {
		return "Running";
	}

	@RequestMapping (
			value    = "/kill",
			method   = RequestMethod.GET
	)
	public String kill() {
		System.exit(0);
		return "Server Killed";
	}
}
