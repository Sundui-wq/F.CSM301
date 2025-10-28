package com.example.graph;

/**
 * Графын ирмэг (edge) - хоёр цэгийн хоорондох зам
 */
public class Edge {
    private final Node from;        // Эхлэх цэг
    private final Node to;          // Дуусах цэг
    private final double weight;    // Жин (замын урт км-ээр)
    private final String roadType;  // Замын төрөл
    private final boolean oneWay;   // Нэг чиглэлийн эсэх

    public Edge(Node from, Node to, double weight, String roadType, boolean oneWay) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.roadType = roadType;
        this.oneWay = oneWay;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public String getRoadType() {
        return roadType;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    @Override
    public String toString() {
        return "Edge{from=" + from.getId() +
                ", to=" + to.getId() +
                ", weight=" + String.format("%.2f", weight) + "km" +
                ", type=" + roadType +
                ", oneWay=" + oneWay + "}";
    }
}