package gabygaby.hexatile.game;


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
        this.scoreEventCount = new int[6];
        this.scoreByLevel = new int[6];
        this.tilesAdded = new int[7];
    }

    public void recordScore(int level, int reward) {
        scoreByLevel[level - 1] += reward;
        scoreEventCount[level - 1]++;
        tilesAdded[level]++;
    }

    public void recordPutTile(int level) {
        tilesAdded[level]++;
    }
}
