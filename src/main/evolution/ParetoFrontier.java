package main.evolution;

import java.util.ArrayList;

/**
 * Created by rusland on 18.12.17.
 */
public class ParetoFrontier {
    public ArrayList<Chromosome> rank;

    public void add(Chromosome c) {
        rank.add(c);
    }

    public int size() {
        return rank.size();
    }
}
