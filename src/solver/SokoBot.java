package solver;

public class SokoBot {
    String output;
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */
    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    right();
    right();
    right();

    return output;
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




}
