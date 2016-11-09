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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.didelphis.io.DiskFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.soundchange.ErrorLogger;
import org.didelphis.soundchange.StandardScript;
import org.didelphis.toolbox.components.CodeEditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/3/2016
 */
public class Controller implements Initializable {
	
	private static final DecimalFormat FORMAT = new DecimalFormat("#0.00");
	private static final FileChooser.ExtensionFilter SCRIPT_EXTENSION_FILTER =
			new FileChooser.ExtensionFilter("Script Files", "*.rule", "*.*");

	@FXML
	private CodeEditor codeEditor;

	private final ErrorLogger errorLogger;
	
	private File scriptFile;
	
	public Controller() {
		errorLogger = new ErrorLogger();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	public void openFile(ActionEvent actionEvent) {
		FileChooser chooser = chooser("Open Script");
		File file = chooser.showOpenDialog(null);
		if (file != null) {
			try {
				scriptFile = file;
				String data = FileUtils.readFileToString(scriptFile);
				codeEditor.setCode(data);
			} catch (IOException e) {
				codeEditor.error(file.toString(), -1, e.toString());
			}
		}
	}
	
	public void saveAsFile(ActionEvent actionEvent) {
		FileChooser chooser = chooser("Save Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			try {
				String data = codeEditor.getCodeAndSnapshot();
				FileUtils.write(file, data);
			} catch (IOException e) {
				codeEditor.error(file.toString(), -1, e.toString());
			}
		}
	}

	public void newFile(ActionEvent actionEvent) {
		FileChooser chooser = chooser("New Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			try {
				String data = codeEditor.getCodeAndSnapshot();
				FileUtils.write(file, data);
			} catch (IOException e) {
				codeEditor.error(file.toString(), -1, e.toString());
			}
		}
	}

	public void saveFile(ActionEvent actionEvent) {
		if (scriptFile != null) {
			try {
				String data = codeEditor.getCodeAndSnapshot();
				FileUtils.write(scriptFile, data);
			} catch (IOException e) {
				codeEditor.error(scriptFile.toString(), -1, e.toString());
			}
		}
	}
	
	public void runScript() {
		String code = codeEditor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");
		codeEditor.clearLog();
		try {
			long start = System.nanoTime();
			String fileName = scriptFile.toString();
			StandardScript script = new StandardScript(fileName, code, handler, errorLogger);
			script.process();
			long end = System.nanoTime();
			double elapsed = (end-start) * 1.0E-6;
			if (errorLogger.isEmpty()) {
				codeEditor.info(fileName, " ran successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				generateErrorLog();
			}
		} catch (Exception e) {
			codeEditor.error(scriptFile.toString(), -1,
					"Unhandled error while running script! ", e.toString());
		}
	}

	public void compileScript() {
		String code = codeEditor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");
		codeEditor.clearLog();
		try {
			long start = System.nanoTime();
			String fileName = scriptFile.getAbsolutePath();
			StandardScript ignored = new StandardScript(fileName, code, handler, errorLogger);
			long end = System.nanoTime();
			double elapsed = (end-start) * 1.0E-6;
			if (errorLogger.isEmpty()) {
				codeEditor.info(fileName, " compiled successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				generateErrorLog();
			}
		} catch (Exception e) {
			codeEditor.error(scriptFile.toString(), -1,
					"Unhandled error while compiling script! ", e.toString());
		}	
	}
	

	private void generateErrorLog() {
		codeEditor.clearLog();
		for (ErrorLogger.Error error : errorLogger) {
			codeEditor.error(
					error.getScript(),
					error.getLine(),
					error.getData(),
					" Exception: ", error.getException().toString()
			);
		}
	}
	
	private FileChooser chooser(String title) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		chooser.getExtensionFilters().addAll(SCRIPT_EXTENSION_FILTER);
		if (scriptFile != null) {
			chooser.setInitialDirectory(scriptFile.getParentFile());
		} else {
			chooser.setInitialDirectory(new File("./"));
		}
		return chooser;
	}
}
