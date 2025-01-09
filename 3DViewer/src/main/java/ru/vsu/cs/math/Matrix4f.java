package ru.vsu.cs.math;

public class Matrix4f {
    public float[] m;

    public Matrix4f() {
        m = new float[16];
        setIdentity();
    }

    public Matrix4f(float[] values) {
        if (values.length != 16) throw new IllegalArgumentException("Matrix4f requires 16 values.");
        m = new float[16];
        System.arraycopy(values, 0, m, 0, 16);
    }

    public Matrix4f(Matrix4f other) {
        m = new float[16];
        System.arraycopy(other.m, 0, this.m, 0, 16);
    }

    public void setIdentity() {
        for (int i = 0; i < 16; i++) {
            m[i] = (i % 5 == 0) ? 1.0f : 0.0f;
        }
    }

    private int index(int row, int col) {
        return col * 4 + row;
    }

    public float get(int row, int col) {
        return m[index(row, col)];
    }

    public void set(int row, int col, float val) {
        m[index(row, col)] = val;
    }

    public Matrix4f mul(Matrix4f other) {
        Matrix4f result = new Matrix4f();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                float sum = 0.0f;
                for (int k = 0; k < 4; k++) {
                    sum += this.get(row, k) * other.get(k, col);
                }
                result.set(row, col, sum);
            }
        }
        return result;
    }

    public Vector3f mul(Vector3f v) {
        float x = get(0,0)*v.x + get(0,1)*v.y + get(0,2)*v.z + get(0,3)*1.0f;
        float y = get(1,0)*v.x + get(1,1)*v.y + get(1,2)*v.z + get(1,3)*1.0f;
        float z = get(2,0)*v.x + get(2,1)*v.y + get(2,2)*v.z + get(2,3)*1.0f;
        float w = get(3,0)*v.x + get(3,1)*v.y + get(3,2)*v.z + get(3,3)*1.0f;
        if (Math.abs(w) > 1e-9f) {
            x /= w; y /= w; z /= w;
        }
        return new Vector3f(x, y, z);
    }

    public static Matrix4f translation(float tx, float ty, float tz) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        mat.set(0,3, tx);
        mat.set(1,3, ty);
        mat.set(2,3, tz);
        return mat;
    }

    public static Matrix4f scale(float sx, float sy, float sz) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        mat.set(0,0, sx);
        mat.set(1,1, sy);
        mat.set(2,2, sz);
        return mat;
    }

    public static Matrix4f rotationX(float angle) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        mat.set(1,1,c);
        mat.set(1,2,-s);
        mat.set(2,1,s);
        mat.set(2,2,c);
        return mat;
    }

    public static Matrix4f rotationY(float angle) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        mat.set(0,0,c);
        mat.set(0,2,s);
        mat.set(2,0,-s);
        mat.set(2,2,c);
        return mat;
    }

    public static Matrix4f rotationZ(float angle) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        mat.set(0,0,c);
        mat.set(0,1,-s);
        mat.set(1,0,s);
        mat.set(1,1,c);
        return mat;
    }

    public static Matrix4f perspective(float fov, float aspectRatio, float nearPlane, float farPlane) {
        Matrix4f result = new Matrix4f();
        result.setIdentity();
        float f = (float)(1.0 / Math.tan(fov * 0.5));
        result.set(0,0, f / aspectRatio);
        result.set(1,1, f);
        result.set(2,2,(farPlane + nearPlane)/(nearPlane - farPlane));
        result.set(2,3,(2*farPlane*nearPlane)/(nearPlane - farPlane));
        result.set(3,2,-1.0f);
        result.set(3,3,0.0f);
        return result;
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f zAxis = eye.sub(target);
        zAxis.normalize();

        Vector3f xAxis = up.cross(zAxis);
        xAxis.normalize();

        Vector3f yAxis = zAxis.cross(xAxis);
        yAxis.normalize();

        Matrix4f view = new Matrix4f();
        view.setIdentity();

        view.set(0,0, xAxis.x); view.set(0,1, yAxis.x); view.set(0,2, zAxis.x);
        view.set(1,0, xAxis.y); view.set(1,1, yAxis.y); view.set(1,2, zAxis.y);
        view.set(2,0, xAxis.z); view.set(2,1, yAxis.z); view.set(2,2, zAxis.z);

        view.set(0,3, -xAxis.dot(eye));
        view.set(1,3, -yAxis.dot(eye));
        view.set(2,3, -zAxis.dot(eye));

        return view;
    }
}
