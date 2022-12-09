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
package nl.altindag.resizeme.presentation.image;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import nl.altindag.resizeme.service.FileChooserService;
import nl.altindag.resizeme.service.ImageService;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ImagePresenter implements Initializable {

    @FXML
    private Button imageChooser;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField percentageField;
    @FXML
    private ImageView imageView;

    private final SimpleStringProperty width = new SimpleStringProperty();
    private final SimpleStringProperty height = new SimpleStringProperty();
    private final SimpleStringProperty percentage = new SimpleStringProperty();

    private final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

    @Inject
    private FileChooserService fileChooserService;
    @Inject
    private ImageService imageService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        decimalFormat.applyPattern("###.#");
        Platform.runLater(() -> imageChooser.requestFocus());

        widthField.textProperty().bindBidirectional(width);
        heightField.textProperty().bindBidirectional(height);
        percentageField.textProperty().bindBidirectional(percentage);

        widthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (imageView.getImage() != null && !newValue.isEmpty() && !heightField.isFocused() && !percentageField.isFocused()) {
                height.set(String.valueOf(decimalFormat.format(imageService.calculateHeight(imageView.getImage(), Double.parseDouble(newValue)))));
                percentage.set(String.valueOf(decimalFormat.format(Double.parseDouble(height.getValue()) / imageView.getImage().getHeight() * 100)));
            }
        });

        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (imageView.getImage() != null && !newValue.isEmpty() && !widthField.isFocused() && !percentageField.isFocused()) {
                width.set(String.valueOf(decimalFormat.format(imageService.calculateWidth(imageView.getImage(), Double.parseDouble(newValue)))));
                percentage.set(String.valueOf(decimalFormat.format(Double.parseDouble(width.getValue()) / imageView.getImage().getWidth() * 100)));
            }
        });

        percentageField.textProperty().addListener((observable, oldValue, newValue) -> {
            double pct = newValue.isEmpty() ? 0 : Double.parseDouble(newValue);
            if (imageView.getImage() != null && pct > 0 && !widthField.isFocused() && !heightField.isFocused()) {
                width.set(String.valueOf(decimalFormat.format(imageService.calculateWidth(imageView.getImage(), imageView.getImage().getHeight() * (pct / 100)))));
                height.set(String.valueOf(decimalFormat.format(imageService.calculateHeight(imageView.getImage(), imageView.getImage().getWidth() * (pct / 100)))));
            }
        });
    }

    @FXML
    public void convertImage(ActionEvent event) {
        if (width.isEmpty().get()) {
            return;
        }

        fileChooserService.saveFile()
                .ifPresent(this::writeFile);
    }

    @FXML
    public void selectImage(ActionEvent event) {
        fileChooserService.getFile()
                .ifPresent(this::displayImage);
    }

    @FXML
    public void resetImage(ActionEvent event) {
        if (imageView.getImage() == null) {
            width.set(null);
            height.set(null);
            percentage.set(null);
            return;
        }

        width.set(String.valueOf(imageView.getImage().getWidth()));
        height.set(String.valueOf(imageView.getImage().getHeight()));
        percentage.set("100.0");
    }

    @FXML
    public void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleDropped(DragEvent event) {
        event.getDragboard().getFiles().stream()
                .filter(file -> isValidImage(file, extension -> extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")))
                .findFirst()
                .ifPresent(this::displayImage);
    }

    private void writeFile(File file) {
        try(var image = imageService.compress(imageView.getImage(), Double.parseDouble(width.getValue()))) {
            Files.copy(image, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void displayImage(File file) {
        imageView.setImage(new Image(file.toURI().toString()));
        calculateResolution();
    }

    private static boolean isValidImage(File file, Predicate<String> extensionFilter) {
        var extension = FilenameUtils.getExtension(file.getName().toLowerCase());
        return extensionFilter.test(extension);
    }

    private void calculateResolution() {
        width.set(decimalFormat.format(Double.valueOf(imageView.getImage().getWidth())));
        height.set(decimalFormat.format(Double.valueOf(imageView.getImage().getHeight())));
        percentage.set("100.0");
    }
}
