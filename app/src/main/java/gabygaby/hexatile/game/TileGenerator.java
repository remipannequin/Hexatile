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

    private int size;
    private Queue<Integer> futures;
    private int reserve;
    private Collection<GeneratorListener> listeners;
    private boolean reserveSelected = false;

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
     * @return the content of the reserve
     */
    public int peekReserve() {
        return reserve;
    }

    /**
     * Get the tile value (level) either from the nomral source,
     * or the tile stored in the reserve, depending on the source selected
     * <p/>
     * <p/>
     * Get the last tile and update future tiles
     * Get the Tile from the reserve, and empty the reserve
     *
     * @return the tile's level generated from the selected source
     */
    public int consume() {
        int v;
        if (reserveSelected) {
            v = reserve;
            reserve = 0;
            for (GeneratorListener l : listeners) {
                l.onReserveChanged();
            }
            selectReserve(false);
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
     * Put the available tile to the reserve
     */
    public void toReserve() {
        if (isReserveFree()) {
            reserve = consume();
            for (GeneratorListener l : listeners) {
                l.onTileConsumed();
                l.onReserveChanged();
            }
        } else {
            Log.w(GameActivity.TAG, "trying to move a tile to the reserve, but it is not free"); //NON-NLS
        }
    }


    /**
     * check if the reserve is occupied
     *
     * @return true if the reserve contains a tile
     */
    public boolean isReserveFree() {
        return (reserve == 0);
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

    public void selectReserve(boolean b) {
        reserveSelected = b;
        for(GeneratorListener l :listeners) {
            l.onSourceChanged(reserveSelected);
        }
    }

    public interface GeneratorListener {
        void onTileConsumed();

        void onReserveChanged();

        void onSourceChanged(boolean fromReserve);
    }

}
