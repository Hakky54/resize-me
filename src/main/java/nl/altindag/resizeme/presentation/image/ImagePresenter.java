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
import javafx.stage.Stage;
import nl.altindag.resizeme.service.FileChooserService;
import nl.altindag.resizeme.service.ImageService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

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

    private SimpleStringProperty width = new SimpleStringProperty();
    private SimpleStringProperty height = new SimpleStringProperty();
    private SimpleStringProperty percentage = new SimpleStringProperty();

    private String pattern = "###.#";
    private DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

    @Inject
    private FileChooserService fileChooserService;
    @Inject
    private ImageService imageService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        decimalFormat.applyPattern(pattern);
        Platform.runLater(() -> imageChooser.requestFocus());

        widthField.textProperty().bindBidirectional(width);
        heightField.textProperty().bindBidirectional(height);
        percentageField.textProperty().bindBidirectional(percentage);

        widthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (imageView.getImage() != null && !newValue.isEmpty() && !heightField.isFocused() && !percentageField.isFocused()) {
                height.set(String.valueOf(decimalFormat.format(imageService.calculateHeight(imageView.getImage(), Double.valueOf(newValue)))));
                percentage.set(String.valueOf(decimalFormat.format(Double.valueOf(height.getValue()) / imageView.getImage().getHeight() * 100)));
            }
        });

        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (imageView.getImage() != null && !newValue.isEmpty() && !widthField.isFocused() && !percentageField.isFocused()) {
                width.set(String.valueOf(decimalFormat.format(imageService.calculateWidth(imageView.getImage(), Double.valueOf(newValue)))));
                percentage.set(String.valueOf(decimalFormat.format(Double.valueOf(width.getValue()) / imageView.getImage().getWidth() * 100)));
            }
        });

        percentageField.textProperty().addListener((observable, oldValue, newValue) -> {
            double pct = newValue.isEmpty() ? 0 : Double.valueOf(newValue);
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

        fileChooserService.save(new Stage())
                .ifPresent(path -> {
                    try {
                        IOUtils.copy(imageService.compress(imageView.getImage(), Double.valueOf(width.getValue())), new FileOutputStream(path));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
    }

    @FXML
    public void selectImage(ActionEvent event) {
        fileChooserService.get(new Stage())
                .ifPresent(image -> {
                    imageView.setImage(new Image(image.toURI().toString()));
                    calculateResolution();
                });
    }

    @FXML
    public void resetImage(ActionEvent event) {
        if (imageView.getImage() == null) {
            width.set(null);
            height.set(null);
            percentage.set(null);
        } else {
            width.set(String.valueOf(imageView.getImage().getWidth()));
            height.set(String.valueOf(imageView.getImage().getHeight()));
            percentage.set("100.0");
        }
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
                .filter(file -> {
                    String extention = FilenameUtils.getExtension(file.getName()).toLowerCase();
                    if (extention.equals("jpg") || extention.equals("png")) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .findFirst()
                .ifPresent(file -> {
                    imageView.setImage(new Image(file.toURI().toString()));
                    calculateResolution();
                });

    }

    private void calculateResolution() {
        width.set(decimalFormat.format(Double.valueOf(imageView.getImage().getWidth())));
        height.set(decimalFormat.format(Double.valueOf(imageView.getImage().getHeight())));
        percentage.set("100.0");
    }
}
