package solver;
import java.util.Random;

public class SokoBot {
    String output = "";
    private char[][] mapData;
    private char[][] itemsData;

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
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

    showPlayerCoordinate();

    Runnable[] moves = {
            () -> up(),
            () -> down(),
            () -> left(),
            () -> right()
    };

    // THIS IS TEMPORARY SOLUTION
    // THIS IS TO TEST THE DEADLOCK

    Random rand = new Random();
    while (output.length() < 1000) {
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
    }

    public void down(){
        output += "d";
    }

    public void left(){
        output += "l";
    }

    public void right(){
        output += "r";
    }

    public void deadlockDetection(){
        // implement your code here
    }

}
