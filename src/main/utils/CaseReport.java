package main.utils;

import main.evolution.Chromosome;
import main.evolution.GAUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rusland on 18.12.17.
 */
public class CaseReport {
    public Solution bestAverage; // average of best per generations/run
    public List<RunReport> runReports;

    public CaseReport() {
        runReports = new ArrayList<>();
    }

    public void add(RunReport runReport) {
        runReports.add(runReport);
    }

    public void calculateAvgBest() {
        int vehicles = 0;
        double distance = 0;
        double weightedFitness = 0;
        int n = runReports.size();
        for (RunReport runReport: runReports) {
            Chromosome c = runReport.best;
            vehicles += c.getNumOfRoutes();
            distance += c.getDistance();
            weightedFitness += GAUtils.getFitness(c.getNumOfRoutes(), c.getDistance());
        }
        bestAverage = new Solution(vehicles / n, distance / n, weightedFitness / n);
    }
}
