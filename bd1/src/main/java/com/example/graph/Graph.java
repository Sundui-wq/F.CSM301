package com.example.graph;

import java.util.*;

/**
 * Графын бүтэц - замын сүлжээг илэрхийлнэ
 */
public class Graph {
    // Adjacency List: цэг -> түүнээс гарах ирмэгүүд
    private final Map<Long, Node> nodes;
    private final Map<Long, List<Edge>> adjacencyList;

    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Цэг нэмэх
     */
    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    /**
     * Ирмэг нэмэх
     */
    public void addEdge(Edge edge) {
        long fromId = edge.getFrom().getId();
        long toId = edge.getTo().getId();

        // Эхлэх болон дуусах цэгүүдийг графт нэмэх
        addNode(edge.getFrom());
        addNode(edge.getTo());

        // Ирмэг нэмэх
        adjacencyList.get(fromId).add(edge);

        // Хэрэв хоёр чиглэлийн зам бол эсрэг чиглэлийн ирмэг нэмэх
        if (!edge.isOneWay()) {
            Edge reverseEdge = new Edge(
                    edge.getTo(),
                    edge.getFrom(),
                    edge.getWeight(),
                    edge.getRoadType(),
                    false
            );
            adjacencyList.get(toId).add(reverseEdge);
        }
    }

    /**
     * ID-аар цэг авах
     */
    public Node getNode(long id) {
        return nodes.get(id);
    }

    /**
     * Бүх цэгүүд
     */
    public Collection<Node> getNodes() {
        return nodes.values();
    }

    /**
     * Тухайн цэгээс гарах ирмэгүүд
     */
    public List<Edge> getEdges(long nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }

    /**
     * Графын статистик мэдээлэл
     */
    public void printStats() {
        System.out.println("=== Графын мэдээлэл ===");
        System.out.println("Цэгүүдийн тоо: " + nodes.size());

        int totalEdges = 0;
        for (List<Edge> edges : adjacencyList.values()) {
            totalEdges += edges.size();
        }
        System.out.println("Ирмэгүүдийн тоо: " + totalEdges);

        // Замын төрлүүдийн статистик
        Map<String, Integer> roadTypeCounts = new HashMap<>();
        for (List<Edge> edges : adjacencyList.values()) {
            for (Edge edge : edges) {
                String type = edge.getRoadType();
                roadTypeCounts.put(type, roadTypeCounts.getOrDefault(type, 0) + 1);
            }
        }

        System.out.println("\nЗамын төрлүүд:");
        roadTypeCounts.forEach((type, count) ->
                System.out.println("  " + type + ": " + count));
    }

    /**
     * Хоёр цэг холбогдсон эсэхийг шалгах
     */
    public boolean hasEdge(long fromId, long toId) {
        List<Edge> edges = adjacencyList.get(fromId);
        if (edges == null) return false;

        for (Edge edge : edges) {
            if (edge.getTo().getId() == toId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Графын хэмжээ
     */
    public int size() {
        return nodes.size();
    }
}