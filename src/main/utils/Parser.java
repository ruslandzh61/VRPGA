package main.utils;

import main.app.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rusland on 05.12.17.
 */
public class Parser {

    public static List<Node> parse(String csvFile, int n) {
        List<Node> customers = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            customers.add(null);
        }
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int i = -1;
            while ((line = br.readLine()) != null && i < n) {
                // skip first line
                if (i < 0) {
                    i++;
                    continue;
                }

                // use comma as separator
                String[] customer = line.split(cvsSplitBy);
                customers.set(i, new Node(Integer.parseInt(
                        customer[0]), Double.parseDouble(customer[1]), Double.parseDouble(customer[2]),
                        Double.parseDouble(customer[3]), Double.parseDouble(customer[4]),
                        Double.parseDouble(customer[5]), Double.parseDouble(customer[6])));
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < customers.size(); ++i) {
            //System.out.println(customers.get(i));
        }
        return customers;
    }

    public static String generateMask(int len) {
        Random rnd = new Random();
        String mask = "";
        for (int i = 0; i < len; ++i) {
            int ch = rnd.nextInt(2);
            mask += Integer.toString(ch);
        }
        return mask;
    }
}
