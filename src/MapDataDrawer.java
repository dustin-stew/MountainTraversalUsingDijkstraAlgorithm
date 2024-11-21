import java.util.*;
import java.io.*;
import java.awt.*;

public class MapDataDrawer {

  public int[][] grid; // map
  public int rows; // number of rows
  public int cols; // number of columns
  private int max = 0; // max value determined in constructor
  private int min = Integer.MAX_VALUE; // min value determined in constructor
  private int maxIndexRow = 0;
  private int maxIndexCol = 0;
  private int minIndexRow = 0;
  private int minIndexCol = 0;
  int[] lastMoves; // the moves of the previous path for drawing paths
  int[] bestMoves; // the moves of the best moves for redrawing in different color

  public MapDataDrawer(String filename, int rows, int cols) {
    this.grid = new int[rows][cols];
    this.rows = rows;
    this.cols = cols;
    this.lastMoves = new int[cols - 1];

    // read in file
    try {
      File myObj = new File(filename);
      Scanner myReader = new Scanner(myObj);
      int row = 0;
      while (myReader.hasNextInt()) {
        int[] tempArray = new int[cols];
        for (int i = 0; i < tempArray.length; i++) {
          tempArray[i] = myReader.nextInt();
        }
        grid[row] = tempArray;
        tempArray = new int[cols];

        row += 1;
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }


  }

  /**
   * @return the min value in the entire grid
   */
  public int findMinValue() {
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        if (this.grid[i][j] < min) {
          this.minIndexRow = i;
          this.minIndexCol = j;
          this.min = this.grid[i][j];
        }
      }
    }
    return min;
  }

  /**
   * @return the max value in the entire grid
   */
  public int findMaxValue() {
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        if (this.grid[i][j] > max) {
          this.max = this.grid[i][j];
          this.maxIndexRow = i;
          this.maxIndexCol = j;
        }
      }
    }
    return max;
  }

  /**
   * @param col the column of the grid to check
   * @return the index of the row with the lowest value in the given col for the grid
   */
  public int indexOfMinInCol(int col) {
    int columnMin = Integer.MAX_VALUE;
    int rowIndex = 0;

    // iterate through each row for that column and get value, return the minimum
    for (int i = 0; i < this.rows; i++) {
      if (this.grid[i][col] < columnMin) {
        columnMin = this.grid[i][col];
        rowIndex = i;
      }
    }
    return rowIndex;
  }

  /**
   * Draws the grid using the given Graphics object.
   * Colors should be grayscale values 0-255, scaled based on min/max values in grid
   */
  public void drawMap(Graphics g) {
    int rangeValues = max - min;

    // iterate through each value, applying the ratio of elevations
    // to the ratio of RGB colors (255)
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        float ratio = (this.grid[i][j] - min) / (float) rangeValues;
        int c = Math.round(255 * ratio); // calculated grayscale value
        g.setColor(new Color(c, c, c)); // set color
        g.fillRect(j, i, 1, 1); // color rectangle
      }
    }


  }

  /**
   * Find a path from West-to-East starting at given row.
   * Choose a foward step out of 3 possible forward locations, using greedy method described in assignment.
   *
   * @return the total change in elevation traveled from West-to-East
   */

  public int drawLowestElevPath(Graphics g, int startRow) {
    g.setColor(new Color(241, 34, 143, 51)); // default color for all paths

    int totalElevationChange = 0;
    int currentCol = 0;
    int currentRow = startRow;
    Random rand = new Random(); // for coin flip

    // while we still have columns to iterate through
    // calculate optimal next row
    while (currentCol < this.cols - 1) {
      int upChange;
      int downChange;
      int bestMove = 0;

      int currentElevation = grid[currentRow][currentCol]; // where do we start this iteration
      int forwardChange = Math.abs(currentElevation - grid[currentRow][currentCol + 1]); // forward is always a possiblity

      // for row 0, we can't go up, so we set the upchange equal to forward change because it can never beat forward change
      if (currentRow == 0) {
        upChange = forwardChange;
      } else {
        upChange = Math.abs(currentElevation - grid[currentRow - 1][currentCol + 1]);
      }

      // for row 479 we can't go down, so we set downchange to forward change because it can never beat fwd change
      if (currentRow == 479) {
        downChange = forwardChange;
      } else {
        downChange = Math.abs(currentElevation - grid[currentRow + 1][currentCol + 1]);
      }

      int lowest = Math.min(forwardChange, Math.min(upChange, downChange)); // get the lowest value of the three to make it simpler

      // If lowest equals forward change, it doesn't matter what the other values are
      // Moves are just integers -1, 0, 1 and indicate the change in row value - column is always the same
      if (lowest == forwardChange) {
        totalElevationChange += forwardChange;
      } else if (lowest == upChange) {
        // If lowest equals both upchange and downchange then it's a coin toss,
        // else the one that is lowest is the best move
        if (lowest == downChange) {
            if (rand.nextInt(2) == 1) {
              bestMove = 1;
              totalElevationChange += downChange;
          } else {
            bestMove = -1;
            totalElevationChange += upChange;
          }
        } else {
          bestMove = -1;
          totalElevationChange += upChange;
        }
      } else {
        bestMove = 1;
        totalElevationChange += downChange;
      }

      // Saving the last path moves for being able to accurately
      // draw the same path again when we find the optimal path
      this.lastMoves[currentCol] = bestMove;

      // move to new row, column
      currentRow = currentRow + bestMove;
      currentCol++;


      // fill in the rectangle for our new location
      g.fillRect(currentCol, currentRow, 1, 1);

    }

    return totalElevationChange;

  }

  /**
   * @return the index of the starting row for the lowest-elevation-change path in the entire grid.
   */
  public int indexOfLowestElevPath(Graphics g) {
    int bestPathIndex = 0;
    int bestPathValue = Integer.MAX_VALUE; // first path will always be lower

    // iterate through each row, running the drawLowestElevPath each time
    for (int i = 0; i < rows; i++) {

      int elevChange = drawLowestElevPath(g, i);

      // save best row number
      if (elevChange < bestPathValue) {
        bestPathValue = elevChange;
        bestPathIndex = i;


        // save best moves as the last moves if it's the best row
        // (so we can redraw the same path rather than another random one)
        bestMoves = lastMoves;
      }
    }

    // Set color to "best" color
    g.setColor(new Color(0, 89, 255));
    // move to best row
    int currentRow = bestPathIndex;

    // redraw path with "best" color
    for (int j = 0; j < lastMoves.length; j++) {
      currentRow = currentRow + bestMoves[j];
      g.fillRect(j + 1, currentRow, 1, 1);

    }

    return bestPathIndex;


  }

  // Below method is for Dijkstra implementation
  public void drawDijkstra(Graphics g, ArrayList<Integer> path) {
    g.setColor(new Color(219, 255, 0));
    for (int i = 0; i < path.size(); i++) {
      int r = path.get(i)/cols - 1;
      int c = path.get(i)%cols;
      g.fillRect(c, r, 1, 1);
    }
  }
}