package gabygaby.hexatile.game;

import java.util.HashSet;
import java.util.Set;

/**
 * Logical representation of an hexagonal tile on the board
 *
 * Created by Rémi Pannequin on 25/09/15.
 */
public class Tile {
    
    public static final int MAX_TILE_LEVEL = 8;

    /**
     * neighbours
     */
    private Tile right, left, up_left, up_right, down_left, down_right;
    /**
     * level of this tile
     */
    private int level;
    /**
     * Kind of the tile it is
     */
    private int kind;
    /**
     * index of the tile in the board
     */
    private int index;
    /**
     * the total number of level 1 tiles that have been fused to create this tile
     */
    private int value;

    /**
     * CReate a new tile. If level is not zero, the kind is the first (vegetal)
     * @param index
     * @param v
     */
    public Tile(int index, int v) {
        this.level = v;
        kind = (level == 0 ? 0 : 1);
        this.index = index;
    }

    public void fill(int level) {
        if (isFree()) {
            this.kind = 1;
            this.level = level;
            this.value = (int)Math.pow(4, level - 1);
        }
    }

    public void setLevel(int level) {
        this.level = Math.min(level, MAX_TILE_LEVEL);
    }


    public int getIndex() {
        return index;
    }


    public Tile(int index) {
        this.level = 0;
        this.index = index;
    }


    public Tile[] getNeighbours() {
        return new Tile[]{left, up_left, up_right, right, down_right, down_left};
    }


    public int getLevel() {
        return level;
    }

    /**
     * increment the tile's level
     */
    public void promote(int group_value) {
        setLevel(this.level + 1);
        value += group_value;
    }

    /**
     * increment the tile's kind
     */
    public void mutate() {
        kind++;
        level--;
    }

    /**
     *
     */
    public boolean isMutable() {
        if (level < 2) {
            return false;
        }
        int limit = (int)Math.pow(5, kind) * (int)Math.pow(4, level - kind - 1);
        return value >= limit;
    }

    /**
     * Cunsume this tile (in a colapse event)
     * reset its level, kind and value.
     * @return the value of the tile, to be added to the promoted tile
     */
    public int consume() {
        int v = value;
        this.level = 0;
        this.value = 0;
        this.kind = 0;
        return v;
    }

    public Tile getRight() {
        return right;
    }

    public void setRight(Tile right) {
        this.right = right;
    }

    public Tile getLeft() {
        return left;
    }

    public void setLeft(Tile left) {
        this.left = left;
    }

    public Tile getUpLeft() {
        return up_left;
    }

    public void setUpLeft(Tile up_left) {
        this.up_left = up_left;
    }

    public Tile getUpRight() {
        return up_right;
    }

    public void setUpRight(Tile up_right) {
        this.up_right = up_right;
    }

    public Tile getDownLeft() {
        return down_left;
    }

    public void setDownLeft(Tile down_left) {
        this.down_left = down_left;
    }

    public Tile getDownRight() {
        return down_right;
    }

    public void setDownRight(Tile down_right) {
        this.down_right = down_right;
    }

    public boolean isFree() {
        return (level == 0);
    }

    /**
     * Compute the group (i.e. connected tiles of same level) that this tile belongs to.
     * @return the group (including the current tile)
     */
    public Set<Tile> findGroup() {
        Set<Tile> group = new HashSet<>();
        group.add(this);
        return findGroup(group);
    }

    /**
     * Grow the group being constituted
     * @param group the current group
     * @return the group after growing
     */
    private Set<Tile> findGroup(Set<Tile> group) {
        for (Tile neighbour: getNeighbours()) {
            if (neighbour.getLevel() == level &&
                    neighbour.getKind() == kind &&
                    !group.contains(neighbour)) {
                group.add(neighbour);
                neighbour.findGroup(group);
            }
        }
        return group;
    }

    public int getKind() {
        return kind;
    }

    public int getValue() {
        return value;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
