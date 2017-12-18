package main.evolution;

import main.app.Node;
import main.app.VRPManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rusland on 05.12.17.
 */
public class Chromosome {
    private List<Integer> customers; // points to VRPGA list of all customers
    private List<Integer> topology; // customers are processed and topology is computed and stored, stores route start idx, point to customers
    private double distance;
    private double fitness;
    private static boolean isParetoRanking;
    private static double aplha;
    private static double betha;

    int rank;

    public static void init(boolean aIsParetoRanking) {
        Chromosome.isParetoRanking = aIsParetoRanking;
    }

    public static void init(boolean aIsParetoRanking, double aAlpha, double aBetha) {
        init(aIsParetoRanking);
        Chromosome.aplha = aAlpha;
        Chromosome.betha = aBetha;
    }

    public Chromosome(boolean generate) {
        customers = new ArrayList<>(VRPManager.getNumOfCustomers());
        topology = new ArrayList<>();
        if (generate) {
            for (int i = VRPManager.getNumOfDepos(); i < VRPManager.getNumOfCustomers() + VRPManager.getNumOfDepos(); ++i) {
                customers.add(i);
            }
            Collections.shuffle(customers);
            buildTopology();
        } else {
            for (int i = VRPManager.getNumOfDepos(); i < VRPManager.getNumOfCustomers() + VRPManager.getNumOfDepos(); ++i) {
                customers.add(-1);
            }
        }
    }

    public Chromosome() {}

    // for testing purposes only
    public Chromosome(List<Integer> customers) {
        this.customers = customers.subList(0, customers.size());

        buildTopology();
    }

    public Chromosome copy() {
        return new Chromosome(customers.subList(0, customers.size()));
    }

    /* no time estimated between two points */
    public void buildTopology() {
        topology = new ArrayList<>();
        // list of sub-routes
        List<ArrayList<Integer>> route = new ArrayList<>();
        int vehicleCapacity = VRPManager.getVehicleCapacity();
        double depoClosTime = VRPManager.getDepo().getClosingTime();
        // Initialize a sub-route
        ArrayList<Integer> subRoute = new ArrayList<>();
        double vehicleLoad = 0;
        double vehicleElapsedTime = 0;
        int lastNodeIdx = 0; // index of last node, if 0 then vehicle visits first customer and
        // time to get to them from depo is calculated
        for (int i = 0; i < customers.size(); ++i) {
            // obtain customer's info
            int custIdx = customers.get(i);
            Node customer = VRPManager.getNode(custIdx);
            double custDemand = customer.getDemand();
            double custOpenTime = customer.getOpeningTime();
            double custClosTime = customer.getClosingTime();
            double custServiceTime = customer.getServiceTime();

            // obtain travelling time info
            double vehicleTravelTime = VRPManager.getTravelTime(lastNodeIdx, custIdx); // compute time to get to customer
            double returnToDepoTime = VRPManager.getTravelTime(custIdx, 0); // compute time to return to depo

            // check 1: customer's time window constraint,
            // check that it arrives before closing, no constraint for opening
            if (custClosTime < vehicleElapsedTime + vehicleTravelTime) {
                //System.out.println("customer's closing constraint: " + (vehicleElapsedTime + vehicleTravelTime) + " " + custClosTime);
                // Save current sub-route
                route.add(subRoute);
                // Initialize a new sub-route and add currect customer to it

                subRoute = new ArrayList<>();
                subRoute.add(custIdx);
                vehicleLoad = custDemand;
                // start when first customer is available, lastCustomerIdx should be 0
                vehicleElapsedTime = Math.max(custOpenTime, vehicleTravelTime) + custServiceTime;
                lastNodeIdx = custIdx;
                continue; // skip the rest
            }

            // initialize temporary varibles for processing current customer
            double updatedLoad = vehicleLoad + custDemand;                      // update vehicle load
            // pick max between elapsed time and opening time + time to travel to customer + service + time to return
            double updatedElapsedTime = Math.max(vehicleElapsedTime + vehicleTravelTime, custOpenTime) +
                    custServiceTime + returnToDepoTime;
            // check 2: depo's time window constraint
            // check 3: vehicle capacity constraint
            if (updatedLoad <= vehicleCapacity && updatedElapsedTime <= depoClosTime) {
                subRoute.add(custIdx);
                vehicleLoad = updatedLoad;
                vehicleElapsedTime = updatedElapsedTime - returnToDepoTime; // subtract because vehicle doesn't return
            } else {
                vehicleTravelTime =  VRPManager.getTravelTime(0, custIdx);// recalculate because now vehicle get there from depo and not from another place
                //System.out.println("depo's and customer's constraint");
                // Save current sub-route
                route.add(subRoute);
                // Initialize a new sub-route and add currect customer to it
                subRoute = new ArrayList<>();
                subRoute.add(custIdx);
                vehicleLoad = custDemand;
                // start when first customer is available or travel time more than opening
                vehicleElapsedTime = Math.max(custOpenTime, vehicleTravelTime) + custServiceTime;
            }
            lastNodeIdx = custIdx;
        }

        if (!subRoute.isEmpty()) {
            // Save current sub-route before return if not empty
            route.add(subRoute);
        }
        //System.out.println("route size is " + route.size());
        for (int i = 0; i < route.size(); ++i) {
            // get sub-route -> get first customer of sub-route -> find its idx in nodes -> add to topology
            topology.add(customers.indexOf(route.get(i).get(0)));
        }
    }

