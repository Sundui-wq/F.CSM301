package com.example.graph;

import java.util.Objects;

/**
 * Графын орой (vertex) - замын уулзвар цэг
 */
public class Node {
    private final long id;           // Цэгийн дугаар
    private final double latitude;   // Өргөрөг
    private final double longitude;  // Уртраг

    public Node(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Хоёр цэгийн хоорондох зайг тооцоолох (Haversine томъёо)
     * @param other Нөгөө цэг
     * @return Зай (километр)
     */
    public double distanceTo(Node other) {
        final int R = 6371; // Дэлхийн радиус (км)

        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node{id=" + id + ", lat=" + latitude + ", lon=" + longitude + "}";
    }
}