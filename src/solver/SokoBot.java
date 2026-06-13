package solver;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class SokoBot {
    String output = "";
    private char[][] mapData;
    private char[][] itemsData;
    private char[][] gameState;
    private boolean canMove = true;
    private boolean boxCanBeMoved = false;
    private int playerRow, playerCol;
    private int moves = 0;

    private ArrayList<Object[]> states = new ArrayList<>();

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
    while (output.length() < 20) {
        int num = rand.nextInt(4);
        moves[num].run();
    }
    return output;
  }

    public ArrayList<int[]> findCrates(char[][] state) {
        ArrayList<int[]> result = new ArrayList<>();
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == '$' || state[i][j] == '/') {
                    result.add(new int[]{j, i});
                }
            }
        }
        return result;
    }

    public ArrayList<int[]> findGoals() {
        ArrayList<int[]> result = new ArrayList<>();
        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length; j++) {
                if (mapData[i][j] == '.') {
                    result.add(new int[]{j, i});
                }
            }
        }
        return result;
    }

    public int computeHeuristic(char[][] state) {
        ArrayList<int[]> crates = findCrates(state);
        ArrayList<int[]> goals = findGoals();

        int totalHeuristicValue = 0;

        for (int i = 0; i < crates.size(); i++) {
            int[] crate = crates.get(i);
            int lowestHeuristicValue = Integer.MAX_VALUE;

            for (int j = 0; j < goals.size(); j++) {
                int[] goal = goals.get(j);
                int heuristicValue = Math.abs(crate[0] - goal[0]) + Math.abs(crate[1] - goal[1]);

                if (heuristicValue < lowestHeuristicValue) {
                    lowestHeuristicValue = heuristicValue;
                }
            }

            totalHeuristicValue += lowestHeuristicValue;
        }

        return totalHeuristicValue;
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
                    playerRow = i;
                    playerCol = j;

                }
                if (itemsData[i][j] == '$') {
                    gameState[i][j] = '$';
                }
            }
        }
    }

    public void relocateEntity(int objRow, int objCol, int dx, int dy, char object) {
        gameState[objRow][objCol] = mapData[objRow][objCol]; // restore underlying tile
        gameState[objRow + dy][objCol + dx] = object;
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


    public void move (int dx, int dy){
        checkNextState(dx, dy);
        if (canMove) {
            if (boxCanBeMoved) relocateEntity(playerRow + dy, playerCol + dx, dx, dy, '$');
            relocateEntity(playerRow, playerCol, dx, dy, '@');
            updateBoxDisplay();
            playerRow += dy;
            playerCol += dx;
            moves++;
        }


    }

    public void updateGameState(String input){
        int f = moves + computeHeuristic(gameState);
        switch (input) {
            case "u" -> move(0, -1);
            case "d" -> move(0, 1);
            case "r" -> move(1, 0);
            case "l" -> move(-1, 0);
        }
        displayGameState();

        states.add(new Object[]{gameState, f});
        System.out.println("player's move: " + input + " | g: " + moves + " | h: " + computeHeuristic(gameState) + " | " + "f:" + f);

        System.out.println("---------------------------------------------");
        System.out.println("\n");

        if(isSolved()) {
            System.out.println("Solved!");
        }
    }

    public void checkNextState(int x, int y) {
        canMove = true; // reset these
        boxCanBeMoved = false;
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

    public boolean isSolved() {
        for(int i = 0; i < mapData.length; i++) {
            for(int j = 0; j < mapData[0].length; j++) {
                if(mapData[i][j] == '.') {
                    if(gameState[i][j] != '$') {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}