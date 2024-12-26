package ru.vsu.cs.render_engine;

import ru.vsu.cs.math.Matrix4f;
import ru.vsu.cs.math.Point2f;
import ru.vsu.cs.math.Vector3f;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        return new Matrix4f();
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return Matrix4f.lookAt(eye, target, new Vector3f(0f, 1f, 0f));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        return Matrix4f.lookAt(eye, target, up);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        return Matrix4f.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        return matrix.mul(vertex);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
