package nl.altindag.resizeme;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.altindag.resizeme.presentation.image.ImageView;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
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
