package ru.vsu.cs.deleting;

import ru.vsu.cs.model.Model;
import ru.vsu.cs.model.Polygon;

import java.util.*;

public class PolygonDeleting {
    public static void deletePolygon(Model model, List<Integer> polygonIndices) {
        if (model == null || polygonIndices == null || polygonIndices.isEmpty()) {
            return;
        }

        for (Integer index : polygonIndices) {
            if (index < 0 || index >= model.polygons.size()) {
                throw new PolygonException("Polygon index " + index + " does not exist");
            }
        }

        Set<Integer> potentialUnusedVertices = new HashSet<>();


        for (int i = polygonIndices.size() - 1; i >= 0; i--) {
            int index = polygonIndices.get(i);
            Polygon polygon = model.polygons.get(index);


            potentialUnusedVertices.addAll(polygon.getVertexIndices());


            model.polygons.remove(index);
        }


        Iterator<Integer> iterator = potentialUnusedVertices.iterator();
        while (iterator.hasNext()) {
            Integer vertexIndex = iterator.next();

            boolean isUsed = model.polygons.stream().anyMatch(poly -> poly.getVertexIndices().contains(vertexIndex));

            if (isUsed) {iterator.remove();}
        }

        List<Integer> unusedVertices = new ArrayList<>(potentialUnusedVertices);
        Collections.sort(unusedVertices, Collections.reverseOrder());

        for (Integer index : unusedVertices) {
            model.vertices.remove((int)index);
        }

        adjustVertexIndicesInPolygons(model, unusedVertices);
    }

    private static void adjustVertexIndicesInPolygons(Model model, List<Integer> removedVertices) {
        if (removedVertices.isEmpty()) return;

        for (Polygon polygon : model.polygons) {
            List<Integer> indices = polygon.getVertexIndices();

            for (int i = 0; i < indices.size(); i++) {
                int currentIndex = indices.get(i);
                int adjustment = (int) removedVertices.stream().filter(removed -> removed < currentIndex).count();

                indices.set(i, currentIndex - adjustment);
            }
        }
    }
}
