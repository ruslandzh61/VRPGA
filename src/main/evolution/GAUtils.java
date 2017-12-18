package main.evolution;

import main.utils.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rusland on 18.12.17.
 */
public class GAUtils {
    public static void paretoRanking(Population pop) {
        //System.out.println("begin pareto ranking");
        //System.out.println(pop.getSize());
        List<ParetoFrontier> frontiers = new ArrayList<>();
        int currRank = 1;
        int N = pop.getSize();
        int m = pop.getSize(); // size decreases
        while (N != 0) {
            ParetoFrontier frontier = new ParetoFrontier();
            frontier.rank = new ArrayList<>();
            // identify frontier
            for (int i = 0; i < m; i++) {
                if (nonDominated(pop, pop.get(i))) {
                    pop.get(i).rank = currRank;
                    //System.out.println("rank is: " + pop.get(i).rank);
                }
            }
            //System.out.println("new frontier identified");
            // copy to rankArray
            for (int i = 0; i < m && i < N; i++) {
                if (pop.get(i).rank == currRank) {
                    Chromosome nonDominated = pop.remove(i);
                    frontier.add(nonDominated);
                    N--;
                    i--;
                    //System.out.println("   non-dominated: " + nonDominated);
                }
            }
            if (frontier.size() != 0) frontiers.add(frontier);

            currRank++;
            m = N;
        }
        if (pop.getSize() != 0) System.out.println("ERROR: should be empty after pareto ranking");

        //reinsert into population
        for (ParetoFrontier r : frontiers) {
            for (Chromosome c : r.rank) {
                pop.add(c);
            }
        }
        pop.frontiers = frontiers;
    }

    private static boolean nonDominated(Population pop, Chromosome chr) {
        for (int i = 0; i < pop.getSize(); ++i) { // current population
            Chromosome temp = pop.get(i);
            if (temp == chr) {
                continue;
            }
            // if indiv found better than given chr return false
            if ((temp.getDistance() <= chr.getDistance() && temp.getNumOfRoutes() < chr.getNumOfRoutes()) ||
                    (temp.getDistance() < chr.getDistance() && temp.getNumOfRoutes() <= chr.getNumOfRoutes())) {
                // tmp is dominated by temp
                return false;
            }
        }

        return true;
    }

    /*public static int[] PMXcrossover(List<Integer> parent1, List<Integer> parent2) {
        Random r = new Random();
        int chromosomeSize = parent1.size();
        List<Integer> copyParent1 = parent1.subList(0, chromosomeSize);
        List<Integer> copyParent2 = parent2.subList(0, chromosomeSize);

        int cuttingPoint1 = r.nextInt(chromosomeSize);
        int cuttingPoint2 = r.nextInt(chromosomeSize);

        while (cuttingPoint1 == cuttingPoint2) {
            cuttingPoint2 = r.nextInt(chromosomeSize);
        }

        if (cuttingPoint1 > cuttingPoint2) {
            int swap;
            swap = cuttingPoint1;
            cuttingPoint1 = cuttingPoint2;
            cuttingPoint2 = swap;
        }

        List<Integer> replacement1 = new ArrayList<>();
        List<Integer> replacement2 = new ArrayList<>();

        for (int i = 0; i < chromosomeSize; i++) {
            replacement1.add(-1);
            replacement2.add(-1);
        }

        List<Integer> offspring1Vector = new ArrayList<>();
        List<Integer> offspring2Vector = new ArrayList<>();
        for (int i = 0; i < chromosomeSize; i++) {
            offspring1Vector.add(i);
            offspring2Vector.add(i);
        }

        for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
            offspring1Vector.remove(i);
            offspring1Vector.add(i, copyParent2.get(i));
            offspring2Vector.remove(i);
            offspring2Vector.add(i, copyParent1.get(i));

            //Integer element = replacement1.get(copyParent2.chromosome.get(i));
            int index = copyParent2.get(i);
            replacement1.remove(index);
            replacement1.add(index, copyParent1.get(i));
            //element = replacement2.get(copyParent1.chromosome.get(i));
            index = copyParent1.get(i);
            replacement2.remove(index);
            replacement2.add(index, copyParent2.get(i));
        }

        System.out.println("chromosomeSize: " + chromosomeSize);
        for (int i = 0; i < chromosomeSize; i++) {
            if ((i >= cuttingPoint1) && (i <= cuttingPoint2)) {
                continue;
            }

            int n1 = copyParent1.get(i);
            int m1 = replacement1.get(n1);

            int n2 = copyParent2.get(i);
            int m2 = replacement2.get(n2);

            while (m1 != -1) {
                n1 = m1;
                m1 = replacement1.get(m1);
            } // while


            while (m2 != -1) {
                n2 = m2;
                m2 = replacement2.get(m2);
            } // while

            Integer element = new Integer(offspring1Vector.get(i));
            offspring1Vector.remove(element);
            offspring1Vector.add(i, n1);
            element = new Integer(offspring2Vector.get(i));
            offspring2Vector.remove(element);
            offspring2Vector.add(i, n2);
        }

        //Chromosome offspring1 = new Chromosome(offspring1Vector);
        //Chromosome offspring2 = new Chromosome(offspring2Vector);
        //Chromosome[] children = new Chromosome[2];
        //children[0] = offspring1;
        //children[1] = offspring2;
        System.out.println(offspring1Vector);
        System.out.println(offspring2Vector);
        return null;
    }*/

