package com.example.algorithms;

import com.example.graph.Edge;
import com.example.graph.Graph;
import com.example.graph.Node;

import java.util.*;

/**
 * BFS (Breadth-First Search) - Өргөнөөр эхэлсэн хайлт
 * Хамгийн цөөн алхам (хамгийн цөөн уулзвар) олоход тохиромжтой
 */
public class BFS {
    private final Graph graph;

    public BFS(Graph graph) {
        this.graph = graph;
    }

    /**
     * BFS ашиглан зам олох
     * @param startId Эхлэх цэгийн ID
     * @param endId Дуусах цэгийн ID
     * @return Зам (цэгүүдийн жагсаалт), олдохгүй бол null
     */
    public List<Node> findPath(long startId, long endId) {
        if (startId == endId) {
            return Arrays.asList(graph.getNode(startId));
        }

        // Queue ашиглан хайлт
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        Map<Long, Long> parent = new HashMap<>();

        queue.offer(startId);
        visited.add(startId);

        while (!queue.isEmpty()) {
            long currentId = queue.poll();

            // Зорилтот цэгт хүрсэн эсэхийг шалгах
            if (currentId == endId) {
                return reconstructPath(parent, startId, endId);
            }

            // Хөршүүдийг шалгах
            List<Edge> edges = graph.getEdges(currentId);
            for (Edge edge : edges) {
                long neighborId = edge.getTo().getId();

                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    parent.put(neighborId, currentId);
                    queue.offer(neighborId);
                }
            }
        }

        // Зам олдсонгүй
        return null;
    }

    /**
     * Замыг сэргээн босгох
     */
    private List<Node> reconstructPath(Map<Long, Long> parent, long startId, long endId) {
        List<Node> path = new ArrayList<>();
        long current = endId;

        while (current != startId) {
            path.add(graph.getNode(current));
            current = parent.get(current);
        }
        path.add(graph.getNode(startId));

        Collections.reverse(path);
        return path;
    }

    /**
     * Замын нийт уртыг тооцоолох
     */
    public double calculatePathDistance(List<Node> path) {
        if (path == null || path.size() < 2) return 0.0;

        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);

            // Ирмэгийн жинг олох
            List<Edge> edges = graph.getEdges(current.getId());
            for (Edge edge : edges) {
                if (edge.getTo().getId() == next.getId()) {
                    totalDistance += edge.getWeight();
                    break;
                }
            }
        }

        return totalDistance;
    }

    /**
     * Алгоритмын статистик
     */
    public PathResult findPathWithStats(long startId, long endId) {
        long startTime = System.nanoTime();

        List<Node> path = findPath(startId, endId);

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0; // миллисекунд

        PathResult result = new PathResult();
        result.path = path;
        result.executionTime = executionTime;
        result.algorithmName = "BFS";

        if (path != null) {
            result.pathLength = path.size();
            result.totalDistance = calculatePathDistance(path);
        }

        return result;
    }

    /**
     * Замын үр дүнгийн класс
     */
    public static class PathResult {
        public List<Node> path;
        public double executionTime; // миллисекунд
        public int pathLength; // цэгүүдийн тоо
        public double totalDistance; // нийт зай (км)
        public String algorithmName;

        public void printResult() {
            System.out.println("\n=== " + algorithmName + " үр дүн ===");
            if (path == null) {
                System.out.println("Зам олдсонгүй!");
            } else {
                System.out.println("Цэгүүдийн тоо: " + pathLength);
                System.out.println("Нийт зай: " + String.format("%.2f", totalDistance) + " км");
                System.out.println("Гүйцэтгэх хугацаа: " + String.format("%.2f", executionTime) + " мс");

                System.out.println("\nЗамын эхний 5 цэг:");
                for (int i = 0; i < Math.min(5, path.size()); i++) {
                    Node node = path.get(i);
                    System.out.println("  " + (i + 1) + ". Node " + node.getId() +
                            " [" + node.getLatitude() + ", " + node.getLongitude() + "]");
                }
            }
        }
    }
}