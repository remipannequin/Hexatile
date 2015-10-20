package gabygaby.hexatile;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.game.TileGenerator;
import gabygaby.hexatile.ui.BoardView;
import gabygaby.hexatile.ui.TileGeneratorView;

public class GameActivity extends Activity implements Board.BoardEventListener {

    public static final String TAG = "hexatil.GameActivity";

    private Board board;

    private GoogleApiClient apiClient;
    private TileGenerator generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        final BoardView boardView = (BoardView) findViewById(R.id.board_view);


        if (savedInstanceState == null) {
            board = new Board(5, 6);
        } else {
            Parcelable savedBoard = savedInstanceState.getParcelable("board");
            board = (Board) savedBoard;
        }
        board.addListener(this);
        ((BoardView) boardView).setBoard(board);

        generator = new TileGenerator(5);
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
        outState.putParcelable("board", board);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void gameOver() {
        //TODO: show statistics in a dialog

        //publish score
        Games.Leaderboards.submitScore(apiClient, getString(R.string.leaderboard_classic) , board.getScore());
    }

    public void updateScore() {
        final TextView scoreView = (TextView) findViewById(R.id.scoreTextView);
        scoreView.setText(String.format("%d", board.getScore()));
    }

    public void newGame(View view) {
        board.reset();
        BoardView contentView = (BoardView) findViewById(R.id.board_view);
        contentView.invalidateAll();
        findViewById(R.id.scoreTextView).invalidate();
    }

    @Override
    public void onTileAdded(Tile newTile, boolean collapsing, int origLevel) {
        //TODO Check achievements
    }

    @Override
    public void onGroupCollapsed(Iterable<Tile> group, Tile promoted) {
        //Check achievements
        updateScore();
    }

    @Override
    public void onCascadeFinished() {
        //TODO Check achievements
    }

    @Override
    public void onGameOver() {
        gameOver();
    }
}
