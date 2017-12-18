package main.app;

import main.evolution.Chromosome;
import main.evolution.ParetoRankingGA;
import main.evolution.WeightedSumGA;
import main.evolution.Population;
import main.utils.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rusland on 05.12.17.
 */
public class VRPGA {
    public VRPGA() {
        int depos = 1;
        int customers = 25;
        int n = depos + customers, vehicleCapacity = 200;
        int popSize = 300, tournSize = 4, elitismRate = 0;
        double crossoverRate = 0.8, mutationRate = 0.1;
        double weightedSumAlpha = 0; // for weighted sum: 100
        double weightedSumBeta = 1; // for weighted sum approach: 1
        boolean isParetoRanking = true;
        int runs = 5;

        for (int i = 0; i < runs; ++i) {
            VRPManager.init(n, vehicleCapacity);
            if (isParetoRanking) {
                ParetoRankingGA.init(popSize, crossoverRate, elitismRate, tournSize, mutationRate);
                Chromosome.init(isParetoRanking);
            } else {
                WeightedSumGA.init(crossoverRate, elitismRate, tournSize, mutationRate);
                Chromosome.init(isParetoRanking, weightedSumAlpha, weightedSumBeta);
            }

            Population pop = new Population(popSize);
            System.out.print("Initial solution: ");
            //pop.printPopulation();
            System.out.println("evolution:");
            for (int j = 1; j <= 350; ++j) {
                if (isParetoRanking) {
                    pop = ParetoRankingGA.evolve(pop);
                } else pop = WeightedSumGA.evolve(pop);
                Chromosome fittest = pop.getFittest();
                //pop.printPopulation();
            /*System.out.println("Generation #" + i + "\n" +
                    "fittest:   " + pop.getFittest() + "\n" +
                    "distance:  " + pop.getFittest().getDistance() +
                    "routes:    " + pop.getFittest().getNumOfRoutes() + "\n");*/
                System.out.println("Generation #" + j + " distance:  " + fittest.getDistance() + " " + fittest.getNumOfRoutes());
            }
        }
    }

    public static void testParser() {
        Parser.parse("/Users/rusland/Desktop/Fall2017/EC/Projects/Assign_1/data/R101_200.csv", 26);
    }

    private static void mutate(List<Integer> route, List<ArrayList<Integer>> individualArray) {

        Random rnd = new Random();
        // choose subRoute
        int subRouteIdx = rnd.nextInt(individualArray.size());
        List<Integer> subRoute = individualArray.get(subRouteIdx);

        // choose range
        int start = rnd.nextInt(subRoute.size());
        int len = 1 + rnd.nextInt(subRoute.size()-start);
        //System.out.println(subRouteIdx + " " + start + " " + len);
        // update range to
        start = route.indexOf(subRoute.get(start));
        int end = start + len;
        //System.out.println(start + " " + end);
        for(int i = 0; i < len / 2; i++) {
            int temp = route.get(start + i);
            route.set(start + i, route.get(end - i - 1));
            route.set(end - i - 1, temp);
        }
    }

    public static void test() {
        int n = 5; // total number of customers and depos
        int vehCapacity = 200;
        VRPManager.init(n, vehCapacity);
        List<Integer> topology = new ArrayList<>();
        // synthetically generate chromosome
        List<Integer> customers = new ArrayList<>();
        for (int i = VRPManager.getNumOfDepos(); i < VRPManager.getNumOfDepos() + VRPManager.getNumOfCustomers(); ++i) {
            customers.add(i);
        }
        Chromosome chromosome = new Chromosome(customers);
        System.out.println(chromosome);
        System.out.println(chromosome.getSubRoute(2));
    }
    public static void buildTopology() {
        int n = 11; // total number of customers and depos
        int vehCapacity = 200;
        VRPManager.init(n, vehCapacity);
        List<Integer> topology = new ArrayList<>();
        // synthetically generate chromosome
        List<Integer> customers = new ArrayList<>();
        for (int i = VRPManager.getNumOfDepos(); i < VRPManager.getNumOfDepos() + VRPManager.getNumOfCustomers(); ++i) {
            customers.add(i);
        }


        /*// list of sub-routes
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
                System.out.println("customer's closing constraint: " + (vehicleElapsedTime + vehicleTravelTime) + " " + custClosTime);
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
                System.out.println("depo's and customer's constraint");
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
        System.out.println("route size is " + route.size());
        for (int i = 0; i < route.size(); ++i) {
            // get sub-route -> get first customer of sub-route -> find its idx in nodes -> add to topology
            topology.add(customers.indexOf(route.get(i).get(0)));
        }*/

        System.out.println(topology);
    }

    public static void testTopology() {
        buildTopology();
    }

    public static void main(String[] args) {
        /* test parser */
        //testParser();
        /*List<Integer> route = new ArrayList<>();
        route.add(6);
        route.add(2);
        route.add(1);
        route.add(4);
        route.add(5);
        route.add(7);
        route.add(3);
        System.out.println(route);

        List<ArrayList<Integer>> subRoutes = new ArrayList<>();
        subRoutes.add(new ArrayList<>());
        subRoutes.add(new ArrayList<>());
        subRoutes.add(new ArrayList<>());

        subRoutes.get(0).add(route.get(0));
        subRoutes.get(0).add(route.get(1));

        subRoutes.get(1).add(route.get(2));
        subRoutes.get(1).add(route.get(3));
        subRoutes.get(1).add(route.get(4));

        subRoutes.get(2).add(route.get(5));
        subRoutes.get(2).add(route.get(6));

        mutate(route, subRoutes);
        System.out.println(route);
        /* test Uniform Order Crossover
        List<Integer> par1 = new ArrayList<>();
        par1.add(6);
        par1.add(2);
        par1.add(1);
        par1.add(4);
        par1.add(5);
        par1.add(7);
        par1.add(3);
        List<Integer> par2 = new ArrayList<>();
        par2.add(4);
        par2.add(3);
        par2.add(7);
        par2.add(2);
        par2.add(1);
        par2.add(6);
        par2.add(5);
        testUOX(par1, par2);
        */

        new VRPGA();
        //testTopology();
        //test();
    }
}
