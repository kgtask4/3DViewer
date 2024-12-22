package ru.vsu.cs.model;


import ru.vsu.cs.math.Vector2f;
import ru.vsu.cs.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Model {

    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector2f> textureVertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Polygon> polygons = new ArrayList<>();
}
