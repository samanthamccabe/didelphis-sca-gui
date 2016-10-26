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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

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
//			Parent root = FXMLLoader.load(resource);
//			primaryStage.setTitle("Didelphis SCA Workbench");
//			primaryStage.setScene(new Scene(root));
//			primaryStage.show();

			WebView webView = new WebView();
			webView.getEngine().load("http://localhost:8080/index.html");
			primaryStage.setScene(new Scene(webView));
			primaryStage.setTitle("Didelphis SCA");
			primaryStage.show();
		}
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		Server server = getServer(8080);
		try {
			server.start();
			launch(args);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Server getServer(int port) {
		Server server = new Server(port);
		// Creating the first web application context
		String path = Main.class.getResource("/").toString();
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setResourceBase(path);
		context.setDescriptor(path + "/WEB-INF/web.xml");
		context.setParentLoaderPriority(true);
		server.setHandler(context);
		return server;
	}
}
