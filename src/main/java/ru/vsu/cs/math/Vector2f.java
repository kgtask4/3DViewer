package ru.vsu.cs.math;

public class Vector2f {
    public float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f() {
        this(0.0f, 0.0f);
    }

    public Vector2f(Vector2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(this.x + other.x, this.y + other.y);
    }

    public Vector2f sub(Vector2f other) {
        return new Vector2f(this.x - other.x, this.y - other.y);
    }

    public Vector2f mul(float scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }

    public float dot(Vector2f other) {
        return this.x * other.x + this.y * other.y;
    }
}
