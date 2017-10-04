package nl.altindag.resizeme.service;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Optional;

public class FileChooserService {

    FileChooser fileChooser;
    private static SimpleObjectProperty<File> lastKnownLocation = new SimpleObjectProperty<>();

    @PostConstruct
    private void init() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JPEG image", "*.jpg")
                , new ExtensionFilter("PNG image", "*.png"));
        fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownLocation);
    }

    public Optional<File> get(Stage stage) {
        Optional<File> file = Optional.ofNullable(fileChooser.showOpenDialog(stage));
        file.ifPresent(this::setLastKnowLocation);
        return file;
    }

    public Optional<File> save(Stage stage) {
        Optional<File> file = Optional.ofNullable(fileChooser.showSaveDialog(stage));
        file.ifPresent(this::setLastKnowLocation);
        return file;
    }

    private void setLastKnowLocation(File file) {
        lastKnownLocation.setValue(file.getParentFile());
    }

}
