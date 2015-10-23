package gabygaby.hexatile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;

import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.game.TileGenerator;
import gabygaby.hexatile.ui.BoardView;
import gabygaby.hexatile.ui.TileGeneratorView;
import gabygaby.hexatile.util.GamePersist;

public class GameActivity extends BaseGameActivity implements Board.BoardEventListener {

    public static final String TAG = "hexatil.GameActivity"; //NON-NLS

    private Board board;
    private BoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        boardView = (BoardView) findViewById(R.id.board_view);



        if (savedInstanceState != null) {
            Parcelable savedBoard = savedInstanceState.getParcelable("board"); //NON-NLS
            board = (Board) savedBoard;
        }
        if (board == null) {
            GamePersist gp = GamePersist.getInstance();
            if (!gp.isInitialized()) {
                gp.init(getApplicationContext());
            }
            if (gp.hasUnfinishedGame()) {
                board = gp.getBoard();
            } else {
                board = new Board(5, 6);
                gp.startGame(board);
            }
        }
        board.addListener(this);
        boardView.setBoard(board);
        boardView.invalidateAll();
        updateScore();
        TileGenerator generator = new TileGenerator(5);
        TileGeneratorView generatorView = (TileGeneratorView)findViewById(R.id.generator_view);
        generatorView.setGenerator(generator);
        boardView.setGenerator(generator);
    }

    /**
     * Restart the game
     */
    private void restart() {
        board = new Board(5, 6);
        GamePersist gp = GamePersist.getInstance();
            if (!gp.isInitialized()) {
                gp.init(getApplicationContext());
            }
        gp.startGame(board);
        board.addListener(this);
        boardView.setBoard(board);
        boardView.invalidateAll();
        updateScore();
        TileGenerator generator = new TileGenerator(5);
        TileGeneratorView generatorView = (TileGeneratorView)findViewById(R.id.generator_view);
        generatorView.setGenerator(generator);
        boardView.setGenerator(generator);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("board", board); //NON-NLS
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void gameOver() {
        GamePersist gp = GamePersist.getInstance();
        if (!gp.isInitialized()) {
            gp.init(getApplicationContext());
        }
        gp.finishGame();
        GoogleApiClient apiClient = getApiClient();
        if (apiClient != null && apiClient.isConnected()) {
            //publish score
            Games.Leaderboards.submitScore(apiClient, getString(R.string.leaderboard_classic), board.getScore());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_over);
        //builder.setMessage("");
        builder.setPositiveButton(getString(R.string.new_game), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restart();
            }
        });
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateScore() {
        final TextView scoreView = (TextView) findViewById(R.id.scoreTextView);
        scoreView.setText(String.format("%d", board.getScore())); //NON-NLS
    }

    @Override
    public void onTileAdded(Tile newTile, boolean collapsing, int origLevel) {
        //TODO Check achievements
    }

    @Override
    public void onGroupCollapsed(Iterable<Tile> group, Tile promoted) {
        int level = promoted.getLevel();
        GoogleApiClient apiClient = getApiClient();
        if (apiClient != null && apiClient.isConnected()) {
            if (level >= 2) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_first_tile));
            }
            if (level >= 4) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_level_3_));
            }
            if (level >= 6) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_level_5_));
            }
        } //TODO: if not connected : record progress and send it later
    }

    @Override
    public void onCascadeFinished() {
        //TODO Check achievements
        //Check achievements
        updateScore();
        int current_score = board.getScore();
        GoogleApiClient apiClient = getApiClient();
        if (apiClient != null && apiClient.isConnected()) {
            if (current_score > 9000) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_over_9000_));
            }
            if (current_score > 100000) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_100_000));
            }
        } //TODO: if not connected : record progress and send it later
    }

    @Override
    public void onGameOver() {
        gameOver();
    }

    @Override
    public void onTileMutated(Tile selected, boolean collapsing, int origLevel) {

    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}
