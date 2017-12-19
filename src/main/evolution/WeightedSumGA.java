package main.evolution;

import main.utils.Parser;

import java.util.*;

/**
 * Created by rusland on 05.12.17.
 */
public class WeightedSumGA {
    private static double crossoverRate;
    private static int elitism; // if elitism is odd number then pick odd population size
    private static int tournamentSize;
    private static double mutationRate;

    private static List<Chromosome> matingPool;

    public static void init(double aCrossoverRate, int elitism, int aTournamentSize, double aMutationRate) {
        WeightedSumGA.crossoverRate = aCrossoverRate;
        WeightedSumGA.elitism = elitism;
        WeightedSumGA.tournamentSize = aTournamentSize;
        WeightedSumGA.mutationRate = aMutationRate;
        matingPool = new ArrayList<>();
    }

    public static Population evolve(Population pop) {
        matingPool = new ArrayList<>();
        /* transform into feasible solution + fitness phase */
        for (int i = 0; i < pop.getSize(); ++i) {
            pop.get(i).buildTopology();
        }

        /* perform elitism operation */
        Population newPop = new Population(pop.getSize());
        int offset = 0;
        List<Chromosome> fits = pop.getFittestN(elitism);
        for (Chromosome fittest: fits) {
            newPop.set(offset++, fittest.copy());
        }

        Random rnd = new Random();
        // selecting mating pool
        for (int i = offset; i < pop.getSize(); i++) {
            matingPool.add(tournamentSelection(pop));
        }

        // crossover
        for (int i = offset; i < matingPool.size(); i += 2) {
            if (rnd.nextDouble() <= crossoverRate) { // recombination
                Chromosome[] children = crossover(matingPool.get(i), matingPool.get(i + 1));
                newPop.set(offset++, children[0]);
                newPop.set(offset++, children[1]);
            } else {
                newPop.set(offset++, matingPool.get(i));
                newPop.set(offset++, matingPool.get(i + 1));
            }
        }

        // mutation
        for (int i = elitism; i < newPop.getSize(); i++) {
            if (rnd.nextDouble() < mutationRate) GAUtils.mutate(newPop.get(i));
        }

        return newPop;
    }

    private static Chromosome[] crossover(Chromosome par1, Chromosome par2) {
        return GAUtils.UOXcrossover(par1, par2);
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

