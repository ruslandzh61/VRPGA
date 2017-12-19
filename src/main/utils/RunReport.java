package main.utils;

import main.evolution.Chromosome;
import main.evolution.GAUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rusland on 18.12.17.
 */
public class RunReport {
    public Chromosome best; // best fitness per run and consecutively - in CaseReport average of best per generation/runs
    public double averageWGA; //average population fitness per generation
    public List<GenerationReport> generationReports;

    public RunReport() {
        generationReports = new ArrayList<>();
    }

    public void add(GenerationReport generationReport) {
        generationReports.add(generationReport);
    }

    public void calculateBest() {
        best = generationReports.get(0).best;
        for (GenerationReport gr: generationReports) {
            if (GAUtils.getFitness(best) > GAUtils.getFitness(gr.best)) best = gr.best.copy();
        }
    }
}
