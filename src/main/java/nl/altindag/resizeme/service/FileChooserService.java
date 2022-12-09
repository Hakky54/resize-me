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
package nl.altindag.resizeme.service;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

public class FileChooserService {

    FileChooser fileChooser;
    private static SimpleObjectProperty<File> lastKnownLocation = new SimpleObjectProperty<>();
    private static Supplier<Stage> stageSupplier = Stage::new;

    @PostConstruct
    private void init() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("JPEG image", "*.jpg"));
        fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownLocation);
    }

    public Optional<File> getFile() {
        var file = Optional.ofNullable(fileChooser.showOpenDialog(stageSupplier.get()));
        file.ifPresent(this::setLastKnowLocation);
        return file;
    }

    public Optional<File> saveFile() {
        var file = Optional.ofNullable(fileChooser.showSaveDialog(stageSupplier.get()));
        file.ifPresent(this::setLastKnowLocation);
        return file;
    }

    private void setLastKnowLocation(File file) {
        lastKnownLocation.setValue(file.getParentFile());
    }

}
