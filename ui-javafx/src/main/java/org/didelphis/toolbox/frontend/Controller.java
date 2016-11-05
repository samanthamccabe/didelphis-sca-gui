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
import org.didelphis.toolbox.components.LogView;

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
	
	@FXML
	private CodeEditor codeEditor;
	
	@FXML
	private LogView logView;

	private final ErrorLogger errorLogger;
	
	private File scriptFile;
	
	public Controller() {
		errorLogger = new ErrorLogger();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	public void openFile(ActionEvent actionEvent) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open Script");
		chooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Script Files", "*.rule", "*.*")
		);
		if (scriptFile != null) {
			chooser.setInitialDirectory(scriptFile.getParentFile());
		} else {
			chooser.setInitialDirectory(new File("./"));
		}
		
		File file = chooser.showOpenDialog(null);
		if (file != null) {
			try {
				scriptFile = file;
				String data = FileUtils.readFileToString(scriptFile);
				codeEditor.setCode(data);
			} catch (IOException e) {
				logView.append(e.toString());
			}
		}
	}
	
	public void saveAsFile(ActionEvent actionEvent) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save Script");
		chooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Text Files", "*.dcr")
		);
		if (scriptFile != null) {
			chooser.setInitialDirectory(scriptFile.getParentFile());
		} else {
			chooser.setInitialDirectory(new File("./"));
		}

		File file = chooser.showSaveDialog(null);
		if (file != null) {
			try {
				String data = codeEditor.getCodeAndSnapshot();
				FileUtils.write(file, data);
			} catch (IOException e) {
				logView.append(e.toString());
			}
		}
	}
	
	public void runScript() {
		String code = codeEditor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");
		logView.clear();
		try {
			long start = System.nanoTime();
			String fileName = scriptFile.toString();
			StandardScript script = new StandardScript(fileName, code, handler, errorLogger);
			script.process();
			long end = System.nanoTime();
			double elapsed = (end-start) * 1.0E-6;
			if (errorLogger.isEmpty()) {
				logView.append("Script \"", fileName, "\" ran successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				logView.clear();
				for (ErrorLogger.Error error : errorLogger) {
					logView.append(
							error.getLine(),
							" \"", error.getScript(),
							"\" ", error.getData(),
							" Exception: ", error.getException().toString()
					);
				}
			}
		} catch (Exception e) {
			logView.append("Unhandled error while running script \"",
					scriptFile.toString(), "\" Cause: ", e.toString());
		}
	}

	public void compileScript() {
		String code = codeEditor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");
		logView.clear();
		try {
			long start = System.nanoTime();
			String fileName = scriptFile.getAbsolutePath();
			StandardScript ignored = new StandardScript(fileName, code, handler, errorLogger);
			long end = System.nanoTime();
			double elapsed = (end-start) * 1.0E-6;
			logView.append("Script \"", fileName,
					"\" compiled successfully in ", FORMAT.format(elapsed),
					" ms");
		} catch (Exception e) {
			logView.append("Unhandled error while compiling script \"",
					scriptFile.toString(), "\" Cause: ", e.toString());
		}	
	}

	public void newFile(ActionEvent actionEvent) {
		// TODO:
	}

	public void closeFile(ActionEvent actionEvent) {
		// TODO:
	}

	public void saveFile(ActionEvent actionEvent) {
		if (scriptFile != null) {
			try {
				String data = codeEditor.getCodeAndSnapshot();
				FileUtils.write(scriptFile, data);
			} catch (IOException e) {
				logView.append("Error while saving! Exception cited: ",
						e.toString());
			}
		}
	}
}
