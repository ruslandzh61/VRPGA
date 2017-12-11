package main.evolution;

import main.app.Node;
import main.app.VRPManagaer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rusland on 05.12.17.
 */
public class Chromosome {
    private List<Integer> nodes; // points to VRPGA list of all nodes
    private List<Integer> topology; // nodes are processed and topology is computed and stored, stores start of route
    private double distance;
    private double fitness;

    public Chromosome(int n, boolean generate) {
        nodes = new ArrayList<>(n);

        if (generate) {
            for (int i = 0; i < n; ++n) {
                nodes.set(i, i);
            }
        }
        Collections.shuffle(nodes);
    }

    public void buildTopology() {
        // update topology

    }

    public int get(int idx) {
        return nodes.get(idx);
    }

    /*public void set(int idx, int nodeIdx) {
        nodes.set(idx, nodeIdx);
    }*/ // don't include as it may result in violation of problem constraints

    public double getFitness() {
        if (distance == 0) fitness = 1.0 / distance; // goal then is to minimize fitness
        return fitness;
    }

    public double getDistance() {
        if (distance == 0) {
            for (int r = 0; r < topology.size(); ++r) { // points to topology
                /* points to nodes */
                int startOfRoute = topology.get(r);
                int endOfRoute;
                if (r + 1 < topology.size()) endOfRoute = topology.get(r + 1) - 1; // start of idx of next route minus 1
                else endOfRoute = nodes.size() - 1; // last customer

                for (int i = startOfRoute; i <= endOfRoute; ++i) {
                    Node from = VRPManagaer.getCustomer(nodes.get(i));
                    if (i == startOfRoute || i == endOfRoute) { // either first or last customer
                        distance += from.distanaceTo(VRPManagaer.getCustomer(0));
                    } else {
                        distance += from.distanaceTo(VRPManagaer.getCustomer(i + 1));
                    }
                }
            }
        }
        return distance;
    }

    public int size() {
        return nodes.size();
    }

    @Override
    public String toString() {
        String geneString = "| ";
        for (int i = 0; i < nodes.size(); i++) {
            geneString += nodes.get(i)+" | ";
        }
        return geneString;
    }
}
