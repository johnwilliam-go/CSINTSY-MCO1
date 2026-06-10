package solver;
import java.util.ArrayList;
import java.util.Random;

public class SokoBot {
    String output = "";
    private char[][] mapData;
    private char[][] itemsData;
    private char gameState[][];
    private boolean canMove = true;
    private boolean boxCanBeMoved = false;

    ArrayList<int[]> crates = new ArrayList<>();
    ArrayList<int[]> goals = new ArrayList<>();
    ArrayList<Object> lowestHeuristic = new ArrayList<>();

    ArrayList<Object[]> heuristics = new ArrayList<>();
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        gameState = new char[height][width];
        this.mapData = mapData;
        this.itemsData = itemsData;
      /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */
    try {
      Thread.sleep(200);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    initializeGameState();


    Runnable[] moves = {
            () -> up(),
            () -> down(),
            () -> left(),
            () -> right()
    };

    // THIS IS TEMPORARY SOLUTION
    // THIS IS TO TEST THE DEADLOCK

    displayGameState();
    Random rand = new Random();
    while (output.length() < 500) {
        int num = rand.nextInt(4);
        moves[num].run();
    }
    return output;
  }

  public void showPlayerCoordinate(){
      crates.clear();
      goals.clear();
      heuristics.clear();
      lowestHeuristic.clear();
      for(int i = 0; i < itemsData.length; i++) {
          for(int j = 0; j < itemsData[0].length; j++) {
              if(gameState[i][j] == '$' || gameState[i][j] == '/') {
                  crates.add(new int[]{j, i});
              }
              if(mapData[i][j] == '.') {
                  goals.add(new int[]{j, i});
              }
          }
      }

      for (int i = 0; i < crates.size(); i++) {
          int[] crate = crates.get(i);

          // System.out.println("Heuristics for crate (" +  crate[0] + "," + crate[1] + ") based on nearest goal"); // remove comment for debugging

          for(int j = 0; j < goals.size(); j++) {
              int[] goal = goals.get(j);
              int heuristicValue = Math.abs(crate[0] - goal[0]) + Math.abs(crate[1] - goal[1]);
              String selectedCrate = "(" + crate[0] + "," + crate[1] + ")";
              String selectedGoal = "(" + goal[0] + "," + goal[1] + ")";
              heuristics.add(new Object[]{
                      selectedCrate,
                      selectedGoal,
                      heuristicValue
              });

              // System.out.println(selectedGoal + "-" + heuristicValue); // remove comment for debugging

          }

          Object[] lowestHeuristicValueOfEachGoal = heuristics.getFirst();

          for (Object[] heuristic : heuristics) {
              int value = (Integer) heuristic[2];
              if (value < (Integer) lowestHeuristicValueOfEachGoal[2]) {
                  lowestHeuristicValueOfEachGoal = heuristic;
              }
          }
          lowestHeuristic.add(lowestHeuristicValueOfEachGoal);

          heuristics.clear(); // clear the temporary arraylist to check another crate's lowest heuristic to the goal
      }

      System.out.println("\n");
      System.out.println("Crate - Goal - Heuristic Value");
      int totalHeuristicValue = 0;

      for (Object o : lowestHeuristic) {
          Object[] object = (Object[]) o;
          System.out.println(object[0] + " - " + object[1] + " - " + object[2]);
          totalHeuristicValue += (Integer) object[2];
      }
      System.out.println("Total Heuristic : " + totalHeuristicValue);
  }

    public void up(){
        output += "u";
        updateGameState("u");
    }

    public void down(){
        output += "d";
        updateGameState("d");
    }

    public void left(){
        output += "l";
        updateGameState("l");
    }

    public void right(){
        output += "r";
        updateGameState("r");
    }

    public void printItems(){
        for(int i = 0; i < mapData.length; i++) {
            for(int j = 0; j < mapData[0].length; j++) {
                System.out.print(mapData[i][j]);
            }
            System.out.println();
        }
        for(int i = 0; i < itemsData.length; i++) {
            for(int j = 0; j < itemsData[0].length; j++) {
                System.out.print(itemsData[i][j]);
            }
            System.out.println();
        }
    }

    public void initializeGameState(){
        for(int i = 0; i < mapData.length; i++) {
            for(int j = 0; j < mapData[0].length; j++) {
                gameState[i][j] = mapData[i][j];
            }
        }
        for(int i = 0; i < itemsData.length; i++) {
            for(int j = 0; j < itemsData[0].length; j++) {
                if (itemsData[i][j] == '@') {
                    gameState[i][j] = '@';
                }
                if (itemsData[i][j] == '$') {
                    gameState[i][j] = '$';
                }
            }
        }
    }

