package gabygaby.hexatile.game;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gabygaby.hexatile.GameActivity;

/**
 * This class represent a board, i.e. a set of hexagonal tiles, with a toroid topology
 * <p/>
 * Created by remi on 25/09/15.
 *
 * @author Rémi Pannequin
 */
public class Board implements Parcelable {

    public static final int THRESHOLD = 3;
    private final Tile[] tiles;
    private final int width;
    private final int height;
    private int score;
    private boolean dirty = false;
    private Stat stat;
    private List<BoardEventListener> listeners;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        stat = new Stat();
        score = 0;
        tiles = new Tile[height * width];
        listeners = new ArrayList<>();

        Tile new_tile;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                new_tile = new Tile(j + i * width);
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

    /**
     * Add a listener, that will be called when the board change
     *
     * @param l the listener to add
     */
    public void addListener(BoardEventListener l) {
        listeners.add(l);
    }

    public void removeListener(BoardEventListener l) {
        listeners.remove(l);
    }

    public void clearListeners() {
        listeners.clear();
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
            int group_value = 0;
            for (Tile t : group) {
                group_value += t.consume();
            }
            last.promote(group_value);
            int reward = (int) Math.pow((last.getLevel() + group.size() - THRESHOLD), last.getLevel());
            stat.recordScore(last.getLevel(), reward);
            score += reward;
            dirty = true;
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
            if (t.isFree() || t.isMutable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reset the game
     */
    public void reset() {
        for (Tile t : tiles) {
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

    /**
     * Fill selected Tile, if it is empty, and enerate all cascading board events
     *
     * @param selected selected Tile
     * @param value the level to give to the new tile
     */
    public void fill(Tile selected, int value) {
        if (selected.isFree()) {
            selected.fill(value);
            dirty = true;
            stat.recordPutTile(selected.getLevel());
            boolean collapsing = (selected.findGroup().size() > Board.THRESHOLD);
            for (BoardEventListener l : listeners) {
                l.onTileAdded(selected, collapsing, value);
            }
            clean(selected, collapsing);
        }
    }

    /**
     * Mutate the selected tile (if possible)
     * @param selected
     */
    public void mutate(Tile selected) {
        if (selected.isMutable()) {
            selected.mutate();
            int level = selected.getLevel();
            dirty = true;
            stat.recordPutTile(selected.getLevel());//TODO record mutations specifically
            boolean collapsing = willCollapse(selected);
            for (BoardEventListener l : listeners) {
                l.onTileMutated(selected, collapsing, level);
            }

            clean(selected, collapsing);
        } else {
            Log.w(GameActivity.TAG, "selected tile was not mutable");
        }
    }

    /**
     * Return true if a collapse event will happen
     * @param selected
     * @return
     */
    private boolean willCollapse(Tile selected) {
        return (selected.findGroup().size() > Board.THRESHOLD);
    }

    /**
     * Collapse groups if needed until no collapse happen
     * @param selected the tile to check for collapsing
     * @param willCollapse
     */
    private void clean(Tile selected, boolean willCollapse) {
        while (isDirty()) {
            Set<Tile> group = compute(selected);

            if (group.size() >= Board.THRESHOLD) {
                for (BoardEventListener l : listeners) {
                    l.onGroupCollapsed(group, selected);
                }
            } else {
                if (willCollapse) {
                    for (BoardEventListener l : listeners) {
                        l.onCascadeFinished();
                    }
                }
            }
        }
        if (isGameOver()) {
            for (BoardEventListener l : listeners) {
                l.onGameOver();
            }
        }

    }


    public void setScore(int score) {
        this.score = score;
    }


    /**
     * Interface to be implemented by classes that wan't to be notified of game events
     */
    public interface BoardEventListener {
        /**
         * A new single tile is added to the board
         *
         * @param newTile the new tile
         * @param collapsing if true, a collapse will follow
         * @param origLevel the level of the added tile before any collapse
         */
        void onTileAdded(Tile newTile, boolean collapsing, int origLevel);

        /**
         * A  group collapse, creating a new tile
         *
         * @param group    the group of collapsed (reset to empty) tiles
         * @param promoted the new level-up tile
         */
        void onGroupCollapsed(Iterable<Tile> group, Tile promoted);

        /**
         * Called when the board is stable, i.e. no more collapse events will happen
         */
        void onCascadeFinished();

        /**
         * Called when the game is over
         */
        void onGameOver();

        void onTileMutated(Tile selected, boolean collapsing, int origLevel);
    }
}
