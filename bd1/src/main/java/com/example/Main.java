package com.example;

import com.example.api.PathfindingController;
import com.example.graph.Graph;
import com.example.parser.ShapefileParser;
import com.example.service.PathfindingService;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Улаанбаатарын Замын Pathfinding API ===\n");

            // 1. Shapefile уншиж график үүсгэх
            System.out.println("График үүсгэж байна...");
            ShapefileParser parser = new ShapefileParser();
            String shapefilePath = "src/main/resources/data/gis_osm_roads_free_1.shp";
            Graph graph = parser.parseShapefile(shapefilePath);

            System.out.println("\n✓ График амжилттай үүслээ!");

            // 2. Service үүсгэх
            PathfindingService service = new PathfindingService(graph);

            // 3. REST API сервер эхлүүлэх
            PathfindingController controller = new PathfindingController(service);
            controller.start(8080);

            System.out.println("\n=== Хэрэглэх заавар ===");
            System.out.println("1. Браузер дээр http://localhost:8080 нээх");
            System.out.println("2. Postman эсвэл curl ашиглах:");
            System.out.println("\nЖишээ:");
            System.out.println("curl -X POST http://localhost:8080/api/path/dijkstra \\");
            System.out.println("  -H \"Content-Type: application/json\" \\");
            System.out.println("  -d '{\"startLat\": 47.92, \"startLng\": 106.92, \"endLat\": 47.93, \"endLng\": 106.93}'");

        } catch (Exception e) {
            System.err.println("Алдаа гарлаа: " + e.getMessage());
            e.printStackTrace();
        }
    }
}