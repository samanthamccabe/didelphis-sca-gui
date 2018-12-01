package org.didelphis.soundchange.ui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.didelphis.io.DiskFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.soundchange.ErrorLogger;
import org.didelphis.soundchange.parser.ScriptParser;
import org.didelphis.utilities.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ViewController {

	private static final Logger LOG = Logger.create(ViewController.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@RequestMapping (
			value    = "/loadNewProject",
			method   = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	public String compile(@RequestBody String object) {
		FeatureType<?> type = IntegerFeature.INSTANCE;
		FileHandler handler = new DiskFileHandler("UTF-8");

		ErrorLogger errorLogger = new ErrorLogger();

		try {
			JsonNode jsonNode = OBJECT_MAPPER.readTree(object);

			String path = jsonNode.get("path").asText();
			String data = jsonNode.get("data").asText();

			ScriptParser<?> scriptParser = new ScriptParser<>(
					path,
					type,
					data,
					handler,
					errorLogger
			);

			scriptParser.parse();

			return OBJECT_MAPPER.writeValueAsString(scriptParser.getPaths());
		} catch (IOException e) {
			LOG.error("Failed to parse request {}", object, e);
		} catch (ParseException e) {
			LOG.error("Failed to compile from request {}", object, e);
		}

		try {
			return OBJECT_MAPPER.writeValueAsString(errorLogger);
		} catch (JsonProcessingException e) {
			LOG.error("Failed to serialize error logger {}", errorLogger.toString(), e);
		}
		return "FAILED";
	}

	@RequestMapping (
			value    = "/test",
			method   = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE
	)
	public String test() {
		return "This is a test";
	}
}
