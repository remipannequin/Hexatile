package gabygaby.hexatile;

import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class GameActivity extends Activity {


    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        final View contentView = findViewById(R.id.fullscreen_content);
        final TextView scoreView = (TextView)findViewById(R.id.scoreTextView);

        if (savedInstanceState == null) {
            board = new Board(6, 8);
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
}
