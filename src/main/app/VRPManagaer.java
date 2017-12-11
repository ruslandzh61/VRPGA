package main.app;

import main.utils.Parser;

/**
 * Created by rusland on 05.12.17.
 */
public class VRPManagaer {
    private static Node[] customers;
    private static int N;
    private static int vehicleCapacity;

    public static void init(int n, int aVehicleCapacity) {
        VRPManagaer.N = n;
        customers = Parser.parse("CSVFilePath", N);
        VRPManagaer.vehicleCapacity = aVehicleCapacity;
    }

    public static Node getCustomer(int idx) {
        return customers[idx];
    }

    public static int getN() {
        return N;
    }

    public static int getVehicleCapacity() {
        return vehicleCapacity;
    }
}
