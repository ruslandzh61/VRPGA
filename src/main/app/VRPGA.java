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
        String[] datasets = {"C101_200.csv", "C202_700.csv", "R101_200.csv",
                "R201_1000.csv", "RC101_200.csv", "RC202_1000.csv"};
        int fitness = 2;
        int customers = 50;
        int n = customers + 1;
        int numOfGenerations = 300;
        int popSize = 250, tournSize = 3, elitismRate = 0;
        double crossoverRate = 0.8, mutationRate = 0.1;
        int crossover = 1;
        double weightedSumAlpha = 100; // for weighted sum: 100
        double weightedSumBeta = 0.001; // for weighted sum approach: 1

        /* BEGINNING OF REPORT */
        writer.write("Report: " + newLine);
        writer.write("GA parameters: " + newLine);
        writer.write("  generation span: " + numOfGenerations);
        writer.write("  population size: " + popSize + newLine);
        writer.write("  tournament size: " + tournSize + newLine);
        writer.write("  elitism: " + elitismRate + newLine);
        writer.write("  crossover rate: " + crossoverRate + newLine);
        writer.write("  mutation rate: " + mutationRate + newLine);

        if (fitness == 1) {
            writer.write("  Fitness evaluation: Pareto Ranking" + newLine);
        } else {
            writer.write("  Fitness evaluation: Weighted Sum" + newLine);
            writer.write("  aplha: " + weightedSumAlpha + newLine);
            writer.write("  beta: " + weightedSumBeta + newLine);
        }

        int runs = 5;
        for (String dataSet: datasets) {
            int vehicleCapacity;
            if (dataSet.charAt(dataSet.length() - 8) != '1')
                vehicleCapacity = Integer.parseInt(dataSet.substring(dataSet.length() - 7, dataSet.length() - 4));
            else vehicleCapacity= Integer.parseInt(dataSet.substring(dataSet.length() - 8, dataSet.length() - 4));
            String filePath = "/Users/rusland/Desktop/Fall2017/EC/Projects/Assign_1/data/" + dataSet;

        /* REPORT PART */
            // data set
            writer.write("data set: " + dataSet + newLine);
            writer.write("vehicle capacity: " + vehicleCapacity + newLine);


            CaseReport caseReport = new CaseReport();
        /* REPORT PART */
            for (int i = 1; i <= runs; ++i) {
                RunReport runReport = new RunReport();
                VRPManager.init(n, vehicleCapacity, filePath);
                if (fitness == 1) {
                    ParetoRankingGA.init(crossover, crossoverRate, elitismRate, tournSize, mutationRate);
                    Chromosome.init(fitness);
                } else {
                    WeightedSumGA.init(crossover, crossoverRate, elitismRate, tournSize, mutationRate);
                    Chromosome.init(fitness, weightedSumAlpha, weightedSumBeta);
                }

                Population pop = new Population(popSize);

            /* REPORT PART */
                GenerationReport gr;
                if (fitness == 1) {
                    GAUtils.paretoRanking(pop);
                    gr = new GenerationReport(pop.getFrontier(0).rank); // all rank 1 chromosomes
                } else {
                    gr = new GenerationReport(pop.getChromosomes());
                }
                // getFitness weighted sum fitness
                // pGA average of pareto
                //writer.write("Generation | Best_Fitness | Average_Fitness | Deviation" + newLine);
                //writer.write("0 " + GAUtils.getFitness(gr.best) + " " + gr.average.weightedSum + " " + gr.wGADeviation + newLine);
            /* REPORT PART END */
                //writer.write("Generation | Best_Fitness | Average_Fitness | Deviation" + newLine);


                for (int j = 1; j <= numOfGenerations; ++j) {
                    if (fitness == 1) {
                        pop = ParetoRankingGA.evolve(pop);
                    } else pop = WeightedSumGA.evolve(pop);

                /* REPORT PART */
                    if (fitness == 1) {
                        gr = new GenerationReport(pop.getFrontier(0).rank); // all rank 1 chromosomes
                    } else {
                        gr = new GenerationReport(pop.getChromosomes());
                    }

                    runReport.add(gr);

                    // getFitness weighted sum fitness
                    // pGA average of pareto

                    //writer.write(j + " " + GAUtils.getFitness(gr.best) + " " + gr.average.weightedSum + " " + gr.wGADeviation + newLine);
                /* REPORT PART END */
                }
                runReport.calculateBest();
                //writer.write("run(" + i + ") | " + "vehicles(" + runReport.best.getNumOfRoutes() + ") | " +
                  //      "distance(" + runReport.best.getDistance() + ") | " + "getFitness(" + GAUtils.getFitness(runReport.best) + ")" + newLine);
                caseReport.add(runReport);
            }

            caseReport.calculateAvgBest();
            //writer.write("case average: vehicles(" + caseReport.bestAverage.vehicles + ") | " +
                   // "distance(" + caseReport.bestAverage.distance + ") | " + "getFitness(" + caseReport.bestAverage.weightedSum + ")" + newLine);
            writer.write(caseReport.bestAverage.vehicles + " " +
                    Math.round(caseReport.bestAverage.distance * 100.0) / 100.0 + " " +
                    Math.round(caseReport.bestAverage.weightedSum * 100.0) / 100.0 + newLine);
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        new VRPGA();
    }
}