package ru.vsu.cs;

import ru.vsu.cs.math.Vector3f;
import ru.vsu.cs.model.Model;
import ru.vsu.cs.render_engine.Camera;

import java.util.ArrayList;
import java.util.List;

public class Scene {
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

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public void addModel(Model model) {
        this.models.add(model);
    }

    public void removeModel(Model model) {
        this.models.remove(model);
    }

    public Model getActiveModel() {
        return activeModel;
    }

    public void setActiveModel(Model activeModel) {
        this.activeModel = activeModel;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public boolean hasActiveModel() {
        return activeModel != null;
    }
    public boolean isActive() {
        return this.equals(activeModel);
    }

}

