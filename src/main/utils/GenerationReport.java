package main.utils;

import main.evolution.Chromosome;
import main.evolution.GAUtils;

import java.util.List;

/**
 * Created by rusland on 18.12.17.
 * GenerationReport stores a generation, best found chromosome, computed average and std deviation
 * per each generation: best fitness value, average population fitness value, standard deviation
 */
public class GenerationReport {
    public Chromosome best; // best chromosome
    public Solution average; // average across whole generation is calculated as a mean of vehicles, mean of distances and mean of weighted sum fitness values
    public double wGADeviation; // deviation across whole generation is calculated as a standard deviation of weighted sum fitness values

    public GenerationReport(List<Chromosome> generation) {
        findBest(generation);
        calculateAverage(generation);
        calculateDeviation(generation);
    }

    void findBest(List<Chromosome> generation) {
        best = generation.get(0);
        double wGA = GAUtils.getFitness(best);
        for (Chromosome ch: generation) {
            if (wGA > GAUtils.getFitness(ch)) best = ch.copy();
        }
    }

    void calculateAverage(List<Chromosome> generation) {
        int vehicles = 0;
        double distance = 0;
        double weightedFitness = 0;
        int n = generation.size();
        for (int i = 0; i < n; ++i) {
            Chromosome c = generation.get(i);
            vehicles += c.getNumOfRoutes();
            distance += c.getDistance();
            weightedFitness += GAUtils.getFitness(c.getNumOfRoutes(), c.getDistance());
        }
        average = new Solution(vehicles / n, distance / n, weightedFitness / n);
    }

    void calculateDeviation(List<Chromosome> generation) {
        double vehicles = 0;
        double distance = 0;
        double weightedFitness = 0;
        int n = generation.size();
        for (int i = 0; i < n; ++i) {
            Chromosome c = generation.get(i);
            vehicles += (c.getNumOfRoutes() - average.vehicles) * (c.getNumOfRoutes() - average.vehicles);
            distance += (c.getDistance() - average.distance) * (c.getDistance() - average.distance);
            double tempWGA = GAUtils.getFitness(c.getNumOfRoutes(), c.getDistance());
            weightedFitness += (tempWGA - average.weightedSum) * (tempWGA - average.weightedSum);
        }
        weightedFitness = Math.sqrt(weightedFitness / n);
        wGADeviation = weightedFitness;
    }
}
