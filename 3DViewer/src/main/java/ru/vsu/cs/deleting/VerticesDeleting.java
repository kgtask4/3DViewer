package ru.vsu.cs.deleting;

import ru.vsu.cs.math.Vector3f;
import ru.vsu.cs.model.Model;
import ru.vsu.cs.model.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VerticesDeleting {

    public static void vertexDeleting(Model model, List<Integer> vertexIndices) {

        List<Polygon> polygons = model.getPolygons();
        List<Vector3f> vertices = model.getVertices();


        for (int vertexIndex : vertexIndices) {
            if (vertexIndex >= 0 && vertexIndex < vertices.size()) {
                Iterator<Polygon> polygonIterator = polygons.iterator();
                while (polygonIterator.hasNext()) {
                    Polygon polygon = polygonIterator.next();
                    if (polygon.containsVertex(vertexIndex)) {
                        polygonIterator.remove();
                    } else{
                        List<Integer> indices = polygon.getVertexIndices();

                        for (int i = 0; i < indices.size(); i++) {
                            if (indices.get(i) > vertexIndex) {
                                indices.set(i, indices.get(i) - 1);
                            }
                        }
                    }
                }
                vertices.remove(vertexIndex);
            } else {
                throw new IndexOutOfBoundsException("Vertex index out of bounds: " + vertexIndex);
            }
        }
    }

}
