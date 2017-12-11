package main.app;

/**
 * Created by rusland on 05.12.17.
 */
public class Node {
    private int x;
    private int y;
    private int id;
    private int demand;
    private int openingTime;
    private int closingTime;
    private int serviceTime;

    public Node(int x, int y, int index) {
        this(x, y, index, 0);
    }

    public Node(final int x, final int y, final int id, final int demand) {
        this.x = x;
        this.y = y;
        this.demand = demand;
        this.id = id;
    }

    public double distanaceTo(Node c) {
        int dimX = (this.x - c.x) * (this.x - c.x);
        int dimY = (this.y - c.y) * (this.y - c.y);
        return Math.sqrt(dimX + dimY);
    }
}
