package main.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rusland on 17.12.17.
 */

public class ParetoRankingGA {
    private static double crossoverRate;
    private static int elitism; // if elitism is odd number then pick odd population size
    private static int tournamentSize;
    private static double mutationRate;

    private static List<Chromosome> nextCandidates;
    private static List<Chromosome> matingPool;

    public static void init(double aCrossoverRate, int elitism, int aTournamentSize, double aMutationRate) {
        ParetoRankingGA.crossoverRate = aCrossoverRate;
        ParetoRankingGA.elitism = elitism;
        ParetoRankingGA.tournamentSize = aTournamentSize;
        ParetoRankingGA.mutationRate = aMutationRate;
        nextCandidates = new ArrayList<>();
        matingPool = new ArrayList<>();
    }

    public static Population evolve(Population pop) {
        GAUtils.paretoRanking(pop); // rank population
        for (Chromosome fittest: pop.getFittestN(elitism)) {
            nextCandidates.add(fittest.copy());
        }
        // mating is empty
        tournamentRankSelection(pop); // get ra

        crossover(); // mating pool is processed during crossover and nextCandidates is filled
        mutate(nextCandidates);

        pop = new Population(nextCandidates);
        GAUtils.paretoRanking(pop); // recalculate to get the fittest
        return pop;
    }

    private static void mutate(List<Chromosome> nextCandidates) {
        Random rnd = new Random();
        for (int i = 0; i < nextCandidates.size(); ++i) {
            if (rnd.nextDouble() < mutationRate) GAUtils.mutate(nextCandidates.get(i));
        }
    }



    public static void tournamentRankSelection(Population pop) {
        matingPool = new ArrayList<>();
        for (int i = 0; i < pop.getSize(); i++) {
            Population tournament = new Population(tournamentSize);
            // For each place in the tournament get a random candidate tour and add it
            for (int j = 0; j < tournamentSize; j++) {
                int randomId = (int) (Math.random() * pop.getSize());
                tournament.set(j, pop.get(randomId));
            }
            // Get the fittest tour
            Chromosome fittest = tournament.getFittest();
            matingPool.add(fittest);
            //System.out.println("fittest: " + fittest.getFitness());
        }


    }

    private static void crossover() {
        Random r = new Random();
        nextCandidates = new ArrayList<>();
        int candidateSize = matingPool.size();

        for (int k = 0; k < candidateSize / 2; k++) {
            // randomly choose parents from mating pool
            int parent1Index = r.nextInt(candidateSize);
            int parent2Index = r.nextInt(candidateSize);
            while (parent1Index == parent2Index) {
                parent2Index = r.nextInt(candidateSize);
            }
            Chromosome parent1 = matingPool.get(parent1Index);
            Chromosome parent2 = matingPool.get(parent2Index);

            Chromosome copyParent1 = parent1.copy();
            Chromosome copyParent2 = parent2.copy();
            if (r.nextDouble() < crossoverRate) {
                Chromosome[] children = GAUtils.UOXcrossover(parent1, parent2);
                nextCandidates.add(children[0]);
                nextCandidates.add(children[1]);
            } else {
                nextCandidates.add(copyParent1);
                nextCandidates.add(copyParent2);
            }
        }
    }

    /*private static void crossover() {
        Random r = new Random();
        nextCandidates = new ArrayList<>();
        int candidateSize = matingPool.size();

        for (int k = 0; k < popSize / 2; k++) {
            // randomly choose parents from mating pool
            int parent1Index = r.nextInt(candidateSize);
            int parent2Index = r.nextInt(candidateSize);
            while (parent1Index == parent2Index) {
                parent2Index = r.nextInt(candidateSize);
            }
            Chromosome parent1 = matingPool.get(parent1Index);
            Chromosome parent2 = matingPool.get(parent2Index);

            Chromosome copyParent1 = parent1.copy();
            Chromosome copyParent2 = parent2.copy();
            if (r.nextDouble() < crossoverRate) {
                int chromosomeSize = matingPool.get(0).size();
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

                ArrayList<Integer> replacement1 = new ArrayList<>();
                ArrayList<Integer> replacement2 = new ArrayList<>();

                for (int i = 0; i < chromosomeSize; i++) {
                    replacement1.add(-1);
                    replacement2.add(-1);
                }

                ArrayList<Integer> offspring1Vector = new ArrayList<>();
                ArrayList<Integer> offspring2Vector = new ArrayList<>();
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

                Chromosome offspring1 = new Chromosome(offspring1Vector);
                Chromosome offspring2 = new Chromosome(offspring2Vector);

                nextCandidates.add(offspring1);
                nextCandidates.add(offspring2);
            } else {
                nextCandidates.add(copyParent1);
                nextCandidates.add(copyParent2);
            }
        }

        // copy from mating pool to nextCandidates

        int tmpSize = popSize - nextCandidates.size();
        for (int i = 0; i < tmpSize; i++) {
            nextCandidates.add(matingPool.get(i));
        }
    }*/
}

