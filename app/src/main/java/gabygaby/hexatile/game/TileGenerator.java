package gabygaby.hexatile.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import gabygaby.hexatile.GameActivity;

/**
 * Generate Tiles
 */
public class TileGenerator {

    private final int size;
    private final Queue<Integer> futures;
    private int stash;
    private final Collection<GeneratorListener> listeners;
    private boolean stashSelected = false;

    public TileGenerator(int bufferSize) {
        size = bufferSize;
        listeners = new ArrayList<>();
        futures = new ArrayBlockingQueue<>(bufferSize);
        for (int i = 0; i < size; i++) {
            futures.add(generate());
        }
    }

    /**
     * Generate a new tile
     *
     * @return the level of the new tile
     */
    private int generate() {
        //exponential generation law
        int level = 1;
        while (Math.random() < 0.16 && level < Tile.MAX_TILE_LEVEL) {
            level++;
        }
        return level;
    }

    /**
     * get the list of future tiles
     *
     * @return the list of future tiles
     */
    public List<Integer> peekFutures() {
        ArrayList<Integer> r = new ArrayList<>();
        for (int v : futures) {
            r.add(v);
        }
        return r;

    }

    /**
     * @return the content of the stash
     */
    public int peekStash() {
        return stash;
    }

    /**
     * Get the tile value (level) either from the nomral source,
     * or the tile stored in the stash, depending on the source selected
     * <p/>
     * <p/>
     * Get the last tile and update future tiles
     * Get the Tile from the stash, and empty the stash
     *
     * @return the tile's level generated from the selected source
     */
    public int consume() {
        int v;
        if (stashSelected) {
            v = stash;
            stash = 0;
            for (GeneratorListener l : listeners) {
                l.onStashChanged();
            }
            selectStash(false);
        } else {
            v = futures.poll();
            futures.add(generate());
            for (GeneratorListener l : listeners) {
                l.onTileConsumed();
            }
        }
        return v;
    }

    /**
     * Put the available tile to the stash
     */
    public void stash() {
        if (isStashPlaceFree()) {
            stash = consume();
            for (GeneratorListener l : listeners) {
                l.onTileConsumed();
                l.onStashChanged();
            }
        } else {
            Log.w(GameActivity.TAG, "trying to move a tile to the stash, but it is not free"); //NON-NLS
        }
    }


    /**
     * check if the stash is occupied
     *
     * @return true if the stash contains a tile
     */
    public boolean isStashPlaceFree() {
        return (stash == 0);
    }

    public void addListener(GeneratorListener l) {
        listeners.add(l);
    }

    public void removeListener(GeneratorListener l) {
        listeners.remove(l);
    }

    public void clearListener() {
        listeners.clear();
    }

    public int getSize() {
        return size;
    }

    public void selectStash(boolean b) {
        stashSelected = b;
        for(GeneratorListener l :listeners) {
            l.onSourceChanged(stashSelected);
        }
    }

    public interface GeneratorListener {
        void onTileConsumed();

        void onStashChanged();

        void onSourceChanged(boolean fromStash);
    }

}
