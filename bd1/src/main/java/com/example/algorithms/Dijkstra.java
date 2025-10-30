package com.example.algorithms;

import com.example.graph.Edge;
import com.example.graph.Graph;
import com.example.graph.Node;

import java.util.*;

public class Dijkstra {
    private final Graph graph;

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    public List<Node> findPath(long startId, long endId) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previous = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(
                Comparator.comparingDouble(nd -> nd.distance)
        );
        distances.put(startId, 0.0);
        pq.offer(new NodeDistance(startId, 0.0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            long currentId = current.nodeId;
            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);

            if (currentId == endId) {
                return reconstructPath(previous, startId, endId);
            }

            List<Edge> edges = graph.getEdges(currentId);
            for (Edge edge : edges) {
                long neighborId = edge.getTo().getId();

                if (visited.contains(neighborId)) {
                    continue;
                }

                double newDistance = distances.getOrDefault(currentId, Double.MAX_VALUE)
                        + edge.getWeight();

                if (newDistance < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                    distances.put(neighborId, newDistance);
                    previous.put(neighborId, currentId);
                    pq.offer(new NodeDistance(neighborId, newDistance));
                }
            }
        }


        return null;
    }

    private List<Node> reconstructPath(Map<Long, Long> previous, long startId, long endId) {
        List<Node> path = new ArrayList<>();
        long current = endId;

        while (current != startId) {
            path.add(graph.getNode(current));

            if (!previous.containsKey(current)) {
                return null;
            }

            current = previous.get(current);
        }
        path.add(graph.getNode(startId));

        Collections.reverse(path);
        return path;
    }

    public double calculatePathDistance(List<Node> path) {
        if (path == null || path.size() < 2) return 0.0;

        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);

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

    public PathResult findPathWithStats(long startId, long endId) {
        long startTime = System.nanoTime();

        List<Node> path = findPath(startId, endId);

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0;

        PathResult result = new PathResult();
        result.path = path;
        result.executionTime = executionTime;
        result.algorithmName = "Dijkstra";

        if (path != null) {
            result.pathLength = path.size();
            result.totalDistance = calculatePathDistance(path);
        }

        return result;
    }

    private static class NodeDistance {
        long nodeId;
        double distance;

        NodeDistance(long nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }

    public static class PathResult {
        public List<Node> path;
        public double executionTime;
        public int pathLength;
        public double totalDistance;
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