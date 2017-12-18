package main.app;

/**
 * Created by rusland on 05.12.17.
 */
public class Node {
    private int id;
    private double x;
    private double y;
    private double demand;
    private double openingTime;
    private double closingTime;
    private double serviceTime;

    public Node(int index, double x, double y) {
        this(index, x, y, 0);
    }

    public Node(final int id, final double x, final double y, final double demand) {
        this.x = x;
        this.y = y;
        this.demand = demand;
        this.id = id;
    }

    public Node(int id, double x, double y, double demand,
                double openingTime, double closingTime, double serviceTime) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.demand = demand;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.serviceTime = serviceTime;
    }

    public double distanceTo(Node c) {
        double dimX = (this.x - c.x) * (this.x - c.x);
        double dimY = (this.y - c.y) * (this.y - c.y);
        return Math.sqrt(dimX + dimY);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public double getDemand() {
        return demand;
    }

    public double getOpeningTime() {
        return openingTime;
    }

    public double getClosingTime() {
        return closingTime;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    @Override
    public String toString() {
        return Integer.toString(id) + " " + x + " " + y + " " +
                demand + " " + openingTime + " " + closingTime + " " + serviceTime;
    }
}
