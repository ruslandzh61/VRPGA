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
    private static int crossover;

    private static List<Chromosome> matingPool;

    public static void init(int aCrossover, double aCrossoverRate, int elitism, int aTournamentSize, double aMutationRate) {
        WeightedSumGA.crossover =  aCrossover;
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
        int candidateSize = matingPool.size();

        // crossover
        for (int i = offset; i < matingPool.size(); i ++) {
            // randomly choose parents from mating pool
            int parent1Index = i;//rnd.nextInt(candidateSize);
            int parent2Index = i + 1;//rnd.nextInt(candidateSize);
            if (parent2Index == matingPool.size()) parent2Index = offset;
            while (parent1Index == parent2Index) {
                parent2Index = rnd.nextInt(candidateSize);
            }
            Chromosome parent1 = matingPool.get(parent1Index);
            Chromosome parent2 = matingPool.get(parent2Index);

            if (rnd.nextDouble() <= crossoverRate) { // recombination
                Chromosome child = crossover(parent1, parent2);
                newPop.set(offset++, child);
            } else {
                newPop.set(offset++, parent1);
            }
        }

        // mutation
        for (int i = elitism; i < newPop.getSize(); i++) {
            if (rnd.nextDouble() < mutationRate) GAUtils.mutate(newPop.get(i));
        }

        return newPop;
    }

    private static Chromosome crossover(Chromosome par1, Chromosome par2) {
        if (crossover == 1) {
            System.out.println("uox");
            return GAUtils.UOXcrossover(par1, par2);
        }
        return GAUtils.partiallyMappedCrossover(par1, par2);
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

