package gabygaby.hexatile.game;

import android.test.suitebuilder.annotation.SmallTest;

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


}
