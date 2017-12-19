package main.evolution;

import main.app.VRPManager;

import java.util.*;

/**
 * Created by rusland on 05.12.17.
 */
public class Population {
    List<Chromosome> chromosomes;
    List<ParetoFrontier> frontiers; // for pareto ranking only

    public Population() {
        chromosomes = new ArrayList<>();
        frontiers = new ArrayList<>();
    }

    public Population(int popSize) {
        chromosomes = new ArrayList<>(popSize);
        for (int i = 0; i < popSize; ++i) {
            chromosomes.add(new Chromosome(true));
        }
    }

    public Population(List<Chromosome> pop) {
        chromosomes = new ArrayList<>(pop.size());
        for (int i = 0; i < pop.size(); ++i) {
            chromosomes.add(pop.get(i));
        }
    }

    public Population(Population pop) {
        chromosomes = new ArrayList<>(pop.getSize());
        for (int i = 0; i < pop.getSize(); ++i) {
            chromosomes.add(pop.get(i).copy());
        }
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public Chromosome getFittest() {
        if (chromosomes.size() < 1) {
            return null;
        }
        Chromosome fittest;
        if (Chromosome.isParetoRanking) {
            if (frontiers == null || frontiers.isEmpty()) {
                GAUtils.paretoRanking(this);
            }
            List<Chromosome> set = frontiers.get(0).rank;
            Random rnd = new Random();
            return set.get(rnd.nextInt(set.size()));
        } else {
            fittest = chromosomes.get(0);
            for (int i = 1; i < chromosomes.size(); ++i) {
                if (fittest.getFitness() > chromosomes.get(i).getFitness()) {
                    fittest = chromosomes.get(i);
                    //System.out.println("changed to: " + chromosomes.get(i).getDistance());
                }
            }
        }
        return fittest;
    }

    public ParetoFrontier getFrontier(int i) {
        if (i < 0) {
            return null;
        }

        return frontiers.get(i);
    }

    public void set(int idx, Chromosome chromosome) {
        chromosomes.set(idx, chromosome);
    }

    public Chromosome get(int idx) {
        return chromosomes.get(idx);
    }

    public void add(Chromosome c) {
        chromosomes.add(c);
    }

    public Chromosome remove(int idx) {
        return chromosomes.remove(idx);
    }

    public int getSize() {
        return chromosomes.size();
    }

    public void printPopulation() {
        for (int i = 0; i < chromosomes.size(); ++i) {
            System.out.println(chromosomes.get(i));
        }
    }

    public List<Chromosome> getFittestN(int n) {
        if (chromosomes.size() < 1) return null;
        Chromosome fittest;
        if (Chromosome.isParetoRanking) {
            if (frontiers == null || frontiers.isEmpty()) {
                GAUtils.paretoRanking(this);
            }
            List<Chromosome> result = new ArrayList<>();
            int i = 0;
            while (n > 0) {
                List<Chromosome> set = frontiers.get(i++).rank;
                result.addAll(set.subList(0, Math.min(set.size(), n)));
                n -= set.size();
            }

            return result;
        } else {
            List<Chromosome> c = chromosomes.subList(0, chromosomes.size());
            Collections.sort(c);
            return c.subList(0, n);
        }
    }
}