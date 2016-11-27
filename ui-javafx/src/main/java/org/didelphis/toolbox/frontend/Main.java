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

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 8/17/2015
 */
public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		URL resource = getClass().getClassLoader().getResource("main.fxml");
		if (resource != null) {
			FXMLLoader loader = new FXMLLoader(resource);

			Parent root = loader.load();
			primaryStage.setTitle("Didelphis SCA Workbench");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		}
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		launch(args);
	}
}
