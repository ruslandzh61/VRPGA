package main.evolution;

/**
 * Created by rusland on 05.12.17.
 */
public class Population {
    private Chromosome[] chromosomes;
    public Population(int n) {
        chromosomes = new Chromosome[n];
    }

    public Chromosome getFittest() {
        return null;
    }

    public void set(int idx, Chromosome chromosome) {

    }

    public Chromosome get(int idx) {
        return chromosomes[idx];
    }

    public int getSize() {
        return chromosomes.length;
    }
}
