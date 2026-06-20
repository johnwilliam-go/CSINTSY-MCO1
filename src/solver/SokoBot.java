package solver;
import java.util.ArrayList;

public class SokoBot {
    String output = "";
    private char[][] mapData;
    private char[][] itemsData;
    private char[][] gameState;
    private boolean canMove = true;
    private boolean boxCanBeMoved = false;
    private int playerRow, playerCol;
    private int moves = 0;


    private ArrayList<char[][]> board = new ArrayList<>();
    private ArrayList<int[]> playerLocaton = new ArrayList<>();

    private ArrayList<String> paths = new ArrayList<>();

    private ArrayList<char[][]> visitedBoards = new ArrayList<>();
    private ArrayList<int[]> visitedPlayers = new ArrayList<>();

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
        initializeGameState();

        board.add(copyState(gameState));
        playerLocaton.add(new int[]{playerRow, playerCol});
        paths.add("");

        visitedBoards.add(copyState(gameState));
        visitedPlayers.add(new int[]{playerRow, playerCol});
        //           up   down  left  right
        int[] dx = {  0  ,  0,   -1,    1};
        int[] dy = { -1  ,  1,    0,    0};
        char[] direction = {'u', 'd', 'l', 'r'};

        // implement bfs here

        return "";
    }

    public boolean checkIfBoardsAreEqual(char[][] a, char[][] b) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (a[i][j] != b[i][j]) { //check if the next board is the same as the previously stored board
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isVisitedByPlayer(char[][] nextBoard, int[] nextPlayerPosition) {
        for (int i = 0; i < visitedBoards.size(); i++) {
            int[] player = visitedPlayers.get(i); // check if player visited that coordinate
            if(
                    player[0] == nextPlayerPosition[0] &&
                    player[1] == nextPlayerPosition[1] &&
                    checkIfBoardsAreEqual(visitedBoards.get(i), nextBoard)
            ){
                return true;
            }
        }
        return false;
    }

    public void move (int dx, int dy){
        checkNextState(dx, dy);

        if (canMove) {
            if (boxCanBeMoved) {
                int newBoxRow = playerRow + 2 * dy;
                int newBoxCol = playerCol + 2 * dx;

                if (isCornerDeadlock(newBoxRow, newBoxCol)) {
                    canMove = false;
                    return;
                }

//                if (isWallDeadlock(newBoxRow, newBoxCol)) {
//                    canMove = false;
//                    return;
//                }

                relocateEntity(playerRow + dy, playerCol + dx, dx, dy, '$');
            }

            relocateEntity(playerRow, playerCol, dx, dy, '@');
            updateBoxDisplay();
            playerRow += dy;
            playerCol += dx;

        }
    }

    public char[][] copyState(char[][] state) {
        char[][] copy = new char[state.length][state[0].length];
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                copy[i][j] = state[i][j];
            }
        }
        return copy;
    }

    public boolean isSolved() {
        for(int i = 0; i < mapData.length; i++) {
            for(int j = 0; j < mapData[0].length; j++) {
                if(mapData[i][j] == '.') {
                    if(gameState[i][j] != '/') {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    ///  ONLY USE THE FUNCTIONS ABOVE!!! (for the most part)///

    ///  some helper functions
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

    public void relocateEntity(int objRow, int objCol, int dx, int dy, char object) {
        gameState[objRow][objCol] = mapData[objRow][objCol]; // restore underlying tile
        gameState[objRow + dy][objCol + dx] = object;
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




    ///  this section is the deadlock detection or to check if a move is valid




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
    public boolean isWallTile(int row, int col) {

        if (row < 0 || row >= gameState.length ||
                col < 0 || col >= gameState[0].length) {
            return true;
        }

        return gameState[row][col] == '#';
    }

    public boolean isCornerDeadlock(int boxRow, int boxCol) {
        if (mapData[boxRow][boxCol] == '.') {
            return false;
        }

        boolean topBlocked = isWallTile(boxRow - 1, boxCol);
        boolean bottomBlocked = isWallTile(boxRow + 1, boxCol);

        boolean leftBlocked = isWallTile(boxRow, boxCol - 1);
        boolean rightBlocked = isWallTile(boxRow, boxCol + 1);

        return (topBlocked && leftBlocked) ||
                (topBlocked && rightBlocked) ||
                (bottomBlocked && leftBlocked) ||
                (bottomBlocked && rightBlocked);
    }

    public boolean rowHasGoalTile(int row) {

        // Check if this row contains at least one goal tile
        for (int col = 0; col < mapData[0].length; col++) {
            if (mapData[row][col] == '.') {
                return true;
            }
        }

        return false;
    }
    public boolean columnHasGoalTile(int col) {

        // Check if this column contains at least one goal tile
        for (int row = 0; row < mapData.length; row++) {
            if (mapData[row][col] == '.') {
                return true;
            }
        }

        return false;
    }


    public boolean isWallDeadlock(int boxRow, int boxCol) {

        if (mapData[boxRow][boxCol] == '.') {
            return false;
        }

        boolean topBlocked = isWallTile(boxRow - 1, boxCol);
        boolean bottomBlocked = isWallTile(boxRow + 1, boxCol);

        boolean leftBlocked = isWallTile(boxRow, boxCol - 1);
        boolean rightBlocked = isWallTile(boxRow, boxCol + 1);

        // Check if the box is touching a wall
        boolean boxIsAgainstHorizontalWall = topBlocked || bottomBlocked;
        boolean boxIsAgainstVerticalWall = leftBlocked || rightBlocked;

        boolean rowHasGoal = rowHasGoalTile(boxRow);
        boolean columnHasGoal = columnHasGoalTile(boxCol);

        // Box is stuck along a horizontal wall with no goal in the row
        if (boxIsAgainstHorizontalWall && !rowHasGoal) {
            return true;
        }

        // Box is stuck along a vertical wall with no goal in the column
        if (boxIsAgainstVerticalWall && !columnHasGoal) {
            return true;
        }

        return false;
    }

    public boolean deadlockDetection(){
        // implement your code here (if you want to create a method instead)
        // if not its fine you can add stuff in the checkNextState
        for (int row = 0; row < gameState.length; row++) {

            for (int col = 0; col < gameState[0].length; col++) {

                if (gameState[row][col] == '$') {

                    if (isCornerDeadlock(row, col)) {
                        return true;
                    }

                    if (isWallDeadlock(row, col)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }



    public void displayGameState(){
        moves++;
        for(int i = 0; i < itemsData.length; i++) {
            for(int j = 0; j < itemsData[0].length; j++) {
                System.out.print(gameState[i][j]);
            }
            System.out.println();
        }
        System.out.println(moves);
    }


    //    public int computeHeuristic(char[][] state) {
//        ArrayList<int[]> crates = findCrates(state);
//        ArrayList<int[]> goals = findGoals();
//
//        int totalHeuristicValue = 0;
//
//        for (int i = 0; i < crates.size(); i++) {
//            int[] crate = crates.get(i);
//            int lowestHeuristicValue = Integer.MAX_VALUE;
//
//            for (int j = 0; j < goals.size(); j++) {
//                int[] goal = goals.get(j);
//                int heuristicValue = Math.abs(crate[0] - goal[0]) + Math.abs(crate[1] - goal[1]);
//
//                if (heuristicValue < lowestHeuristicValue) {
//                    lowestHeuristicValue = heuristicValue;
//                }
//            }
//
//            totalHeuristicValue += lowestHeuristicValue;
//        }
//
//        return totalHeuristicValue;
//    }
//    public void storeBoard(char[][] board) {
//        this.board.add(copyState(board));
//    }

    // fuck u a* i perfer doing MOMECHE MASSCHE SEPPROC than this shit
//    public Object[] createState(char[][] board, int g, int playerRow, int playerCol, String path) {
//        int h = computeHeuristic(board);
//        int f = g + h;
//
//        return new Object[]{
//                board,
//                g,
//                h,
//                f,
//                playerRow,
//                playerCol,
//                path
//        };
//    }

//    public void updateGameState(String input){
//        int f = moves + computeHeuristic(gameState);
//        switch (input) {
//            case "u" -> move(0, -1);
//            case "d" -> move(0, 1);
//            case "r" -> move(1, 0);
//            case "l" -> move(-1, 0);
//        }
//        moves++;
//        displayGameState();
//
//        String paths = output;
//        states.add(createState(
//                copyState(gameState),
//                moves,
//                playerRow,
//                playerCol,
//                paths
//        ));
//        System.out.println("player's move: " + input + " | g: " + moves + " | h: " + computeHeuristic(gameState) + " | " + "f:" + f);
//
//        System.out.println("---------------------------------------------");
//        System.out.println("\n");
//
//    }

    //    public ArrayList<int[]> findCrates(char[][] state) {
//        ArrayList<int[]> result = new ArrayList<>();
//        for (int i = 0; i < state.length; i++) {
//            for (int j = 0; j < state[0].length; j++) {
//                if (state[i][j] == '$' || state[i][j] == '/') {
//                    result.add(new int[]{j, i});
//                }
//            }
//        }
//        return result;
//    }
//
//    public ArrayList<int[]> findGoals() {
//        ArrayList<int[]> result = new ArrayList<>();
//        for (int i = 0; i < mapData.length; i++) {
//            for (int j = 0; j < mapData[0].length; j++) {
//                if (mapData[i][j] == '.') {
//                    result.add(new int[]{j, i});
//                }
//            }
//        }
//        return result;
//    }

}