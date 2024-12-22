package ru.vsu.cs.math;

public class Point2f {
    public float x, y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2f(Point2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    public boolean equals(Point2f other) {
        final float eps = 1e-7f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps;
    }
}
