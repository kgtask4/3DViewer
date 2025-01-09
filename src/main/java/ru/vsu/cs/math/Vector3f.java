package ru.vsu.cs.math;

public class Vector3f {
    public float x, y, z;

    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public void addInPlace(Vector3f other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public Vector3f sub(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public void subInPlace(Vector3f other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
    }

    public Vector3f mul(float scalar) {
        return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public float dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3f cross(Vector3f other) {
        return new Vector3f(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public float length() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        float len = length();
        if (len > 1e-9) {
            this.x /= len;
            this.y /= len;
            this.z /= len;
        }
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
    public boolean equals(Vector3f other) {
        final float eps = 1e-7f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public String toString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }
}