    public void relocateEntity(int x, int y, char object) {
        for (int i = 0; i < gameState.length; i++) {
            for (int j = 0; j < gameState[0].length; j++) {
                if (gameState[i][j] == object || (object == '$' && gameState[i][j] == '/')) {
                    gameState[i][j] = mapData[i][j]; // restore underlying tile
                    gameState[i + y][j + x] = object;
                    return;
                }
            }
        }
    }



    public void displayGameState(){
        for(int i = 0; i < itemsData.length; i++) {
            for(int j = 0; j < itemsData[0].length; j++) {
                System.out.print(gameState[i][j]);
            }
            System.out.println();
        }
    }

    public void updateBoxDisplay() {
        for (int i = 0; i < gameState.length; i++) {
            for (int j = 0; j < gameState[0].length; j++) {
                if (gameState[i][j] == '$' && mapData[i][j] == '.') {
                    gameState[i][j] = '/';
                } else if (gameState[i][j] == '/' && mapData[i][j] != '.') {
                    gameState[i][j] = '$';
                }
            }
        }
    }

    public void updateGameState(String input){
        canMove = true;
        boxCanBeMoved = false;

        switch (input) {
            case "u" -> {
                checkNextState(0, -1);
                if (canMove) {
                    if (boxCanBeMoved) relocateEntity(0, -1, '$');
                    relocateEntity(0, -1, '@');
                    updateBoxDisplay();
                }
            }
            case "d" -> {
                checkNextState(0, 1);
                if (canMove) {
                    if (boxCanBeMoved) relocateEntity(0, 1, '$');
                    relocateEntity(0, 1, '@');
                    updateBoxDisplay();
                }
            }
            case "r" -> {
                checkNextState(1, 0);
                if (canMove) {
                    if (boxCanBeMoved) relocateEntity(1, 0, '$');
                    relocateEntity(1, 0, '@');
                    updateBoxDisplay();
                }
            }
            case "l" -> {
                checkNextState(-1, 0);
                if (canMove) {
                    if (boxCanBeMoved) relocateEntity(-1, 0, '$');
                    relocateEntity(-1, 0, '@');
                    updateBoxDisplay();
                }
            }
        }
        displayGameState();
        System.out.println(input);
        showPlayerCoordinate();
        System.out.println("---------------------------------------------");
        System.out.println("\n");
    }

    public void checkNextState(int x, int y) {
        for (int i = 0; i < gameState.length; i++) {
            for (int j = 0; j < gameState[0].length; j++) {
                if (gameState[i][j] == '@') {
                    // INPUT YOUR CODE HERE

                    int nextVertical = i + y;    // if y = negative, it checks the left     if y = positive, it checks the right
                    int nextHorizontal = j + x;  // if x = negative, it checks the top      if x = positive,  it checks the bottom
                    int doubleNextVertical = i + 2 * y;   // checks 2 units to the left or right
                    int doubleNextHorizontal = j + 2 * x; // checks 2 units up and down
                    // you can initalize more variables
                    // example deadlock situation,
                    // #  #
                    // #@$ #
                    // #
                    // if player moves one time to the right, a move is allowed, but the box will be stuck there forever
                    // soo we make initialize another variable
                    //
                    // HINT:
                    // int tripleNextHorizontal = j + 3 * x  (this will check 3 units to the right or left)  (you are probably gonna use this)

                    // if you find deadlock, simply make do " canMove = false; "


                    //this prevents you from exiting the map cuz it will cause an array out of bounds index error type shit
                    if (nextVertical < 0 || nextVertical >= gameState.length || nextHorizontal < 0 || nextHorizontal >= gameState[0].length) {
                        canMove = false;
                        return;
                    }

                    // checking if ur next move is a wall
                    if (gameState[nextVertical][nextHorizontal] == '#') {
                        canMove = false;

                    // checking if ur next move is facing a box
                    // '$' for box not in the goal
                    // '/' for box in the goal
                    } else if (gameState[nextVertical][nextHorizontal] == '$' || gameState[nextVertical][nextHorizontal] == '/') {

                        // if you are actually facing a box....

                        // checks if the player and the box are next to a wall
                        // @$#
                        if (doubleNextVertical < 0 || doubleNextVertical >= gameState.length || doubleNextHorizontal < 0 || doubleNextHorizontal >= gameState[0].length) {
                            canMove = false;
                            return;
                        }
                        // checks player if the box is also next to a box
                        // @$$
                        if (gameState[doubleNextVertical][doubleNextHorizontal] == '$' || gameState[doubleNextVertical][doubleNextHorizontal] == '/'
                                || gameState[doubleNextVertical][doubleNextHorizontal] == '#') {
                            canMove = false;

                        // if not, the state must be like this: @$ #
                        // this means a move is possible
                        } else {
                            boxCanBeMoved = true;
                        }
                    }
                }
            }
        }
    }

    public void deadlockDetection(){
        // implement your code here (if you want to create a method instead)
        // if not its fine you can add stuff in the checkNextState
    }
}