    /*private List<ArrayList<Integer>> removeSelectedElements(List<Integer> custToBeRemoved, Chromosome parent) {
        List<ArrayList<Integer>> parent =
    }

    public void bestRouteCrossover(Chromosome parent1, Chromosome parent2) {
        Random r = new Random();
        Chromosome copyParent1 = parent1.copy();
        Chromosome copyParent2 = parent2.copy();

        // Selecting route for Parent 1
        int tmp = r.nextInt(copyParent1.getNumOfRoutes());
        List<Integer> keyParent1 = new ArrayList<>();
        for (int i : copyParent1.getSubRoute(tmp)) {
            keyParent1.add(i);
        }
        // Selecting key for Parent 2
        tmp = r.nextInt(copyParent2.getNumOfRoutes());
        List<Integer> keyParent2 = new ArrayList<>();
        for (int i : copyParent2.getSubRoute(tmp)) {
            keyParent2.add(i);
        }
//
//        System.out.println(keyParent1.route);
//        System.out.println(keyParent2.route);

        // call method for removing keyParent elements from alroutesParent
        List<ArrayList<Integer>> newalroutesParent2 = removeSelectedElements(keyParent1, copyParent2);
        List<ArrayList<Integer>> newalroutesParent1 = removeSelectedElements(keyParent2, copyParent1);

//        System.out.println("Modified route list of parents: ");
//        System.out.println("parent 1: ");
//        displayRoutes(newalroutesParent1);
//        System.out.println("parent 2: ");
//        displayRoutes(newalroutesParent2);

        // insert key for first parent in second new parent
//        System.out.println("Checking Insertion Validity: Start");
        for (int i : keyParent1) {
            if (i == 0) {
                continue;
            }
            boolean flag = false;
            ArrayList<Chromosome> tmpCandidates = new ArrayList<Chromosome>();
            for (int k = 0; k < newalroutesParent2.size(); k++) {
                //System.out.println("Route k: " + k);
                Route jroute = newalroutesParent2.get(k);
                for (int j = 1; j < jroute.route.size(); j++) {
                    // get a new copy of this candidate
                    Candidate tmpCopyCandidate = copyCandidate(copyParent2);
                    // insert the i node and check validity
                    tmpCopyCandidate.routeTable.get(k).route.add(j, i);
                    //System.out.println(checkInsertionValidity(tmpCopyCandidate.routeTable.get(k)));
                    if (checkInsertionValidity(tmpCopyCandidate.routeTable.get(k))) {
                        flag = true;
                        tmpCopyCandidate.fitness = evaluateFitnessCandidate(tmpCopyCandidate);
                        //System.out.println(tmpCopyCandidate.fitness);
                        tmpCandidates.add(tmpCopyCandidate);
                    }
                    // if not valid delete this candidate
                    // if valid calculate fitness
                    // store this candidate in new arraylist of new candidates
                }
            }
            Collections.sort(tmpCandidates);
            if (tmpCandidates.size() != 0) {
                copyParent2 = tmpCandidates.get(0);
            }
        }

        for (int i : keyParent2.route) {
            if (i == 0) {
                continue;
            }
            boolean flag = false;
            ArrayList<Candidate> tmpCandidates = new ArrayList<Candidate>();
            for (int k = 0; k < newalroutesParent1.size(); k++) {
                //System.out.println("Route k: " + k);
                Route jroute = newalroutesParent1.get(k);
                for (int j = 1; j < jroute.route.size(); j++) {
                    // get a new copy of this candidate
                    Candidate tmpCopyCandidate = copyCandidate(copyParent1);
                    // insert the i node and check validity
                    tmpCopyCandidate.routeTable.get(k).route.add(j, i);
                    //System.out.println(checkInsertionValidity(tmpCopyCandidate.routeTable.get(k)));
                    if (checkInsertionValidity(tmpCopyCandidate.routeTable.get(k))) {
                        flag = true;
                        tmpCopyCandidate.fitness = evaluateFitnessCandidate(tmpCopyCandidate);
                        //System.out.println(tmpCopyCandidate.fitness);
                        tmpCandidates.add(tmpCopyCandidate);
                    }
                    // if not valid delete this candidate
                    // if valid calculate fitness
                    // store this candidate in new arraylist of new candidates
                }
            }
            if (flag == false) {
                //System.out.println("Hello");
                copyParent1.routeTable.get(copyParent1.routeTable.size() - 1).route.add(i);
                copyParent1.fitness = evaluateFitnessCandidate(copyParent1);
                tmpCandidates.add(copyParent1);
            }
            Collections.sort(tmpCandidates);
            copyParent1 = tmpCandidates.get(0);
        }
    }*/

