package gabygaby.hexatile.game;


import android.util.Log;

import java.security.InvalidParameterException;

import gabygaby.hexatile.GameActivity;

/**
 * Class to store statistics about a game
 *
 * Created by remi on 14/10/15.
 */
public class Stat {

    private int[] scoreByLevel;
    private int[] scoreEventCount;
    private int[] tilesAdded;

    public Stat() {
        this.scoreEventCount = new int[Tile.MAX_TILE_LEVEL+1];
        this.scoreByLevel = new int[Tile.MAX_TILE_LEVEL+1];
        this.tilesAdded = new int[Tile.MAX_TILE_LEVEL+1];
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
}
