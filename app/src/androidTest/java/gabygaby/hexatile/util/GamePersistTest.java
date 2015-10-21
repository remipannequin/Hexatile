package gabygaby.hexatile.util;

import android.test.RenamingDelegatingContext;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by remi on 21/10/15.
 */
public class GamePersistTest extends TestCase {




    class FileNotFoundMockContext extends MockContext {
        @Override
        public FileInputStream openFileInput(String name) throws FileNotFoundException {
            throw new FileNotFoundException();
        }
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();


        /*FileOutputStream fos = new FileOutputStream("test1");
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putInt(123456).putInt(5).putInt(-1).putInt(0).putInt(0);
        fos.write(buffer.array());
        fos.close();*/
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

    }

    @SmallTest
    public void testRead() {

        //GamePersist instance = new GamePersist(new FileFoundMockContext());



    }

     @SmallTest
    public void testReadFirstTime() {
        GamePersist instance = new GamePersist(new FileNotFoundMockContext());
        assertFalse(instance.hasUnfinishedGame());
        assertEquals(0, instance.highScore());
        assertEquals(1, instance.bestTileEver());
    }

}
