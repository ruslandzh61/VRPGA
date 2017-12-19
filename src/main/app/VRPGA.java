package main.app;

import main.evolution.*;
import main.utils.CaseReport;
import main.utils.GenerationReport;
import main.utils.RunReport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
        int customers = 25;
        int n = customers + 1, vehicleCapacity = 200;
        int numOfGenerations = 100;
        int popSize = 300, tournSize = 3, elitismRate = 0;
        double crossoverRate = 0.8, mutationRate = 0.1;
        double weightedSumAlpha = 100; // for weighted sum: 100
        double weightedSumBeta = 0.001; // for weighted sum approach: 1

        int runs = 5;

        writer.write("Report: " + newLine);
        // data set
        writer.write("  data set: C201_200.csv" + newLine);
        writer.write("  type: narrow window - clustered" + newLine);
        writer.write("  vehicle capacity: " + vehicleCapacity + newLine);

        writer.write("GA parameters: " + newLine);
        writer.write("  generation span: " + numOfGenerations);
        writer.write("  population size: " + popSize + newLine);
        writer.write("  tournament size: " + tournSize + newLine);

        writer.write("  elitism: " + elitismRate + newLine);
        writer.write("  crossover rate: " + crossoverRate + newLine);
        writer.write("  mutation rate: " + mutationRate + newLine);

        if (isParetoRanking) {
            writer.write("  Fitness evaluation: Pareto Ranking" + newLine);
        } else {
            writer.write("  Fitness evaluation: Weighted Sum" + newLine);
            writer.write("  aplha: " + weightedSumAlpha + newLine);
            writer.write("  beta: " + weightedSumBeta + newLine);
        }
        CaseReport caseReport = new CaseReport();
        for (int i = 1; i <= runs; ++i) {
            RunReport runReport = new RunReport();
            VRPManager.init(n, vehicleCapacity);
            if (isParetoRanking) {
                ParetoRankingGA.init(crossoverRate, elitismRate, tournSize, mutationRate);
                Chromosome.init(isParetoRanking);
            } else {
                WeightedSumGA.init(crossoverRate, elitismRate, tournSize, mutationRate);
                Chromosome.init(isParetoRanking, weightedSumAlpha, weightedSumBeta);
            }

            Population pop = new Population(popSize);

            /* REPORT PART */
            GenerationReport gr;
            if (isParetoRanking) {
                gr = new GenerationReport(pop.getFrontier(0).rank); // all rank 1 chromosomes
            } else {
                gr = new GenerationReport(pop.getChromosomes());
            }
            // getFitness weighted sum fitness
            // pGA average of pareto
            writer.write("Generation | Best_Fitness | Average_Fitness | Deviation" + newLine);
            writer.write("0 " + GAUtils.getFitness(gr.best) + " " + gr.average.weightedSum + " " + gr.wGADeviation + newLine);
            /* REPORT PART END */
            writer.write("Generation | Best_Fitness | Average_Fitness | Deviation" + newLine);


            for (int j = 1; j <= numOfGenerations; ++j) {
                if (isParetoRanking) {
                    pop = ParetoRankingGA.evolve(pop);
                } else pop = WeightedSumGA.evolve(pop);

                /* REPORT PART */
                if (isParetoRanking) {
                    gr = new GenerationReport(pop.getFrontier(0).rank); // all rank 1 chromosomes
                } else {
                    gr = new GenerationReport(pop.getChromosomes());
                }

                runReport.add(gr);

                // getFitness weighted sum fitness
                // pGA average of pareto

                writer.write(j + " " + GAUtils.getFitness(gr.best) + " " + gr.average.weightedSum + " " + gr.wGADeviation + newLine);
                /* REPORT PART END */
            }
            runReport.calculateBest();
            writer.write("run(" + i + ") | " + "vehicles(" + runReport.best.getNumOfRoutes() + ") | " +
                    "distance(" + runReport.best.getDistance() + ") | " + "getFitness(" + GAUtils.getFitness(runReport.best) + ")" + newLine);
            caseReport.add(runReport);
        }

        caseReport.calculateAvgBest();
        writer.write("case average: vehicles(" + caseReport.bestAverage.vehicles + ") | " +
                "distance(" + caseReport.bestAverage.distance + ") | " + "getFitness(" + caseReport.bestAverage.weightedSum + ")" + newLine);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        new VRPGA();
    }
}