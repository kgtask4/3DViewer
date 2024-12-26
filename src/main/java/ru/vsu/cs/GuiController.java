package ru.vsu.cs;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private ComboBox<String> modelSelector;

    private List<Model> models = new ArrayList<>();
    private Model activeModel;

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

            for (Model model : this.models) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, model, (int) width, (int) height);
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

        modelSelector.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index >= 0 && index < models.size()) {
                activeModel = models.get(index);
            }
        });
    }

    private void updateModelScale(ScaleAxis axis, boolean negate) {
        if (activeModel == null) {
            return;
        }

        float scale = negate ? 0.9f : 1.1f;

        for (Vector3f vertex : activeModel.vertices) {
            switch (axis) {
                case X_AXIS -> vertex.x *= scale;
                case Y_AXIS -> vertex.y *= scale;
                case Z_AXIS -> vertex.z *= scale;
            }
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
            case P:
                rotateModelAroundAxis(0, 1, 0, (float) Math.toRadians(15));
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
            Model model = ObjReader.read(fileContent);
            this.models.add(model);

            modelSelector.getItems().add("Model " + (models.size()));
            if (models.size() == 1) {
                modelSelector.getSelectionModel().select(0);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void rotateModelAroundAxis(float axisX, float axisY, float axisZ, float angle) {
        if (activeModel == null) return;

        float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        axisX /= axisLength;
        axisY /= axisLength;
        axisZ /= axisLength;

        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float[][] rotationMatrix = {
                {
                        cos + axisX * axisX * (1 - cos),
                        axisX * axisY * (1 - cos) - axisZ * sin,
                        axisX * axisZ * (1 - cos) + axisY * sin,
                        0
                },
                {
                        axisY * axisX * (1 - cos) + axisZ * sin,
                        cos + axisY * axisY * (1 - cos),
                        axisY * axisZ * (1 - cos) - axisX * sin,
                        0
                },
                {
                        axisZ * axisX * (1 - cos) - axisY * sin,
                        axisZ * axisY * (1 - cos) + axisX * sin,
                        cos + axisZ * axisZ * (1 - cos),
                        0
                },
                {0, 0, 0, 1}
        };

        for (Vector3f vertex : activeModel.vertices) {
            float[] vertexPos = {vertex.x, vertex.y, vertex.z, 1};
            float[] transformedPos = new float[4];

            for (int i = 0; i < 4; i++) {
                transformedPos[i] = 0;
                for (int j = 0; j < 4; j++) {
                    transformedPos[i] += rotationMatrix[i][j] * vertexPos[j];
                }
            }

            vertex.x = transformedPos[0];
            vertex.y = transformedPos[1];
            vertex.z = transformedPos[2];
        }

        RenderEngine.render(canvas.getGraphicsContext2D(), camera, activeModel, (int) canvas.getWidth(), (int) canvas.getHeight());
    }

    @FXML
    public void handleTeleportMenu(ActionEvent actionEvent) {
        Dialog<Vector3f> dialog = new Dialog<>();
        dialog.setTitle("Перемещение объекта");
        dialog.setHeaderText("Введите координаты для перемещения объекта");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField xField = new TextField();
        xField.setPromptText("X");
        TextField yField = new TextField();
        yField.setPromptText("Y");
        TextField zField = new TextField();
        zField.setPromptText("Z");

        gridPane.add(new Label("X:"), 0, 0);
        gridPane.add(xField, 1, 0);
        gridPane.add(new Label("Y:"), 0, 1);
        gridPane.add(yField, 1, 1);
        gridPane.add(new Label("Z:"), 0, 2);
        gridPane.add(zField, 1, 2);

        dialog.getDialogPane().setContent(gridPane);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                float x = Float.parseFloat(xField.getText());
                float y = Float.parseFloat(yField.getText());
                float z = Float.parseFloat(zField.getText());

                if (activeModel != null) {
                    float[][] translationMatrix = {
                            {1, 0, 0, x},
                            {0, 1, 0, y},
                            {0, 0, 1, z},
                            {0, 0, 0, 1}
                    };

                    for (Vector3f vertex : activeModel.vertices) {
                        float[] vertexPos = {vertex.x, vertex.y, vertex.z, 1};
                        float[] transformedPos = new float[4];

                        for (int i = 0; i < 4; i++) {
                            transformedPos[i] = 0;
                            for (int j = 0; j < 4; j++) {
                                transformedPos[i] += translationMatrix[i][j] * vertexPos[j];
                            }
                        }

                        vertex.x = transformedPos[0];
                        vertex.y = transformedPos[1];
                        vertex.z = transformedPos[2];
                    }

                    RenderEngine.render(canvas.getGraphicsContext2D(), camera, activeModel, (int) canvas.getWidth(), (int) canvas.getHeight());
                }
            } catch (NumberFormatException e) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Некорректный ввод");
                alert.setContentText("Убедитесь, что вы ввели числа в каждое из полей.");
                alert.showAndWait();
            }
        });

        dialog.showAndWait();
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
