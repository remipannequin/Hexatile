package gabygaby.hexatile.game;

import java.util.Set;

/**
 * Scoring rules.
 *
 * The basic scheme is :
 * - 1 point for a basic collapse of 4 level-1 tiles.
 * - double if there is more than 6 tiles
 * - double if there are 9 tiles (maximum number in a hexagon-tiled board)
 * - double if level is higher
 * ...
 *  - the double bonus adds
 */
public class Score {

    /**
     * Compute score in case of group collapse
     * @param last the promoted tile
     * @param group the group
     * @return
     */
    public static int collapse(Tile last, Set<Tile> group) {
        int reward = 1;
        int size = group.size();
        int level = last.getLevel();
        if (size > Board.THRESHOLD) {
            reward *= 2;
        }
        if (size > 9) {
            reward *= 2;
        }
        reward *= Math.pow(2, level - 1);
        return reward;
    }



}
