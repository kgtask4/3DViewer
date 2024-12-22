package ru.vsu.cs;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.vsu.cs.math.Vector3f;
import ru.vsu.cs.model.Model;
import ru.vsu.cs.obj_reader.ObjReader;
import ru.vsu.cs.render_engine.Camera;
import ru.vsu.cs.render_engine.RenderEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

enum ScaleAxis {
    X_AXIS,
    Y_AXIS,
    Z_AXIS
}

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            (float) Math.toRadians(60),
            1,
            0.01F,
            1000F
    );

    private Timeline timeline;

    private double lastMouseX;
    private double lastMouseY;
    private boolean draggingLeft = false;
    private boolean draggingMiddle = false;

    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private float distance = 100f;

    private void updateCameraPosition() {
        Vector3f target = camera.getTarget();
        float cx = (float) (target.x + distance * Math.cos(pitch) * Math.sin(yaw));
        float cy = (float) (target.y + distance * Math.sin(pitch));
        float cz = (float) (target.z + distance * Math.cos(pitch) * Math.cos(yaw));
        camera.setPosition(new Vector3f(cx, cy, cz));
    }

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((e1, e2, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((e1, e2, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

            if (height > 0) {
                camera.setAspectRatio((float) (width / height));
            }

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        canvas.setOnMousePressed(e -> onMousePressed(e));
        canvas.setOnMouseDragged(e -> onMouseDragged(e));
        canvas.setOnMouseReleased(e -> onMouseReleased(e));
        canvas.setOnScroll(e -> onScroll(e));

        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> onKeyPressed(e));
            }
        });
    }

    private void updateModelScale(ScaleAxis axis, boolean negate) {
        float scale = (negate ? 0.9f : 1.1f);
        if (mesh != null) {
            for (Vector3f vertex : mesh.vertices) {
                switch(axis) {
                    case X_AXIS -> vertex.x *= scale;
                    case Y_AXIS -> vertex.y *= scale;
                    case Z_AXIS -> vertex.z *= scale;
                }
            }

            RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) canvas.getWidth(), (int) canvas.getHeight());
        }
    }

    private void onKeyPressed(KeyEvent e) {
        boolean shiftPressed = e.isShiftDown();

        switch (e.getCode()) {
            case W:
                distance = Math.max(0.1f, distance - TRANSLATION);
                updateCameraPosition();
                break;
            case S:
                distance += TRANSLATION;
                updateCameraPosition();
                break;
            case A:
                yaw -= 0.05f;
                updateCameraPosition();
                break;
            case D:
                yaw += 0.05f;
                updateCameraPosition();
                break;
            case R:
                pitch += 0.05f;
                updateCameraPosition();
                break;
            case F:
                pitch -= 0.05f;
                updateCameraPosition();
                break;
            case X:
                updateModelScale(ScaleAxis.X_AXIS, shiftPressed);
                break;
            case Y:
                updateModelScale(ScaleAxis.Y_AXIS, shiftPressed);
                break;
            case Z:
                updateModelScale(ScaleAxis.Z_AXIS, shiftPressed);
                break;
            default:
                break;
        }
    }

    private void onMousePressed(MouseEvent e) {
        lastMouseX = e.getSceneX();
        lastMouseY = e.getSceneY();
        if (e.getButton() == MouseButton.PRIMARY) {
            draggingLeft = true;
        } else if (e.getButton() == MouseButton.MIDDLE || e.getButton() == MouseButton.SECONDARY) {
            draggingMiddle = true;
        }
    }

    private void onMouseDragged(MouseEvent e) {
        double dx = e.getSceneX() - lastMouseX;
        double dy = e.getSceneY() - lastMouseY;
        lastMouseX = e.getSceneX();
        lastMouseY = e.getSceneY();

        if (draggingLeft) {
            yaw += dx * 0.01f;
            pitch -= dy * 0.01f;

            if (pitch > Math.PI / 2 - 0.1) pitch = (float) (Math.PI / 2 - 0.1);
            if (pitch < -Math.PI / 2 + 0.1) pitch = (float) (-Math.PI / 2 + 0.1);

            updateCameraPosition();
        } else if (draggingMiddle) {
            Vector3f forward = new Vector3f(
                    (float) (Math.sin(yaw) * Math.cos(pitch)),
                    (float) (Math.sin(pitch)),
                    (float) (Math.cos(yaw) * Math.cos(pitch))
            );
            forward.normalize();

            Vector3f right = new Vector3f(
                    (float) Math.sin(yaw - Math.PI / 2),
                    0,
                    (float) Math.cos(yaw - Math.PI / 2)
            );
            right.normalize();

            Vector3f up = new Vector3f(0, 1, 0);

            float panSpeed = 0.1f * distance / 100f;
            Vector3f target = camera.getTarget();
            Vector3f newTarget = target.add(right.mul(-(float) dx * panSpeed)).add(up.mul((float) dy * panSpeed));
            camera.setTarget(newTarget);
            updateCameraPosition();
        }
    }

    private void onMouseReleased(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            draggingLeft = false;
        } else if (e.getButton() == MouseButton.MIDDLE || e.getButton() == MouseButton.SECONDARY) {
            draggingMiddle = false;
        }
    }

    private void onScroll(ScrollEvent e) {
        double delta = e.getDeltaY();
        distance *= (delta > 0) ? 0.9f : 1.1f;
        if (distance < 0.1f) distance = 0.1f;
        updateCameraPosition();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        distance = Math.max(0.1f, distance - TRANSLATION);
        updateCameraPosition();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        distance += TRANSLATION;
        updateCameraPosition();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        yaw -= 0.05f;
        updateCameraPosition();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        yaw += 0.05f;
        updateCameraPosition();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        pitch += 0.05f;
        updateCameraPosition();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        pitch -= 0.05f;
        updateCameraPosition();
    }
}
