module nl.altindag.resizeme {
    requires java.image.scaling;
    requires javafx.swing;
    requires java.annotation;
    requires javafx.fxml;
    requires javafx.controls;
    requires afterburner.fx;
    requires org.apache.commons.io;

    opens nl.altindag.resizeme.presentation.image to javafx.fxml, afterburner.fx;
    opens nl.altindag.resizeme.service to afterburner.fx;

    exports nl.altindag.resizeme to javafx.graphics;
    exports nl.altindag.resizeme.service to afterburner.fx;
}