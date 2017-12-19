package main.app;

import main.utils.Parser;
import java.util.List;

/**
 * Created by rusland on 05.12.17.
 */
public class VRPManager {
    private static List<Node> nodes; // both depos and customers
    private static int N;
    private static int vehicleCapacity;
    private static double[][] travelTimeMatrix; // computed without vehicle speed

    public static void init(int n, int aVehicleCapacity, String filePath) {
        VRPManager.N = n;
        nodes = Parser.parse(filePath, N);
        travelTimeMatrix = new double[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                travelTimeMatrix[i][j] = nodes.get(i).distanceTo(nodes.get(j));
            }
        }
        VRPManager.vehicleCapacity = aVehicleCapacity;
    }

    public static double getTravelTime(int from, int to) {
        return travelTimeMatrix[from][to];
    }

    public static Node getNode(int idx) {
        return nodes.get(idx);
    }

    public static Node getDepo() {
        return nodes.get(0);
    }

    public static int getNumOfCustomers() {
        return N-1;
    }
    public static int getNumOfDepos() {
        return 1;
    }

    public static int getVehicleCapacity() {
        return vehicleCapacity;
    }
}
