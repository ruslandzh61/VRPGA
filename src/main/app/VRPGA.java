package main.app;

import main.evolution.GA;
import main.evolution.Population;
import main.utils.Parser;

/**
 * Created by rusland on 05.12.17.
 */
public class VRPGA {
    public VRPGA() {
        int n = 25, vehicleCapacity = 200;
        int popSize=200, tournSize=3, elitismRate = 0;
        double crossoverRate = 0.85, mutationRate = 0.15;
        VRP.init(n, vehicleCapacity);
        GA.init(crossoverRate, elitismRate, tournSize, mutationRate);

        Population pop = new Population(popSize);
        System.out.println("Initial distance: " + pop.getFittest().getFitness());
        pop = GA.evolve(pop);
        for (int i = 0; i < 100; ++i) {
            pop = GA.evolve(pop);
            System.out.println("Generation #" + i + ": " + pop.getFittest().getFitness());
        }
    }

    public static void main(String[] args) {
        VRPGA problem = new VRPGA();
    }
}
