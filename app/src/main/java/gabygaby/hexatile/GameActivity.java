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
import gabygaby.hexatile.ui.BoardView;

public class GameActivity extends Activity {

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
        final TextView scoreView = (TextView)findViewById(R.id.scoreTextView);

        if (savedInstanceState == null) {
            board = new Board(5, 6);
        } else {
            Parcelable savedBoard = savedInstanceState.getParcelable("board");
            board = (Board)savedBoard;
        }

        ((BoardView)contentView).setBoard(board);

        board.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Board board = (Board) observable;
                scoreView.setText(String.format("%d", board.getScore()));
            }
        });
        //TODO: also add Observer to update achievemnts

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

    public void newGame(View view) {
        board.reset();
        BoardView contentView = (BoardView) findViewById(R.id.fullscreen_content);
        contentView.invalidateAll();
        findViewById(R.id.scoreTextView).invalidate();
    }

}
