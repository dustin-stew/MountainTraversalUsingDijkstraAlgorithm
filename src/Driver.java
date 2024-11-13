import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {

        // construct DrawingPanel, and get its Graphics context
        DrawingPanel panel = new DrawingPanel(840, 480);
        Graphics g = panel.getGraphics();

        // Test Step 1 - construct mountain map data
        MapDataDrawer map = new MapDataDrawer("data/Colorado_840x480.dat", 480, 840);

        // Test Step 2 - min, max, minRow in col
        int min = map.findMinValue();
        System.out.println("Min value in map: " + min);

        int max = map.findMaxValue();
        System.out.println("Max value in map: " + max);

        int minRow = map.indexOfMinInCol(0);
        System.out.println("Row with lowest val in col 0: " + minRow);

        // Test Step 3 - draw the map
        map.drawMap(g);

        // Test Step 4 - draw a greedy path
        g.setColor(new Color(219, 255, 0)); // can set the color of the 'brush' before drawing, then method doesn't need to
        // worry about it
        int totalChange = map.drawLowestElevPath(g, minRow); // use minRow from Step 2 as starting point
        System.out.println(
                "Lowest-Elevation-Change Path starting at row " + minRow + " gives total change of: " + totalChange);

        // Test Step 5 - draw the best path
        int bestRow = map.indexOfLowestElevPath(g);

        totalChange = map.drawLowestElevPath(g, bestRow);
        System.out.println("The Lowest-Elevation-Change Path starts at row: " + bestRow
                + " and gives a total change of: " + totalChange);


        Scanner sc = new Scanner(System.in);


        while (true) {
            System.out.println("\nWould you like to calculate a path using Djikstra's algorithm? Enter y if so. Enter any other key to exit.");
            if (!sc.nextLine().toLowerCase().equals("y")) break;
            int startRow;
            int endRow = 0;
            boolean BestEndRow = false;
            System.out.println("\nTo calculate the most efficient route using Djikstra's algorithm please enter the below values!\n");
            System.out.println("Enter starting row (enter default if you'd like to use the same number from the previous step): ");
            try {
                startRow = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                startRow = bestRow;
            }
            System.out.println("Enter ending row (enter default if you want to use the best ending row): ");
            try {
                endRow = Integer.parseInt(sc.nextLine()) * map.cols - 1;
            } catch (NumberFormatException e) {
                BestEndRow = true;
            }


            int V = map.cols * map.rows;

            int source = map.cols * startRow;

            // Adjacency list representation of the
            // connected edges by declaring List class object
            // Declaring object of type List<Node>
            List<List<Node>> adj
                    = new ArrayList<List<Node>>();

            // Initialize list for every node
            for (int i = 0; i < map.rows; i++) {
                for (int j = 0; j < map.cols; j++) {
                    List<Node> item = new ArrayList<Node>();
                    adj.add(item);
                }


            }

            // Inputs for the GFG(dpq) graph
            for (int i = 0; i < map.rows; i++) {
                for (int j = 0; j < map.cols - 1; j++) {
                    if (i > 0) {
                        adj.get((i) * map.cols + j).add(new Node((i - 1) * map.cols + j + 1, Math.abs(map.grid[i][j] - map.grid[i - 1][j + 1])));
                    }
                    adj.get((i) * map.cols + j).add(new Node((i) * map.cols + j + 1, Math.abs(map.grid[i][j] - map.grid[i][j + 1])));
                    if (i < map.rows - 1) {
                        adj.get((i) * map.cols + j).add(new Node((i + 1) * map.cols + j + 1, Math.abs(map.grid[i][j] - map.grid[i + 1][j + 1])));
                    }
                }

            }


            // Calculating the single source shortest path
            Djikstra djikstra = new Djikstra(V);
            djikstra.algorithm(adj, source);
//
//        // Printing the shortest path to all the nodes
//        // from the source node
//        System.out.println("The shorted path from node :");
            int bestDjikstra = Integer.MAX_VALUE;
            int bestDjikstraIndex = 0;
            int bestDjikstraCoordinate = 0;
            for (int i = 0; i < map.rows; i++) {
                int rowValue = i * map.cols + map.cols - 1;
                if (djikstra.dist[rowValue] < bestDjikstra) {
                    bestDjikstra = djikstra.dist[rowValue];
                    bestDjikstraIndex = i;
                    bestDjikstraCoordinate = rowValue;
                }
                System.out.printf("The best route from %d to %d (%d, %d) is %d\n", source/map.cols, rowValue, i, map.cols, djikstra.dist[rowValue]);
            }
            System.out.printf("The best path is from %d to %d with an elevation change of: %d", source/map.cols, bestDjikstraIndex, bestDjikstra);
            if (BestEndRow) endRow = bestDjikstraCoordinate;
            djikstra.getPath(endRow);
            map.drawDjikstra(g, djikstra.path);


        }
    }

}


// 335163
//
// 334324
//335164
//336004

/*
y
0
d
y
20
d
y
40
d
y
60
d
y
80
d
y
100
d
y
120
d
y
140
d
y
160
d
y
180
d
y
200
d
y
220
d
y
240
d
y
260
d
y
280
d
y
300
d
y
320
d
y
340
d
y
360
d
y
380
d
y
400
d
y
420
d
y
440
d
y
460
d
y
479
d

 */