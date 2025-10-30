package com.example.algorithms;

import com.example.graph.Edge;
import com.example.graph.Graph;
import com.example.graph.Node;

import java.util.*;

public class DFS {
    private final Graph graph;

    public DFS(Graph graph) {
        this.graph = graph;
    }

    public List<Node> findPath(long startId, long endId) {
        Set<Long> visited = new HashSet<>();
        List<Node> path = new ArrayList<>();

        if (dfsHelper(startId, endId, visited, path)) {
            return path;
        }

        return null;
    }

    private boolean dfsHelper(long currentId, long endId, Set<Long> visited, List<Node> path) {
        visited.add(currentId);
        path.add(graph.getNode(currentId));

        // Зорилтот цэгт хүрсэн эсэхийг шалгах
        if (currentId == endId) {
            return true;
        }

        // Хөршүүдийг шалгах
        List<Edge> edges = graph.getEdges(currentId);
        for (Edge edge : edges) {
            long neighborId = edge.getTo().getId();

            if (!visited.contains(neighborId)) {
                if (dfsHelper(neighborId, endId, visited, path)) {
                    return true;
                }
            }
        }

        // Энэ замаар очих боломжгүй - буцах
        path.remove(path.size() - 1);
        return false;
    }

    public List<List<Node>> findAllPaths(long startId, long endId, int maxPaths) {
        List<List<Node>> allPaths = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        List<Node> currentPath = new ArrayList<>();

        findAllPathsHelper(startId, endId, visited, currentPath, allPaths, maxPaths);

        return allPaths;
    }

    private void findAllPathsHelper(long currentId, long endId, Set<Long> visited,
                                    List<Node> currentPath, List<List<Node>> allPaths, int maxPaths) {

        if (allPaths.size() >= maxPaths) {
            return;
        }

        visited.add(currentId);
        currentPath.add(graph.getNode(currentId));

        if (currentId == endId) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            // Хөршүүдийг шалгах
            List<Edge> edges = graph.getEdges(currentId);
            for (Edge edge : edges) {
                long neighborId = edge.getTo().getId();

                if (!visited.contains(neighborId)) {
                    findAllPathsHelper(neighborId, endId, visited, currentPath, allPaths, maxPaths);
                }
            }
        }

        // Backtrack
        currentPath.remove(currentPath.size() - 1);
        visited.remove(currentId);
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
        result.algorithmName = "DFS";

        if (path != null) {
            result.pathLength = path.size();
            result.totalDistance = calculatePathDistance(path);
        }

        return result;
    }

    public AllPathsResult findAllPathsWithStats(long startId, long endId, int maxPaths) {
        long startTime = System.nanoTime();

        List<List<Node>> allPaths = findAllPaths(startId, endId, maxPaths);

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0;

        AllPathsResult result = new AllPathsResult();
        result.allPaths = allPaths;
        result.executionTime = executionTime;
        result.algorithmName = "DFS (All Paths)";

        return result;
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
            }
        }
    }

    public static class AllPathsResult {
        public List<List<Node>> allPaths;
        public double executionTime;
        public String algorithmName;

        public void printResult() {
            System.out.println("\n=== " + algorithmName + " үр дүн ===");
            System.out.println("Олсон замуудын тоо: " + allPaths.size());
            System.out.println("Гүйцэтгэх хугацаа: " + String.format("%.2f", executionTime) + " мс");

            if (!allPaths.isEmpty()) {
                System.out.println("\nЭхний 3 зам:");
                for (int i = 0; i < Math.min(3, allPaths.size()); i++) {
                    List<Node> path = allPaths.get(i);
                    System.out.println("  Зам " + (i + 1) + ": " + path.size() + " цэг");
                }
            }
        }
    }
}