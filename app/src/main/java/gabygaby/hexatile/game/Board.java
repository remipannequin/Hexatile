package gabygaby.hexatile.game;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Observable;
import java.util.Set;

/**
 * This class represent a board, i.e. a set of hexagonal tiles, with a toroid topology
 * <p/>
 * Created by remi on 25/09/15.
 *
 * @author Rémi Pannequin
 */
public class Board extends Observable implements Parcelable {

    public static final int THRESHOLD = 3;
    private final Tile[] tiles;
    private final int width;
    private final int height;
    private int score;
    private boolean dirty = false;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        score = 0;
        tiles = new Tile[height * width];

        Tile new_tile;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                new_tile = new Tile(this, j + i * width);
                tiles[j + i * width] = new_tile;
            }
        }

        int r, l, u, d, ul, ur, dl, dr;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                l = (j > 0 ? j - 1 : width - 1);
                r = (j + 1) % width;
                u = (i > 0 ? i - 1 : height - 1);
                d = (i + 1) % height;
                ul = (i % 2 == 1 ? j : l);
                ur = (i % 2 == 1 ? r : j);
                dl = (i % 2 == 1 ? j : l);
                dr = (i % 2 == 1 ? r : j);
                Tile current = getTile(i, j);
                current.setLeft(getTile(i, l));
                current.setRight(getTile(i, r));
                current.setUpLeft(getTile(u, ul));
                current.setUpRight(getTile(u, ur));
                current.setDownLeft(getTile(d, dl));
                current.setDownRight(getTile(d, dr));
            }
        }
    }

    public Board(Parcel in) {
        this(in.readInt(), in.readInt());
        int[] levels = new int[width * height];
        in.readIntArray(levels);
        int i = 0;
        for (Tile t : tiles) {
            t.setLevel(levels[i++]);
        }
        score = in.readInt();
    }

    public Tile getTile(int row, int column) {
        return tiles[row * width + column];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * compute a fusion around the last tile changed.
     *
     * @param last Last tile modified
     * @return the list of index of changed tiles (excluding last)
     */
    public Set<Tile> compute(Tile last) {
        Set<Tile> group = last.findGroup();
        group.remove(last);
        if (group.size() >= THRESHOLD) {
            for (Tile t : group) {
                t.consume();
            }
            last.promote();
            score += Math.pow((last.getLevel() + group.size() - THRESHOLD), last.getLevel());
            setChanged();
            notifyObservers();
            return group;
        } else {
            dirty = false;
        }
        return group;
    }

    public int getScore() {
        return score;
    }

    /**
     * compute if the game is over
     *
     * @return true if the board is completelly filled
     */
    public boolean isGameOver() {
        for (Tile t : tiles) {

            if (t.isFree()) {
                return false;
            }

        }
        return true;
    }

    /**
     * Reset the game
     */
    public void reset() {
        for (Tile t: tiles) {
            t.consume();
        }
        score = 0;
    }

    public boolean isDirty() {
        return dirty;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);

        int[] levels = new int[width * height];
        int i = 0;
        for (Tile t : tiles) {

            levels[i++] = t.getLevel();

        }
        dest.writeIntArray(levels);
        dest.writeInt(score);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    public Tile[] getTiles() {
        return tiles;
    }

    public void setDirty() {
        dirty = true;
    }
}
