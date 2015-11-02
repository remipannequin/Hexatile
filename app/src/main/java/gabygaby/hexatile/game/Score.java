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
        if (size > (Board.THRESHOLD + 1)) {
            reward *= 2;
        }
        if (size >= 9) {
            reward *= 2;
        }
        reward *= Math.pow(2, level - 2);
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

    /**
     * Compute the mutation limit for a tile.
     * A tile is mutable if it level is strictly superior than its kind (i.e. tile of kind = 1 are
     * mutable stating at level 2) and if a superior kind exists.
     *
     * @param level the level of the tile
     * @param kind the kind of tile
     * @return the limit to be mutable, or maxvalue, if the level or kind does not enable mutation
     */
    public static int limit(int level,int kind) {
        if (kind >= Tile.MAX_TILE_KIND || level <= kind) {
            return Integer.MAX_VALUE;
        }
        return (int)(Math.pow(Board.THRESHOLD + 1, level - kind) * Math.pow(Board.THRESHOLD + 2, kind));
    }

}
