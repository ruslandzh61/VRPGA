package main.app;

import main.evolution.*;
import main.utils.Parser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rusland on 05.12.17.
 */
public class VRPGA {
    public VRPGA() throws IOException {
        // generate report variable
        String newLine = "\n";
        String fileName = "report.txt";
        String tempFile = "results.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        boolean isParetoRanking = false;
        int depos = 1;
        int customers = 50;
        int n = depos + customers, vehicleCapacity = 200;
        int numOfGenerations = 350;
        int popSize = 300, tournSize = 3, elitismRate = 5;
        double crossoverRate = 0.8, mutationRate = 0.1;
        double weightedSumAlpha = 100; // for weighted sum: 100
        double weightedSumBeta = 0.001; // for weighted sum approach: 1

        int runs = 1;

        writer.write("Report: " + newLine);
        // data set
        writer.write("data set: C201_200.csv" + newLine);
        writer.write("type: narrow window - clustered" + newLine);
        writer.write("vehicle capacity: " + vehicleCapacity + newLine);

        writer.write("GA parameters: " + newLine);
        writer.write("  generation span: " + numOfGenerations);
        writer.write("  population size: " + popSize + newLine);
        writer.write("  tournament size: " + tournSize + newLine);

        writer.write("  elitism: " + elitismRate + newLine);
        writer.write("  crossover rate: " + crossoverRate + newLine);
        writer.write("  mutation rate: " + mutationRate + newLine);

        if (isParetoRanking) {
            writer.write("Fitness evaluation: Pareto Ranking");
        } else {
            writer.write("Fitness evaluation: Weighted Sum");
            writer.write("aplha: " + weightedSumAlpha);
            writer.write("beta: " + weightedSumBeta);
        }

        for (int i = 0; i < runs; ++i) {
            VRPManager.init(n, vehicleCapacity);
            if (isParetoRanking) {
                ParetoRankingGA.init(crossoverRate, elitismRate, tournSize, mutationRate);
                Chromosome.init(isParetoRanking);
            } else {
                WeightedSumGA.init(crossoverRate, elitismRate, tournSize, mutationRate);
                Chromosome.init(isParetoRanking, weightedSumAlpha, weightedSumBeta);
            }

            Population pop = new Population(popSize);
            System.out.println("Generation #0" + " distance: " +
                    pop.getFittest().getDistance() + " " + pop.getFittest().getNumOfRoutes());

            System.out.println("evolution:");
            for (int j = 1; j <= numOfGenerations; ++j) {
                if (isParetoRanking) {
                    pop = ParetoRankingGA.evolve(pop);
                } else pop = WeightedSumGA.evolve(pop);
                Chromosome fittest = pop.getFittest();
                //pop.printPopulation();
                System.out.println("Generation #" + j + " distance:  " + fittest.getDistance() + " " + fittest.getNumOfRoutes());
            }
        }

        writer.close();
    }

    public static void main(String[] args) throws IOException {
        new VRPGA();
    }
}


/* GenerationReport stores a generation, best found chromosome, computed average and std deviation
* */
class GenerationReport {
    Chromosome bestChromosome;
    Solution average;
    Solution standardDeviation;
    Population generation;

    GenerationReport(Population aGeneration) {
        generation = new Population(aGeneration);
    }
    void findBest() {
        bestChromosome = generation.getFittest().copy();
    }
    void calculateAverage() {
        for (int i = 0; i < generation.getSize(); ++i) {

        }
    }
    void calculateDeviation() {}
}

/* stores all generations bestChromosome, avg best over all generations*/
class RunReport {
    Chromosome bestChromosome;
    Solution best;
    void calculate() {
        // iterate through generations
    }
}

/* holds informaiton for multiple runs */
class CaseReport {
    Solution bestAverage;

    void calculateBestAverage() {
        // iterate through all runs and find average of best solutions
    }

    void calculateAverage() {
        // iterate through all runs and consequitevely all generations in each run
    }
}

class Solution {
    int vehicles;
    double distance;
    double weightedFitness;
}
