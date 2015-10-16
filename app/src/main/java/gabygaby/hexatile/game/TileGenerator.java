package gabygaby.hexatile.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Generate Tiles
 *
 */
public class TileGenerator {

    private int size;
    private Queue<Integer> futures;
    private int reserve;
    private Collection<GeneratorListener> listeners;

    public TileGenerator(int bufferSize) {
        size = bufferSize;
        listeners = new ArrayList<>();
        futures = new ArrayBlockingQueue<Integer>(bufferSize);
        for (int i = 0; i < size; i++) {
            futures.add(generate());
        }
    }

    /**
     * Generate a new tile
     * @return
     */
    private int generate() {
        //TODO : generation law
        return 1;
    }

    /**
     * get the list of future tiles
     * @return
     */
    public List<Integer> peekFutures() {
        ArrayList<Integer> r = new ArrayList<Integer>();
        for (int v : futures) {
            r.add(v);
        }
        return r;
    }

    /**
     *
     * @return
     */
    public int peekReserve() {
        return reserve;
    }

    /**
     * Get the last tile and update future tiles
     * @return
     */
    public int consume() {
        int v = futures.poll();
        futures.add(generate());
        for (GeneratorListener l : listeners) {
            l.onTileConsumed();
        }
        return v;
    }

    /**
     * Put the available tile to the reserve
     */
    public void toReserve() {
        if (isReserveFree()) {
            int v = consume();
            reserve = v;
            for (GeneratorListener l :listeners) {
                l.onTileConsumed();
                l.onReserveChanged();
            }
        } else {
            //TODO warning or exception
        }
    }

    /**
     * Get the Tile from the reserve, and empty the reserve
     */
    public int consumeReserve() {
        int v = reserve;
        reserve = 0;
        for (GeneratorListener l :listeners) {
            l.onReserveChanged();
        }
        return v;
    }

    /**
     * check if the reserve is occupied
     * @return
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

    interface GeneratorListener {
        void onTileConsumed();

        void onReserveChanged();
    }

}
