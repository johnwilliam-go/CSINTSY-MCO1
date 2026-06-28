package solver;
import java.util.*;

public class State {
    private int playerX, playerY;
    private String path;
    private Set<String> cratePositions;

    public State(int playerX, int playerY, String path, Set<String> cratePositions){
        this.playerX = playerX;
        this.playerY = playerY;
        this.cratePositions = cratePositions;
        this.path = path;
    }

    // This is for handling individual coordinates separately
    // Crucial for checking whether two game states are equal.
    // For checking visited paths of the player.

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof State)){
            return false;
        }
        State thatOne = (State) o;
        return playerX == thatOne.playerX && playerY == thatOne.playerY && Objects.equals(cratePositions, thatOne.cratePositions);
    }
}
