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
        int kind = last.getKind();
        int level = last.getLevel();
        if (size > Board.THRESHOLD) {
            reward *= 2;
        }
        if (size > (Board.THRESHOLD * 2)) {
            reward *= 2;
        }
        reward *= Math.pow(2, level - 1);
        reward *= Math.pow(2, kind - 1);

        return reward;
    }


    /**
     * A mutation of a level-n tile scores the same as a 4-tile group colapse of level-n tiles
     * @param selected
     * @return
     */
    public static int mutate(Tile selected) {
        int reward = 1;
        int kind = selected.getKind();
        int level = selected.getLevel();
        reward *= Math.pow(2, level - 1);
        reward *= Math.pow(2, kind - 1);

        return reward;
    }

}
