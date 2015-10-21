package gabygaby.hexatile;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.ui.TileView;
import gabygaby.hexatile.util.GamePersist;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final String TAG = "hexatile.MainActivity";
    private static int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;
    private boolean mResolvingConnectionFailure;
    private TextView highscore;
    private Button newGameButton;
    private TileView bestTile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        highscore = (TextView) findViewById(R.id.highScoreTextView);
        newGameButton = (Button) findViewById(R.id.gameButton);
        bestTile = (TileView) findViewById(R.id.bestTileView);
        Tile t = new Tile(0, 1);
        bestTile.setTile(t);
        bestTile.syncDrawnLevel();
        ObjectAnimator bestTileRotAnim = ObjectAnimator.ofFloat(bestTile, "flip", 0, 1);
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

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        //get level from saved highscores
        GamePersist gp = GamePersist.getInstance();
        if (!gp.isInitialized()) {
            gp.init(getApplicationContext());
        }

        highscore.setText(String.format("%d", gp.highScore()));
        bestTile.getTile().setLevel(gp.bestTileEver());
        bestTile.syncDrawnLevel();
        if (gp.hasUnfinishedGame()) {
            newGameButton.setText("Resume Game");
        }


        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop(): disconnecting");
        //if (mGoogleApiClient.isConnected()) {
        //    mGoogleApiClient.disconnect();
        //}
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        // Show sign-out button on main menu


        // Show "you are signed in" message on win screen, with no sign in button.


        // Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String displayName;
        if (p == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
                if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
                        RC_SIGN_IN, getString(R.string.signin_other_error))) {
                    mResolvingConnectionFailure = false;
                }
        }

        // Sign-in failed, so show sign-in button on main menu

    }

    /**
     * Called when clicking the singIn signOut buttons
     *
     * @param view
     */
    public void onClick(View view) {
        // start the sign-in flow
        mSignInClicked = true;
        mGoogleApiClient.connect();

        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        } else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            mSignInClicked = false;
            Games.signOut(mGoogleApiClient);

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }


    }


    public void onSignOutButtonClicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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

