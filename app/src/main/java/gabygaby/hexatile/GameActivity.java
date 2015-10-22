package gabygaby.hexatile;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.game.TileGenerator;
import gabygaby.hexatile.ui.BoardView;
import gabygaby.hexatile.ui.TileGeneratorView;
import gabygaby.hexatile.util.GamePersist;

public class GameActivity extends Activity implements Board.BoardEventListener {

    public static final String TAG = "hexatil.GameActivity"; //NON-NLS

    private Board board;

    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        final BoardView boardView = (BoardView) findViewById(R.id.board_view);



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

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
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
        //TODO: show statistics in a dialog

        //publish score
        Games.Leaderboards.submitScore(apiClient, getString(R.string.leaderboard_classic), board.getScore());
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
        if (level >= 2) {
            Games.Achievements.unlock(apiClient, getString(R.string.achievement_first_tile));
        }
        if (level >= 4) {
            Games.Achievements.unlock(apiClient, getString(R.string.achievement_level_3_));
        }
        if (level >= 6){
            Games.Achievements.unlock(apiClient, getString(R.string.achievement_level_5_));
        }
    }

    @Override
    public void onCascadeFinished() {
        //TODO Check achievements
        //Check achievements
        updateScore();
        int current_score = board.getScore();
        if (current_score > 9000) {
            Games.Achievements.unlock(apiClient, getString(R.string.achievement_over_9000_));
        }
        if (current_score > 100000) {
            Games.Achievements.unlock(apiClient, getString(R.string.achievement_100_000));
        }
    }

    @Override
    public void onGameOver() {
        gameOver();
    }
}
