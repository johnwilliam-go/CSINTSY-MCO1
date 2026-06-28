package solver;
import java.util.*;

public class SokoBot {
    private char[][] mapData;
    private char[][] itemsData;
    private char[][] gameState;
    private int playerRow, playerCol;
    private int height;
    private int width;


    private final ArrayList<String> queueBoard = new ArrayList<>();

    private final ArrayList<String> paths = new ArrayList<>();

    private final HashSet<String> visitedBoards = new HashSet<>();

    private static class Node {
        String board;
        String path;
        int cost;

        Node(String board, String path, int cost) {
            this.board = board;
            this.path = path;
            this.cost = cost;
        }
    }

    private static class Step {

        int row;
        int col;
        String path;

        Step(int row, int col, String path) {
            this.row = row;
            this.col = col;
            this.path = path;
        }
    }

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        this.height = height;
        this.width = width;
        this.mapData = mapData;
        this.itemsData = itemsData;
        this.gameState = new char[height][width];

        initializeGameState();
        updateBoxDisplay();

        visitedBoards.clear();

        PriorityQueue<Node> queueBoard = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));

        String first = boardToString(gameState);
        queueBoard.add(new Node(first, "", heuristic(gameState)));
        visitedBoards.add(first);

        long startTime = System.currentTimeMillis();

        //           up   down  left  right
        int[] dx = {  0,   0,   -1,    1};
        int[] dy = { -1,   1,    0,    0};
        char[] direction = {'u', 'd', 'l', 'r'};

        while (!queueBoard.isEmpty()) {
            if (System.currentTimeMillis() - startTime > 14999) {
                return "";
            }

            Node currentBoard = queueBoard.poll();
            gameState = stringToBoard(currentBoard.board);

            if (isSolved()) {
                return currentBoard.path;
            }

            HashMap<Integer, String> reachableArea =
                    getReachableTiles(gameState, playerRow, playerCol);

            for (int nextPosition : reachableArea.keySet()) {
                int walkRow = nextPosition / width;
                int walkCol = nextPosition % width;
                String walkPath = reachableArea.get(nextPosition);

                for (int i = 0; i < dx.length; i++) {
                    int boxRow = walkRow + dy[i];
                    int boxCol = walkCol + dx[i];

                    int nextBoxRow = boxRow + dy[i];
                    int nextBoxCol = boxCol + dx[i];

                    if (nextBoxRow < 0 || nextBoxRow >= height ||
                            nextBoxCol < 0 || nextBoxCol >= width) {
                        continue;
                    }

                    if (!isBox(gameState[boxRow][boxCol])) {
                        continue;
                    }

                    if (isBlockedForBox(gameState[nextBoxRow][nextBoxCol])) {
                        continue;
                    }

                    char[][] newState = copyBoard(gameState);

                    newState[playerRow][playerCol] = mapData[playerRow][playerCol];
                    newState[boxRow][boxCol] = '@';
                    newState[nextBoxRow][nextBoxCol] = '$';

                    gameState = newState;
                    updateBoxDisplay();

                    String newBoard = boardToString(gameState);

                    if (!visitedBoards.contains(newBoard)) {
                        if (!deadlockDetection()) {
                            String newPath = currentBoard.path + walkPath + direction[i];
                            int newCost = newPath.length() + heuristic(gameState) * 40;

                            queueBoard.add(new Node(newBoard, newPath, newCost));
                            visitedBoards.add(newBoard);
                        }
                    }

                    gameState = stringToBoard(currentBoard.board);
                }
            }
        }

        return "";
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
    public char[][] copyBoard(char[][] board) {
        char[][] newBoard = new char[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                newBoard[row][col] = board[row][col];
            }
        }

        return newBoard;
    }
    public boolean isBox(char tile) {
        return tile == '$' || tile == '/';
    }

    public boolean isBlockedForBox(char tile) {
        return tile == '#' || tile == '$' || tile == '/';
    }

    public HashMap<Integer, String> getReachableTiles(char[][] board, int startRow, int startCol) {
        HashMap<Integer, String> reachable = new HashMap<>();
        ArrayDeque<Step> queue = new ArrayDeque<>();

        queue.add(new Step(startRow, startCol, ""));
        reachable.put(startRow * width + startCol, "");

        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};
        char[] moveChar = {'u', 'd', 'l', 'r'};

        while (!queue.isEmpty()) {
            Step current = queue.poll();

            for (int i = 0; i < 4; i++) {
                int newRow = current.row + dRow[i];
                int newCol = current.col + dCol[i];

                if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width) {
                    continue;
                }

                char tile = board[newRow][newCol];

                if (tile == '#' || tile == '$' || tile == '/') {
                    continue;
                }

                int key = newRow * width + newCol;

                if (!reachable.containsKey(key)) {
                    String newPath = current.path + moveChar[i];
                    reachable.put(key, newPath);
                    queue.add(new Step(newRow, newCol, newPath));
                }
            }
        }

        return reachable;
    }

    public int heuristic(char[][] state) {
        int totalDistance = 0;

        for (int boxRow = 0; boxRow < height; boxRow++) {
            for (int boxCol = 0; boxCol < width; boxCol++) {

                if (state[boxRow][boxCol] == '$' || state[boxRow][boxCol] == '/') {
                    int shortestDistance = Integer.MAX_VALUE;

                    for (int targetRow = 0; targetRow < height; targetRow++) {
                        for (int targetCol = 0; targetCol < width; targetCol++) {

                            if (mapData[targetRow][targetCol] == '.') {
                                int rowDistance = Math.abs(boxRow - targetRow);
                                int colDistance = Math.abs(boxCol - targetCol);
                                int distance = rowDistance + colDistance;

                                if (distance < shortestDistance) {
                                    shortestDistance = distance;
                                }
                            }
                        }
                    }

                    totalDistance += shortestDistance;

                    if (state[boxRow][boxCol] == '$' && isCornerDeadlock(boxRow, boxCol)) {
                        totalDistance += 10000;
                    }
                }
            }
        }

        return totalDistance;
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

    public boolean isBoxOrWall(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return true;
        }

        return gameState[row][col] == '#' ||
                gameState[row][col] == '$' ||
                gameState[row][col] == '/';
    }

    public boolean isSmallSquareDeadlock(int row, int col) {
        for (int startRow = row - 1; startRow <= row; startRow++) {
            for (int startCol = col - 1; startCol <= col; startCol++) {

                boolean blockedSquare =
                        isBoxOrWall(startRow, startCol) &&
                                isBoxOrWall(startRow + 1, startCol) &&
                                isBoxOrWall(startRow, startCol + 1) &&
                                isBoxOrWall(startRow + 1, startCol + 1);

                if (blockedSquare) {
                    return true;
                }
            }
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
                    if (isSmallSquareDeadlock(row, col)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}