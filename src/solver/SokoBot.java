package solver;
import java.util.ArrayList;
import java.util.HashSet;

public class SokoBot {
    private char[][] mapData;
    private char[][] itemsData;
    private char[][] gameState;
    private boolean canMove = true;
    private boolean boxCanBeMoved = false;
    private int playerRow, playerCol;
    private int moves = 0;
    private int height;
    private int width;


    private final ArrayList<String> queueBoard = new ArrayList<>();

    private final ArrayList<String> paths = new ArrayList<>();

    private final HashSet<String> visitedBoards = new HashSet<>();

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        this.height = height;
        this.width = width;
        gameState = new char[height][width];
        this.mapData = mapData;
        this.itemsData = itemsData;


        initializeGameState();
        String first = boardToString(gameState);
        queueBoard.add(first);
        paths.add("");
        visitedBoards.add(first);


        //           up   down  left  right
        int[] dx = {  0  ,  0,   -1,    1};
        int[] dy = { -1  ,  1,    0,    0};
        char[] direction = {'u', 'd', 'l', 'r'};

        while(!queueBoard.isEmpty()) {
            String currentBoard = String.valueOf(queueBoard.removeFirst());
            String path = paths.removeFirst();
            gameState = stringToBoard(currentBoard);
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (gameState[r][c] == '@') {
                        playerRow = r;
                        playerCol = c;
                    }
                }
            }

            if(isSolved()) {
                return path;
            }
            for(int i = 0; i < dx.length; i++){
                gameState = stringToBoard(currentBoard);

                checkNextState(dx[i], dy[i]);
                if (canMove) {
                    move(dx[i], dy[i]);

                    String newBoard = boardToString(gameState);

                    if (!visitedBoards.contains(newBoard)) {
                        if(!deadlockDetection()){
                            queueBoard.add(newBoard);
                            paths.add(path + direction[i]);
                            visitedBoards.add(newBoard);
                        }
                    }
                }
            }
        }

        return "";
    }

    public void move (int dx, int dy){
        if(boxCanBeMoved){
            relocateEntity(playerRow + dy, playerCol + dx, dx, dy, '$'); // move the box
        }
        relocateEntity(playerRow, playerCol, dx, dy, '@'); // move the player
        updateBoxDisplay();
        playerRow += dy;
        playerCol += dx;
    }

    public String boardToString(char[][] state) {
        StringBuilder boardtostring = new StringBuilder();
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                boardtostring.append(state[i][j]);
            }
        }
        return boardtostring.toString();
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

    public char[][] stringToBoard(String s) {
        char[][] board = new char[height][width];
        int idx = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                board[i][j] = s.charAt(idx++);
                if(board[i][j] == '@') {
                    playerRow = i;
                    playerCol = j;
                }
            }
        }
        return board;
    }

    // you might need checkNextState() as well


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
        gameState[objRow][objCol] = mapData[objRow][objCol];
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
        canMove = true;
        boxCanBeMoved = false;
        for (int i = 0; i < gameState.length; i++) {
            for (int j = 0; j < gameState[0].length; j++) {
                if (gameState[i][j] == '@') {

                    int nextVertical = i + y;    // if y = negative, it checks the left     if y = positive, it checks the right
                    int nextHorizontal = j + x;  // if x = negative, it checks the top      if x = positive,  it checks the bottom
                    int doubleNextVertical = i + 2 * y;   // checks 2 units to the left or right
                    int doubleNextHorizontal = j + 2 * x; // checks 2 units up and down

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
        if (boxCanBeMoved) {
            int newBoxRow = playerRow + 2 * y;
            int newBoxCol = playerCol + 2 * x;

            if (isCornerDeadlock(newBoxRow, newBoxCol)) {
                canMove = false;
                boxCanBeMoved = false;
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

                }
            }
        }

        return false;
    }

}