    /* uniform-order crossover (UOX) */
    public static Chromosome[] UOXcrossover(Chromosome par1, Chromosome par2) {
        int len = par1.size();
        Chromosome[] children = new Chromosome[2];
        children[0] = new Chromosome(false);
        children[1] = new Chromosome(false);

        /* 1. generate mask */
        String mask = Parser.generateMask(len);
        //System.out.println("mask: " + mask);
        /* 2. apply mask */
        for (int i = 0; i < mask.length(); ++i) {
            char ch = mask.charAt(i);
            if (ch == '1') {
                children[0].set(i, par1.get(i));
                children[1].set(i, par2.get(i));
            }
        }

        /* 3. copy the rest */
        // copy from first parent to second child
        for (int parIdx = 0, childIdx = 0; parIdx < len && childIdx < len;) {
            if (mask.charAt(childIdx) == '1') {
                childIdx++;
                continue;
            }
            if (!children[1].contains(par1.get(parIdx))) children[1].set(childIdx++, par1.get(parIdx++));
            else parIdx++;
        }

        // copy from second parent to first child
        for (int parIdx = 0, childIdx = 0; parIdx < len && childIdx < len;) {
            if (mask.charAt(childIdx) == '1') {
                childIdx++;
                continue;
            }
            if (!children[0].contains(par2.get(parIdx))) children[0].set(childIdx++, par2.get(parIdx++));
            else parIdx++;
        }
        children[0].buildTopology();
        children[1].buildTopology();
        children[0].calculateDistance();
        children[1].calculateDistance();

        return children;
    }

    /* constrained route reversal mutation */
    public static Chromosome mutate(Chromosome chromosome) {
        Random rnd = new Random();
        // choose subRoute
        chromosome.buildTopology();
        //System.out.println("number of routes: " + chromosome.getNumOfRoutes());
        for (int i = 0; i < chromosome.getNumOfRoutes(); ++i) {
            //System.out.println(chromosome.getSubRoute(i));
        }
        int subRouteIdx = rnd.nextInt(chromosome.getNumOfRoutes());
        //System.out.println(chromosome);
        //System.out.println("subroute: " + subRouteIdx);
        List<Integer> subRoute = chromosome.getSubRoute(subRouteIdx);

        // choose range
        int start = rnd.nextInt(subRoute.size());
        int len = rnd.nextInt(subRoute.size()-start);

        // update range to
        start = chromosome.indexOf(subRoute.get(start));

        chromosome.reverse(start, start + len);
        chromosome.calculateDistance();

        return chromosome;
    }
}
