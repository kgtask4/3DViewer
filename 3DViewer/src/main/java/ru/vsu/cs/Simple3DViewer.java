package ru.vsu.cs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Simple3DViewer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AnchorPane viewport = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gui.fxml")));

        Scene scene = new Scene(viewport);
        stage.setMinWidth(1600);
        stage.setMinHeight(900);
        viewport.prefWidthProperty().bind(scene.widthProperty());
        viewport.prefHeightProperty().bind(scene.heightProperty());

        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/ru/vsu/cs/icon.png")).toExternalForm());
        stage.getIcons().add(icon);
        stage.setTitle("3DViewer");
        ;
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}