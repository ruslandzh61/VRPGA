package main.utils;

/**
 * Created by rusland on 18.12.17.
 */
public class Solution {
    public int vehicles;
    public double distance;
    public double weightedSum;

    public Solution(int vehicles, double distance, double weightedFitness) {
        this.vehicles = vehicles;
        this.distance = distance;
        this.weightedSum = weightedFitness;
    }
}
