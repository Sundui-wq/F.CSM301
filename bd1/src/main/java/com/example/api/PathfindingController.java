package com.example.api;

import com.example.graph.Node;
import com.example.service.PathfindingService;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API Controller
 * ЗАСВАРЛАСАН: /api/graph/nodes endpoint нэмэв - бүх цэгүүдийг буцаана
 */
public class PathfindingController {
    private final PathfindingService service;
    private final Gson gson;

    public PathfindingController(PathfindingService service) {
        this.service = service;
        this.gson = new Gson();
    }

    /**
     * Javalin сервер эхлүүлэх
     */
    public void start(int port) {
        Javalin app = Javalin.create(config -> {
            // CORS тохируулах
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });

            // Static файлууд үйлчлэх
            config.staticFiles.add("/public");
        }).start(port);

        // API endpoints
        app.get("/", this::home);
        app.get("/api/health", this::health);
        app.get("/api/graph/stats", this::graphStats);
        app.get("/api/graph/nodes", this::getAllNodes); // ШИНЭ ENDPOINT
        app.post("/api/path/bfs", this::findPathBFS);
        app.post("/api/path/dfs", this::findPathDFS);
        app.post("/api/path/dijkstra", this::findPathDijkstra);
        app.post("/api/path/compare", this::compareAlgorithms);

        System.out.println("\n✓ Server эхэллээ: http://localhost:" + port);
        System.out.println("✓ API documentation: http://localhost:" + port + "/api/health");
    }

    /**
     * Үндсэн хуудас
     */
    private void home(Context ctx) {
        ctx.redirect("/index.html");
    }

    /**
     * Сервер эрүүл байгаа эсэхийг шалгах
     */
    private void health(Context ctx) {
        Map<String, Object> response = Map.of(
                "status", "OK",
                "message", "Pathfinding API is running",
                "graphNodes", service.getGraph().size()
        );
        ctx.json(response);
    }

    /**
     * График статистик
     */
    private void graphStats(Context ctx) {
        ctx.json(Map.of(
                "nodes", service.getGraph().size(),
                "message", "График бэлэн"
        ));
    }

    /**
     * ШИНЭ: Бүх Node-уудыг буцаах endpoint
     * Энэ нь frontend дээр бүх цэгүүдийг газрын зураг дээр харуулах боломж олгоно
     */
    private void getAllNodes(Context ctx) {
        Collection<Node> allNodes = service.getGraph().getNodes();

        // Node -> SimpleNode (JSON serialization-д тохиромжтой)
        List<SimpleNode> simpleNodes = allNodes.stream()
                .map(node -> new SimpleNode(node.getId(), node.getLatitude(), node.getLongitude()))
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
                "success", true,
                "totalNodes", simpleNodes.size(),
                "nodes", simpleNodes
        );

        ctx.json(response);
    }

    /**
     * BFS зам олох
     */
    private void findPathBFS(Context ctx) {
        try {
            PathRequest request = gson.fromJson(ctx.body(), PathRequest.class);
            PathResponse response = service.findPathBFS(
                    request.startLat, request.startLng,
                    request.endLat, request.endLng
            );
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new PathResponse(false, "Алдаа: " + e.getMessage()));
        }
    }

    /**
     * DFS зам олох
     */
    private void findPathDFS(Context ctx) {
        try {
            PathRequest request = gson.fromJson(ctx.body(), PathRequest.class);
            PathResponse response = service.findPathDFS(
                    request.startLat, request.startLng,
                    request.endLat, request.endLng
            );
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new PathResponse(false, "Алдаа: " + e.getMessage()));
        }
    }

    /**
     * Dijkstra зам олох
     */
    private void findPathDijkstra(Context ctx) {
        try {
            PathRequest request = gson.fromJson(ctx.body(), PathRequest.class);
            PathResponse response = service.findPathDijkstra(
                    request.startLat, request.startLng,
                    request.endLat, request.endLng
            );
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new PathResponse(false, "Алдаа: " + e.getMessage()));
        }
    }

    /**
     * Гурван алгоритмыг харьцуулах
     */
    private void compareAlgorithms(Context ctx) {
        try {
            PathRequest request = gson.fromJson(ctx.body(), PathRequest.class);
            Map<String, PathResponse> results = service.compareAlgorithms(
                    request.startLat, request.startLng,
                    request.endLat, request.endLng
            );
            ctx.json(results);
        } catch (Exception e) {
            ctx.status(400).json(new PathResponse(false, "Алдаа: " + e.getMessage()));
        }
    }

    /**
     * API хүсэлтийн класс
     */
    private static class PathRequest {
        double startLat;
        double startLng;
        double endLat;
        double endLng;
    }

    /**
     * JSON-д хөрвүүлэхэд тохиромжтой энгийн Node класс
     */
    private static class SimpleNode {
        private long id;
        private double lat;
        private double lng;

        public SimpleNode(long id, double lat, double lng) {
            this.id = id;
            this.lat = lat;
            this.lng = lng;
        }

        public long getId() { return id; }
        public double getLat() { return lat; }
        public double getLng() { return lng; }
    }
}
