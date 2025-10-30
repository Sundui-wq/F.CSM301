package com.example;

import com.example.api.PathfindingController;
import com.example.graph.Graph;
import com.example.parser.ShapefileParser;
import com.example.service.PathfindingService;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Улаанбаатарын Замын Pathfinding API ===\n");

            System.out.println("График үүсгэж байна...");
            ShapefileParser parser = new ShapefileParser();
            String shapefilePath = "src/main/resources/data/gis_osm_roads_free_1.shp";
            Graph graph = parser.parseShapefile(shapefilePath);

            System.out.println("\n✓ График амжилттай үүслээ!");

            PathfindingService service = new PathfindingService(graph);

            PathfindingController controller = new PathfindingController(service);
            controller.start(8080);

            System.out.println("http://localhost:8080 ");

        } catch (Exception e) {
            System.err.println("Алдаа гарлаа: " + e.getMessage());
            e.printStackTrace();
        }
    }
}