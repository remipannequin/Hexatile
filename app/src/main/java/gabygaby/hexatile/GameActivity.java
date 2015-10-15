package gabygaby.hexatile;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.ui.BoardView;

public class GameActivity extends Activity implements Board.BoardEventListener {

    private static final String TAG = "Hexatile";

    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        final View contentView = findViewById(R.id.fullscreen_content);


        if (savedInstanceState == null) {
            board = new Board(5, 6);
        } else {
            Parcelable savedBoard = savedInstanceState.getParcelable("board");
            board = (Board)savedBoard;
        }

        ((BoardView)contentView).setBoard(board);
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

    }

    public void updateScore() {
        final TextView scoreView = (TextView)findViewById(R.id.scoreTextView);
        scoreView.setText(String.format("%d", board.getScore()));
    }

    public void newGame(View view) {
        board.reset();
        BoardView contentView = (BoardView) findViewById(R.id.fullscreen_content);
        contentView.invalidateAll();
        findViewById(R.id.scoreTextView).invalidate();
    }

    @Override
    public void onTileAdded(Tile newTile) {
        //TODO Check achievements
    }

    @Override
    public void onGroupCollapsed(Iterable<Tile> group, Tile promoted) {
        //Check achievements
        updateScore();
    }

    @Override
    public void onCascadeStarted() {
        //TODO Check achievements
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
