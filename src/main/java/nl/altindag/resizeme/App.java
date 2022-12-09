/*
 * Copyright 2017 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.altindag.resizeme;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.altindag.resizeme.presentation.image.ImageView;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        ImageView imageView = new ImageView();

        Scene scene = new Scene(imageView.getView());
        final String uri = getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(uri);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Resize Me!");
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
