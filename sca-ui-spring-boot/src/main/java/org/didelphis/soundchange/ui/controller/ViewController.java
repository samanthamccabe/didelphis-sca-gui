package org.didelphis.soundchange.ui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.didelphis.io.DiskFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.soundchange.ErrorLogger;
import org.didelphis.soundchange.parser.ScriptParser;
import org.didelphis.soundchange.ui.FileTreeUtil;
import org.didelphis.utilities.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;

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
			Collection<String> paths = scriptParser.getPaths();
			paths.add(mainPath);
			FileTreeUtil.Node node = FileTreeUtil.parsePaths(paths);
			return OBJECT_MAPPER.writeValueAsString(node);
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
