package main.evolution;

import main.utils.Parser;

import java.util.List;
import java.util.Random;

/**
 * Created by rusland on 18.12.17.
 */
public class GAUtils {
    /* uniform-order crossover (UOX) */
    public static Chromosome[] UOXcrossover(Chromosome par1, Chromosome par2) {
        int len = par1.size();
        Chromosome[] children = new Chromosome[2];
        children[0] = new Chromosome(false);
        children[1] = new Chromosome(false);

        /* 1. generate mask */
        String mask = Parser.generateMask(len);
        //System.out.println("mask: " + mask);
        /* 2. apply mask */
        for (int i = 0; i < mask.length(); ++i) {
            char ch = mask.charAt(i);
            if (ch == '1') {
                children[0].set(i, par1.get(i));
                children[1].set(i, par2.get(i));
            }
        }

        /* 3. copy the rest */
        // copy from first parent to second child
        for (int parIdx = 0, childIdx = 0; parIdx < len && childIdx < len;) {
            if (mask.charAt(childIdx) == '1') {
                childIdx++;
                continue;
            }
            if (!children[1].contains(par1.get(parIdx))) children[1].set(childIdx++, par1.get(parIdx++));
            else parIdx++;
        }

        // copy from second parent to first child
        for (int parIdx = 0, childIdx = 0; parIdx < len && childIdx < len;) {
            if (mask.charAt(childIdx) == '1') {
                childIdx++;
                continue;
            }
            if (!children[0].contains(par2.get(parIdx))) children[0].set(childIdx++, par2.get(parIdx++));
            else parIdx++;
        }
        children[0].buildTopology();
        children[1].buildTopology();

        return children;
    }

    /* constrained route reversal mutation */
    public static Chromosome mutate(Chromosome chromosome) {
        Random rnd = new Random();
        // choose subRoute
        chromosome.buildTopology();
        //System.out.println("number of routes: " + chromosome.getNumOfRoutes());
        for (int i = 0; i < chromosome.getNumOfRoutes(); ++i) {
            //System.out.println(chromosome.getSubRoute(i));
        }
        int subRouteIdx = rnd.nextInt(chromosome.getNumOfRoutes());
        //System.out.println(chromosome);
        //System.out.println("subroute: " + subRouteIdx);
        List<Integer> subRoute = chromosome.getSubRoute(subRouteIdx);

        // choose range
        int start = rnd.nextInt(subRoute.size());
        int len = rnd.nextInt(subRoute.size()-start);

        // update range to
        start = chromosome.indexOf(subRoute.get(start));

        chromosome.reverse(start, start + len);

        return chromosome;
    }
}
