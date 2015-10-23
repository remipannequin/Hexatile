package gabygaby.hexatile.game;

import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.TestCase;

/**
 * Created by remi on 28/09/15.
 */
public class BoardTest extends TestCase {

    @SmallTest
    public void testCreateBoard() {
        Board instance = new Board(5,4);
        Tile t1 = instance.getTile(1,2);
        assertEquals(t1.getDownLeft(), instance.getTile(2,2));
        assertEquals(t1.getDownRight(),instance.getTile(2,3));
        assertEquals(t1.getUpLeft(), instance.getTile(0,2));
        assertEquals(t1.getUpRight(),instance.getTile(0,3));
        assertEquals(t1.getLeft(), instance.getTile(1,1));
        assertEquals(t1.getRight(),instance.getTile(1,3));

        Tile t2 = instance.getTile(3,4);
        assertEquals(t2.getLeft(), instance.getTile(3,3));
        assertEquals(t2.getRight(),instance.getTile(3,0));
        assertEquals(t2.getDownLeft(), instance.getTile(0,4));
        assertEquals(t2.getDownRight(),instance.getTile(0,0));
        assertEquals(t2.getUpLeft(), instance.getTile(2,4));
        assertEquals(t2.getUpRight(),instance.getTile(2,0));
    }

    @LargeTest
    public void testRandomPlay() {
        int[] scores = new int[10000];
        for (int i = 0; i < scores.length; i++) {
            TileGenerator gene = new TileGenerator(1);
            Board instance = new Board(5, 6);
            while (!instance.isGameOver()) {
                for (Tile t : instance.getTiles()) {
                    if (t.isFree()) {
                        instance.fill(t, gene.consume());
                    }
                }
            }
            scores[i] = instance.getScore();
            System.out.println(String.format("game %d/%d : score %d",
                    i, scores.length, instance.getScore()));
        }
        double total = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (Integer s : scores) {
            total += s;
            min = Math.min(min, s);
            max = Math.max(max, s);
        }

        System.out.println(String.format("mean %f, min %f, max %f", total/ scores.length, min, max));

    }


}
