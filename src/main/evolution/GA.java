package main.evolution;

import main.app.VRPManagaer;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 * Created by rusland on 05.12.17.
 */
public class GA {
    private static double crossoverRate;
    private static int elitism; // if elitism is odd number then pick odd population size
    private static int tournamentSize;
    private static double mutationRate;

    public static int getElitism() {
        return elitism;
    }

    public static int getTournamentSize() {
        return tournamentSize;
    }

    public static double getCrossoverRate() {
        return crossoverRate;
    }

    public static void init(double aCrossoverRate, int elitism, int aTournamentSize, double aMutationRate) {
        GA.crossoverRate = aCrossoverRate;
        GA.elitism = elitism;
        GA.tournamentSize = aTournamentSize;
        GA.mutationRate = aMutationRate;
    }

    public static Population evolve(Population pop) {
        /* transform into feasible solution */
        for (int i = 0; i < pop.getSize(); ++i) {
            pop.get(i).buildTopology();
        }

        /* perform elitism operation */
        Population newPop = new Population(VRPManagaer.getN());
        int offset = 0;
        while (offset<elitism) {
            newPop.set(offset++, pop.getFittest());
        }

        Random rnd = new Random();
        Queue<Chromosome> tempPop = new PriorityQueue<>();// utilized for selection
        for (int i = offset; i < pop.getSize(); i += 2) {
            /* perform selection for mating */
            Chromosome parent1 = tournamentSelection(pop);
            Chromosome parent2 = tournamentSelection(pop);
            if (rnd.nextDouble() <= crossoverRate) { // recombination
                Chromosome[] children = crossover(parent1, parent2);
                /* perform survivor selection */
                tempPop = new PriorityQueue<>();

                tempPop.add(children[0]);
                tempPop.add(children[1]);
                tempPop.add(parent1);
                tempPop.add(parent2);

                newPop.set(offset++, mutate(tempPop.poll()));
                newPop.set(offset++, mutate(tempPop.poll()));
            } else {
                newPop.set(offset++, parent1);
                newPop.set(offset++, parent2);
            }
        }
        return newPop;
    }

    private static Chromosome[] crossover(Chromosome c1, Chromosome c2) {
        Chromosome[] children = new Chromosome[2];
        
        return children;
    }

    private static Chromosome mutate(Chromosome chromosome) {
        return chromosome;
    }

    private static Chromosome tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize);
        // For each place in the tournament get a random candidate tour and add it
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.getSize());
            tournament.set(i, pop.get(randomId));
        }
        // Get the fittest tour
        Chromosome fittest = tournament.getFittest();
        return fittest;
    }

}
