package main.evolution;

import main.app.VRPManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rusland on 05.12.17.
 */
public class Population {
    private List<Chromosome> chromosomes;

    public Population() {
        chromosomes = new ArrayList<>();
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

    public Chromosome getFittest() {
        if (chromosomes.size() < 1) return null;
        Chromosome fittest = chromosomes.get(0);
        for (int i = 1; i < getSize(); ++i) {
            if (fittest.getFitness() > get(i).getFitness()) {
                fittest = get(i);
            }
        }
        //System.out.println(fittest==chromosomes.get(0));
        //System.out.println(fittest.getFitness());
        return fittest;
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
}