    public int indexOf(int customerid) {
        return customers.indexOf(customerid);
    }

    public List<Integer> getSubRoute(int idx) {
        int start = topology.get(idx); // start inclusive, points to customers
        int end; // end exclusive
        if (idx == topology.size() - 1) { // if last route, then cust's end
            end = customers.size();
        } else end = topology.get(idx + 1); // otherwise (next route start) - 1

        return customers.subList(start, end);
    }

    // end exclusive
    public void reverse(int start, int end) {
        //System.out.println(start + " " + end);
        for(int i = 0; i < (end - start) / 2; i++) {
            int temp = customers.get(start + i);
            customers.set(start + i, customers.get(end - i - 1));
            customers.set(end - i - 1, temp);
        }
        buildTopology(); // recalculate sub-routes after changing the chromosome
    }

    public int getNumOfRoutes() { return topology.size(); }

    public int get(int idx) {
        return customers.get(idx);
    }

    public boolean contains(int val) {
        return customers.contains(val);
    }

    public void set(int idx, int val) {
        customers.set(idx, val);
    }

    /*public void set(int idx, int nodeIdx) {
        customers.set(idx, nodeIdx);
    }*/ // don't include as it may result in violation of problem constraints

    public double getFitness() {
        if (isParetoRanking) {
            return rank;
        } else if (fitness == 0) {
            fitness = 1.0 / (getNumOfRoutes() * aplha + getDistance() * betha); // goal then is to minimize fitness
        }
        return fitness;
    }

    public double getDistance() {
        if (distance == 0) {
            for (int r = 0; r < topology.size(); ++r) { // points to topology
                /* points to customers */
                int startOfRoute = topology.get(r); // points to customers
                int endOfRoute;
                if (r + 1 < topology.size()) endOfRoute = topology.get(r + 1) - 1; // start of idx of next route minus 1
                else endOfRoute = customers.size() - 1; // last customer

                distance += VRPManager.getNode(customers.get(startOfRoute)).distanceTo(VRPManager.getNode(0));
                //System.out.println(distance + " ");
                for (int i = startOfRoute; i < endOfRoute; ++i) {
                    distance += VRPManager.getNode(customers.get(i)).distanceTo(VRPManager.getNode(customers.get(i + 1)));
                    //System.out.println(i + " " + distance + " ");
                }
                distance += VRPManager.getNode(customers.get(endOfRoute)).distanceTo(VRPManager.getNode(0));
                //System.out.println(distance + " ");
            }
        }
        return distance;
    }

    public int size() {
        return customers.size();
    }


    // prints id of customer
    @Override
    public String toString() {
        String geneString = "fitness (" + getFitness() + ") distance (" + getDistance() +
                ") routes: (" + topologyStr() + ") solution (";
        for (int i = 0; i < customers.size(); i++) {
            // if beginning of new route put a separator
            if (topology.contains(i) && i != 0) {
                geneString += " -|- ";
            }
            geneString += VRPManager.getNode(customers.get(i)).getId() + " ";
        }

        geneString += ")";
        return geneString;
    }

    private String topologyStr() {
        String result = "";
        for (int i = 0; i < topology.size(); ++i) {
            result += VRPManager.getNode(customers.get(topology.get(i))).getId() + " ";
        }
        return result;
    }

    /*@Override
    public int compareTo(Object o) {
        Chromosome other = (Chromosome) o;
        if (isParetoRanking) {
            if(other.rank < this.rank) {
                return -1;
            } else if(other.rank == this.rank) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if(other.getFitness() < this.getFitness()) {
                return -1;
            } else if(other.getFitness() == this.getFitness()) {
                return 0;
            } else {
                return 1;
            }
        }
    }*/
}
