package gabygaby.hexatile;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

/**
 * Created by remi on 29/10/15.
 */
public class GameActivityTest extends ActivityUnitTestCase<GameActivity> {


    private GameActivity mActivity;

    public GameActivityTest() {
        super(GameActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                GameActivity.class);
        startActivity(intent, null, null);
        mActivity = getActivity();
    }

    public void testCreateActivity() {

        assertNotNull(mActivity);
        TextView scoreTV = (TextView) mActivity.findViewById(R.id.scoreTextView);
        assertEquals("", scoreTV.getText());

    }


}
