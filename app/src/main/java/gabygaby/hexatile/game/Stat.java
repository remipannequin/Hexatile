package gabygaby.hexatile.game;



import java.security.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store statistics about a game
 *
 * Created by remi on 14/10/15.
 */
public class Stat {

    private final int boardSize;
    private final int[] scoreByLevel;
    private final int[] scoreEventCount;
    private final int[] tilesAdded;
    private final List<Float> boardOccupation;
    private final int[] tileMutated;

    
        
    public Stat(int boardSize) {
        this.boardSize = boardSize;
        this.scoreEventCount = new int[Tile.MAX_TILE_LEVEL+1];
        this.scoreByLevel = new int[Tile.MAX_TILE_LEVEL+1];
        this.tilesAdded = new int[Tile.MAX_TILE_LEVEL+1];
        this.tileMutated = new int[3];
        this.boardOccupation = new ArrayList<>();
    }

    public void recordScore(int level, int reward) {
        scoreByLevel[level - 1] += reward;
        scoreEventCount[level - 1]++;
        tilesAdded[level]++;
    }

    public void recordPutTile(int level) {
        if (level < 0 || level > Tile.MAX_TILE_LEVEL) {
            throw new InvalidParameterException(String.format("invalid level %d to record", level));
        }
        tilesAdded[level]++;
    }

    /**
     * Record that a tile was mutated
     * @param kind at least 2, since only tile above kind 1 can mutate
     */
    public void recordMutateTile(int kind) {
        tileMutated[kind-2]++;
    }

    public void recordOccupation(int numberOfTiles) {
        boardOccupation.add((float)numberOfTiles / (float)boardSize);
    }

}
