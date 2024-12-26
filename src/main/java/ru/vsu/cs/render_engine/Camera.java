package ru.vsu.cs.render_engine;

import ru.vsu.cs.math.Matrix4f;
import ru.vsu.cs.math.Vector3f;

public class Camera {

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = new Vector3f(position);
        this.target = new Vector3f(target);
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector3f position) {
        this.position = new Vector3f(position);
    }

    public void setTarget(final Vector3f target) {
        this.target = new Vector3f(target);
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getTarget() {
        return new Vector3f(target);
    }

    public void movePosition(final Vector3f translation) {
        this.position.addInPlace(translation);
    }

    public void moveTarget(final Vector3f translation) {
        this.target.addInPlace(translation);
    }

    public Matrix4f getViewMatrix() {
        return Matrix4f.lookAt(position, target, new Vector3f(0f,1f,0f));
    }

    public Matrix4f getProjectionMatrix() {
        return Matrix4f.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
}
