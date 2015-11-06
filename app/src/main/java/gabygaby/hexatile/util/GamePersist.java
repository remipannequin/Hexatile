package gabygaby.hexatile.util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import gabygaby.hexatile.GameActivity;
import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;

/**
 * Persist game information (high score, bestTile, current game in a private file.
 *
 * Structure of the file
 * int (4 bytes) high score
 * int (1 byte) bestTile level
 * int (4 bytes) score (-1 if no board)
 * int (1 byte) board width (zero if no board)
 * int (1 byte) board height (zero if no board)
 *
 * (for height * width)
 *    short (2 bytes) tile level/kind
 *
 *
 *
 */
public class GamePersist implements Board.BoardEventListener {
    private Context context;
    final String FILENAME_HIGHSCORE = "highscore"; //NON-NLS
    final String FILENAME_CURRENT = "current"; //NON-NLS

    private int bestTile;
    private int highScore;
    private Board board;


    private static final GamePersist ourInstance = new GamePersist();

    public static GamePersist getInstance() {
        return ourInstance;
    }

    private GamePersist() {
        //sane values
        highScore = 0;
        bestTile = 1;
    }

    public void init(Context ctx) {
        context = ctx;

        FileInputStream fis;
        try {
            fis = context.openFileInput(FILENAME_HIGHSCORE);

            FileChannel chan = fis.getChannel();
            long fSize = chan.size();
            ByteBuffer buffer = ByteBuffer.allocate((int) fSize);
            chan.read(buffer);
            buffer.rewind();
            highScore = buffer.getInt();
            bestTile = buffer.getInt();
            fis.close();

            fis = context.openFileInput(FILENAME_CURRENT);
            chan = fis.getChannel();
            fSize = chan.size();
            buffer = ByteBuffer.allocate((int) fSize);
            chan.read(buffer);
            buffer.rewind();
            int score = buffer.getInt();
            int w = buffer.getInt();
            int h = buffer.getInt();
            board = new Board(w, h);
            board.setScore(score);
            Tile[] tiles = board.getTiles();
            for (Tile t: tiles) {
                short level = buffer.getShort();
                short kind = buffer.getShort();
                if (kind == 0 && level != 0) {kind = 1;}
                int value = buffer.getInt();
                t.setLevel(level);
                t.setKind(kind);
                t.setValue(value);
            }
            board.addListener(this);
            fis.close();

        } catch (FileNotFoundException e) {
            //Can happen on the first exec
            board = null;
        } catch ( BufferUnderflowException e) {
            // file may be corrupted (too short)
            Log.w(GameActivity.TAG, "error while loading save file");
            context.deleteFile(FILENAME_CURRENT);
            board = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean needsInitialization() {
        return context == null;
    }

    private void write_score() {
        FileOutputStream fos;

        try {
            fos = context.openFileOutput(FILENAME_HIGHSCORE, Context.MODE_PRIVATE);
            ByteBuffer buffer = ByteBuffer.allocate((Integer.SIZE * 2) / Byte.SIZE);
            buffer.putInt(highScore);
            buffer.putInt(bestTile);
            fos.write(buffer.array());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write_board() {
        FileOutputStream fos;
        if (hasUnfinishedGame()) {
            int s = board.getScore();
            int w = board.getWidth();
            int h = board.getHeight();
            Tile[] tiles = board.getTiles();
            try {
                fos = context.openFileOutput(FILENAME_CURRENT, Context.MODE_PRIVATE);

                ByteBuffer buffer = ByteBuffer.allocate(
                        (Integer.SIZE * 3 + //score, width, height
                                (Short.SIZE * 2 + Integer.SIZE) //level, kind, value...
                                        * w * h) // ...for each tile
                                / Byte.SIZE);
                buffer.putInt(s);
                buffer.putInt(w);
                buffer.putInt(h);
                for (Tile t : tiles) {
                    short level = (short) t.getLevel();
                    short kind = (short) t.getKind();
                    int value = t.getValue();
                    buffer.putShort(level);
                    buffer.putShort(kind);
                    buffer.putInt(value);
                }
                fos.write(buffer.array());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            context.deleteFile(FILENAME_CURRENT);
        }
    }

    public int bestTileEver() {
        return bestTile;
    }

    public int highScore() {
        return highScore;
    }

    public boolean hasUnfinishedGame() {
        return (board != null);
    }

    public Board getUnfinishedGameBoard() {
        return board;
    }

    public void startGame(Board b) {
        board = b;
        board.addListener(this);
    }

    public void finishGame() {
        int score = board.getScore();
        if (highScore < score) {
            highScore = score;
            write_score();
        }
        board = null;
        write_board();
    }

    @Override
    public void onTileAdded(Tile newTile, boolean collapsing, int origLevel) {

    }

    @Override
    public void onGroupCollapsed(Iterable<Tile> group, Tile promoted) {
        int score = board.getScore();
        if (highScore < score) {
            highScore = score;
        }
        if (promoted.getLevel() > bestTile) {
            bestTile = promoted.getLevel();
            write_score();
        }
    }

    @Override
    public void onCascadeFinished() {
        write_board();
    }

    @Override
    public void onGameOver() {

    }

    @Override
    public void onTileMutated(Tile selected, boolean collapsing, int origLevel) {
    }

    public Board getBoard() {
        return board;
    }
}
