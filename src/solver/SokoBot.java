package solver;
import java.util.Random;

public class SokoBot {
    String output = "";
    private char[][] mapData;
    private char[][] itemsData;
    private char gameState[][];
    private boolean canMove = true;
    private boolean boxCanBeMoved = false;

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

    showPlayerCoordinate();

    Runnable[] moves = {
            () -> up(),
            () -> down(),
            () -> left(),
            () -> right()
    };

    // THIS IS TEMPORARY SOLUTION
    // THIS IS TO TEST THE DEADLOCK
    printItems();
    Random rand = new Random();
    while (output.length() < 100) {
        int num = rand.nextInt(4);
        moves[num].run();
    }
    return output;
  }

  public void showPlayerCoordinate(){
      for(int i = 0; i < mapData.length; i++) {
          for(int j = 0; j < mapData[0].length; j++) {
              if(mapData[i][j] == '.') {
                  System.out.print("Goal: (" + j + "," + i + ")\n");
              }
          }
      }

      for(int i = 0; i < itemsData.length; i++) {
          for(int j = 0; j < itemsData[0].length; j++) {
              if(itemsData[i][j] == '@' ) {
                  System.out.print("Player: (" + j + "," + i + ")\n");
              }
              if(itemsData[i][j] == '$' ) {
                  System.out.print("Crates: (" + j + "," + i + ")\n");
              }
          }
      }
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
    }

    public void checkNextState(int x, int y) {
        for (int i = 0; i < gameState.length; i++) {
            for (int j = 0; j < gameState[0].length; j++) {
                if (gameState[i][j] == '@') {
                    int nextI = i + y;
                    int nextJ = j + x;
                    int nextNextI = i + 2 * y;
                    int nextNextJ = j + 2 * x;

                    if (nextI < 0 || nextI >= gameState.length || nextJ < 0 || nextJ >= gameState[0].length) {
                        canMove = false;
                        return;
                    }

                    if (gameState[nextI][nextJ] == '#') {
                        canMove = false;
                    } else if (gameState[nextI][nextJ] == '$' || gameState[nextI][nextJ] == '/') {
                        // check bounds for cell beyond box
                        if (nextNextI < 0 || nextNextI >= gameState.length || nextNextJ < 0 || nextNextJ >= gameState[0].length) {
                            canMove = false;
                            return;
                        }
                        if (gameState[nextNextI][nextNextJ] == '$' || gameState[nextNextI][nextNextJ] == '/'
                                || gameState[nextNextI][nextNextJ] == '#') {
                            canMove = false;
                        } else {
                            boxCanBeMoved = true;
                        }
                    }
                }
            }
        }
    }

    public void deadlockDetection(){
        // implement your code here
    }
}
