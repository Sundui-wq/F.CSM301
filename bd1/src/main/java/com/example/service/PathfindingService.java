package com.example.service;

import com.example.algorithms.BFS;
import com.example.algorithms.DFS;
import com.example.algorithms.Dijkstra;
import com.example.graph.Graph;
import com.example.graph.Node;
import com.example.api.PathResponse;

import java.util.*;

public class PathfindingService {
    private final Graph graph;
    private final BFS bfs;
    private final DFS dfs;
    private final Dijkstra dijkstra;

    public PathfindingService(Graph graph) {
        this.graph = graph;
        this.bfs = new BFS(graph);
        this.dfs = new DFS(graph);
        this.dijkstra = new Dijkstra(graph);
    }

    public Node findNearestNode(double lat, double lng) {
        Node nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : graph.getNodes()) {
            double distance = calculateDistance(lat, lng, node.getLatitude(), node.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = node;
            }
        }

        return nearest;
    }

    public PathResponse findPathBFS(double startLat, double startLng, double endLat, double endLng) {
        Node startNode = findNearestNode(startLat, startLng);
        Node endNode = findNearestNode(endLat, endLng);

        if (startNode == null || endNode == null) {
            return new PathResponse(false, "Цэгүүд олдсонгүй");
        }

        BFS.PathResult result = bfs.findPathWithStats(startNode.getId(), endNode.getId());

        return PathResponse.fromNodeList(
                result.path,
                "BFS",
                result.totalDistance,
                result.executionTime
        );
    }

    public PathResponse findPathDFS(double startLat, double startLng, double endLat, double endLng) {
        Node startNode = findNearestNode(startLat, startLng);
        Node endNode = findNearestNode(endLat, endLng);

        if (startNode == null || endNode == null) {
            return new PathResponse(false, "Цэгүүд олдсонгүй");
        }

        DFS.PathResult result = dfs.findPathWithStats(startNode.getId(), endNode.getId());

        return PathResponse.fromNodeList(
                result.path,
                "DFS",
                result.totalDistance,
                result.executionTime
        );
    }

    public PathResponse findPathDijkstra(double startLat, double startLng, double endLat, double endLng) {
        Node startNode = findNearestNode(startLat, startLng);
        Node endNode = findNearestNode(endLat, endLng);

        if (startNode == null || endNode == null) {
            return new PathResponse(false, "Цэгүүд олдсонгүй");
        }

        Dijkstra.PathResult result = dijkstra.findPathWithStats(startNode.getId(), endNode.getId());

        return PathResponse.fromNodeList(
                result.path,
                "Dijkstra",
                result.totalDistance,
                result.executionTime
        );
    }

    public Map<String, PathResponse> compareAlgorithms(double startLat, double startLng,
                                                       double endLat, double endLng) {
        Map<String, PathResponse> results = new HashMap<>();

        results.put("bfs", findPathBFS(startLat, startLng, endLat, endLng));
        results.put("dfs", findPathDFS(startLat, startLng, endLat, endLng));
        results.put("dijkstra", findPathDijkstra(startLat, startLng, endLat, endLng));

        return results;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public Graph getGraph() {
        return graph;
    }
}