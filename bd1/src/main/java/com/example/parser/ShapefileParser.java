package com.example.parser;

import com.example.graph.Edge;
import com.example.graph.Graph;
import com.example.graph.Node;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Shapefile файл уншиж график бүтэц үүсгэх
 * ЗАСВАРЛАСАН: Дунд цэгүүдийг ч графикт нэмж, график бүрэн холбогдох болгов
 */
public class ShapefileParser {
    private final Graph graph;
    private final Map<String, Long> coordinateToNodeId;
    private long nodeIdCounter = 0;

    public ShapefileParser() {
        this.graph = new Graph();
        this.coordinateToNodeId = new HashMap<>();
    }

    /**
     * Shapefile файл уншиж график үүсгэх
     * @param shapefilePath Shapefile файлын зам
     * @return График бүтэц
     */
    public Graph parseShapefile(String shapefilePath) throws IOException {
        System.out.println("Shapefile уншиж байна: " + shapefilePath);

        File file = new File(shapefilePath);

        // Resources-оос файл уншиж түр файл үүсгэх
        if (!file.exists()) {
            file = extractFromResources(shapefilePath);
        }

        if (!file.exists()) {
            throw new IOException("Файл олдсонгүй: " + shapefilePath);
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureCollection collection = featureSource.getFeatures();

        System.out.println("Нийт feature: " + collection.size());

        try (SimpleFeatureIterator iterator = collection.features()) {
            int count = 0;
            int skipped = 0;
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                boolean processed = processFeature(feature);
                if (processed) {
                    count++;
                } else {
                    skipped++;
                }

                if ((count + skipped) % 1000 == 0) {
                    System.out.println("Боловсруулсан: " + count + " feature, устгагдсан: " + skipped);
                }
            }

            System.out.println("Нийт боловсруулсан feature: " + count + ", устгагдсан feature: " + skipped);
        }

        store.dispose();

        System.out.println("Shapefile уншилт дууслаа!");
        graph.printStats();

        return graph;
    }

    /**
     * Resources folder-оос файл гаргаж авах
     */
    private File extractFromResources(String resourcePath) throws IOException {
        // Зөвхөн файлын нэрийг авах
        String fileName = new File(resourcePath).getName();
        String baseName = fileName.replace(".shp", "");

        // Шаардлагатай файлын өргөтгөлүүд
        String[] extensions = {".shp", ".dbf", ".shx", ".prj", ".cpg"};

        // Түр зам үүсгэх
        Path tempDir = Files.createTempDirectory("shapefile_");
        File shpFile = null;

        for (String ext : extensions) {
            String resPath = "/data/" + baseName + ext;
            InputStream is = getClass().getResourceAsStream(resPath);

            if (is != null) {
                Path tempFile = tempDir.resolve(baseName + ext);
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
                is.close();

                if (ext.equals(".shp")) {
                    shpFile = tempFile.toFile();
                }
            }
        }

        return shpFile;
    }

    // Улаанбаатарын координатын хязгаар
    private static final double UB_MIN_LAT = 47.7;
    private static final double UB_MAX_LAT = 48.2;
    private static final double UB_MIN_LON = 106.6;
    private static final double UB_MAX_LON = 107.2;

    /**
     * Координат Улаанбаатарт байгаа эсэхийг шалгах
     */
    private boolean isInUlaanbaatar(double lat, double lon) {
        return lat >= UB_MIN_LAT && lat <= UB_MAX_LAT &&
                lon >= UB_MIN_LON && lon <= UB_MAX_LON;
    }

    /**
     * Feature боловсруулж график руу нэмэх
     */
    private boolean processFeature(SimpleFeature feature) {
        try {
            // Геометр авах
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            if (geometry == null) {
                return false;
            }

            // Улаанбаатарын хязгаарт байгаа эсэхийг шалгах
            Coordinate centroid = geometry.getCentroid().getCoordinate();
            if (!isInUlaanbaatar(centroid.y, centroid.x)) {
                return false;
            }

            // Атрибутууд авах
            String fclass = getStringAttribute(feature, "fclass");
            String oneway = getStringAttribute(feature, "oneway");
            Double maxspeed = getDoubleAttribute(feature, "maxspeed");

            // Замын төрөл шүүх
            if (!isValidRoadType(fclass)) {
                return false;
            }

            boolean isOneWay = "yes".equalsIgnoreCase(oneway) ||
                    "true".equalsIgnoreCase(oneway) ||
                    "B".equalsIgnoreCase(oneway);

            // Геометрийг боловсруулах
            if (geometry instanceof LineString) {
                processLineString((LineString) geometry, fclass, isOneWay, maxspeed);
                return true;
            } else if (geometry instanceof MultiLineString) {
                MultiLineString mls = (MultiLineString) geometry;
                boolean any = false;
                for (int i = 0; i < mls.getNumGeometries(); i++) {
                    LineString ls = (LineString) mls.getGeometryN(i);
                    processLineString(ls, fclass, isOneWay, maxspeed);
                    any = true;
                }
                return any;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Feature боловсруулахад алдаа: " + e.getMessage());
            return false;
        }
    }

    /**
     * LineString боловсруулж ирмэгүүд үүсгэх
     * ЗАСВАРЛАСАН: Одоо бүх дунд цэгүүдийг ч графикт нэмнэ
     */
    private void processLineString(LineString lineString, String roadType,
                                   boolean oneWay, Double maxspeed) {
        Coordinate[] coords = lineString.getCoordinates();
        if (coords.length < 2) return;

        // Coordinate бүрийг node болгон үүсгэж, хоорондох ирмэгүүдийг холбох
        for (int i = 0; i < coords.length - 1; i++) {
            Node fromNode = getOrCreateNode(coords[i]);
            Node toNode = getOrCreateNode(coords[i + 1]);

            // Хоёр цэгийн хоорондох зайг тооцоолох
            double distance = fromNode.distanceTo(toNode);

            // Ирмэг үүсгэх
            Edge edge = new Edge(fromNode, toNode, distance, roadType, oneWay);
            graph.addEdge(edge);
        }
    }

    /**
     * Координатаас цэг үүсгэх эсвэл авах
     */
    private Node getOrCreateNode(Coordinate coord) {
        String key = coord.x + "," + coord.y;

        if (!coordinateToNodeId.containsKey(key)) {
            long id = nodeIdCounter++;
            coordinateToNodeId.put(key, id);
            Node node = new Node(id, coord.y, coord.x); // lat, lon
            graph.addNode(node);
            return node;
        }

        long id = coordinateToNodeId.get(key);
        return graph.getNode(id);
    }

    /**
     * Замын төрөл шалгах
     */
    private boolean isValidRoadType(String fclass) {
        if (fclass == null) return true;

        String fc = fclass.toLowerCase();
        return fc.equals("motorway") ||
                fc.equals("trunk") ||
                fc.equals("primary") ||
                fc.equals("secondary") ||
                fc.equals("tertiary") ||
                fc.equals("residential") ||
                fc.equals("living_street") ||
                fc.equals("unclassified") ||
                fc.equals("service") ||
                fc.equals("road") ||
                fc.equals("track") ||
                fc.equals("path");
    }

    /**
     * String атрибут авах
     */
    private String getStringAttribute(SimpleFeature feature, String attributeName) {
        try {
            Object value = feature.getAttribute(attributeName);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Double атрибут авах
     */
    private Double getDoubleAttribute(SimpleFeature feature, String attributeName) {
        try {
            Object value = feature.getAttribute(attributeName);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Graph getGraph() {
        return graph;
    }
}
