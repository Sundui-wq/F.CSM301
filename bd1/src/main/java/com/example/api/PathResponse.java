package com.example.api;

import com.example.graph.Node;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API-ийн хариулт - замын мэдээлэл
 */
public class PathResponse {
    private boolean success;
    private String message;
    private String algorithm;
    private List<PathNode> path;
    private int pathLength;
    private double totalDistance;
    private double executionTime;

    public PathResponse() {
    }

    public PathResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Node жагсаалтаас PathResponse үүсгэх
     */
    public static PathResponse fromNodeList(List<Node> nodes, String algorithm,
                                            double distance, double time) {
        PathResponse response = new PathResponse();
        response.success = nodes != null && !nodes.isEmpty();
        response.algorithm = algorithm;
        response.executionTime = time;

        if (response.success) {
            response.message = "Зам амжилттай олдлоо";
            response.path = nodes.stream()
                    .map(node -> new PathNode(node.getId(), node.getLatitude(), node.getLongitude()))
                    .collect(Collectors.toList());
            response.pathLength = nodes.size();
            response.totalDistance = distance;
        } else {
            response.message = "Зам олдсонгүй";
        }

        return response;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public List<PathNode> getPath() { return path; }
    public void setPath(List<PathNode> path) { this.path = path; }

    public int getPathLength() { return pathLength; }
    public void setPathLength(int pathLength) { this.pathLength = pathLength; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public double getExecutionTime() { return executionTime; }
    public void setExecutionTime(double executionTime) { this.executionTime = executionTime; }

    /**
     * Замын цэг (JSON-д хөрвүүлэхэд тохиромжтой)
     */
    public static class PathNode {
        private long id;
        private double lat;
        private double lng;

        public PathNode(long id, double lat, double lng) {
            this.id = id;
            this.lat = lat;
            this.lng = lng;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }
}