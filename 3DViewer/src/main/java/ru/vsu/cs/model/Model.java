package ru.vsu.cs.model;

import ru.vsu.cs.math.Vector2f;
import ru.vsu.cs.math.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Model {

    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector2f> textureVertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Polygon> polygons = new ArrayList<>();

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Vector2f> getTextureVertices() {
        return textureVertices;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Model model = (Model) obj;

        return Objects.equals(vertices, model.vertices) &&
                Objects.equals(textureVertices, model.textureVertices) &&
                Objects.equals(normals, model.normals) &&
                Objects.equals(polygons, model.polygons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, textureVertices, normals, polygons);
    }
}

