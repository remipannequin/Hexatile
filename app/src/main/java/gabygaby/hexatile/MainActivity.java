package gabygaby.hexatile;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.google.example.games.basegameutils.BaseGameActivity;

import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.ui.TileView;
import gabygaby.hexatile.util.GamePersist;

public class MainActivity extends BaseGameActivity implements View.OnClickListener {

    public static final String TAG = "hexatile.MainActivity"; //NON-NLS

    private TextView highscore;
    private Button newGameButton;
    private TileView bestTile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        highscore = (TextView) findViewById(R.id.highScoreTextView);
        newGameButton = (Button) findViewById(R.id.gameButton);
        bestTile = (TileView) findViewById(R.id.bestTileView);
        Tile t = new Tile(0, 1);
        bestTile.setTile(t);
        bestTile.syncDrawnLevel();
        ObjectAnimator bestTileRotAnim = ObjectAnimator.ofFloat(bestTile, "flip", 0, 1); //NON-NLS
        bestTileRotAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bestTile.invalidate();
            }
        });
        bestTileRotAnim.setDuration(3000);
        bestTileRotAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        bestTileRotAnim.setRepeatMode(Animation.REVERSE);
        bestTileRotAnim.setRepeatCount(Animation.INFINITE);
        bestTileRotAnim.start();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);


    }


    @Override
    protected void onStart() {
        super.onStart();
        //get level from saved highscores
        GamePersist gp = GamePersist.getInstance();
        if (!gp.isInitialized()) {
            gp.init(getApplicationContext());
        }

        highscore.setText(String.format("%d", gp.highScore())); //NON-NLS
        bestTile.getTile().setLevel(gp.bestTileEver());
        bestTile.syncDrawnLevel();
        if (gp.hasUnfinishedGame()) {
            newGameButton.setText(R.string.button_resume_game);
        }
    }


    /**
     * Called when clicking the singIn signOut buttons
     *
     * @param view
     */
    public void onClick(View view) {
        if (isSignedIn()) {
            signOut();
        } else {
            beginUserInitiatedSignIn();
        }
    }

    /**
     * show game activity
     *
     * @param v
     */
    public void onGameButtonClicked(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSignInFailed() {
        // show sign-in button, hide the sign-out button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignInSucceeded() {
        // show sign-in button, hide the sign-out button
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }




    class Accomplishments {
        boolean firstTileAchievement = false;
        boolean over900Achievement = false;
        boolean level3Achievement = false;
        boolean level5Achievement = false;
        boolean millionAchievement = false;
        int score;


        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }


}

