package gabygaby.hexatile.game;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of an hexagonal tile on the board
 *
 * Created by Rémi Pannequin on 25/09/15.
 */
public class Tile {

    private Tile right, left, up_left, up_right, down_left, down_right;
    private int level;
    private int index;

    public Tile(int index, int v) {
        this.level = v;
        this.index = index;
    }

    public void fill(int value) {
        if (isFree()) {
            level = value;
        }
    }

    public void setLevel(int level) {
        this.level = Math.min(level, 7);
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

    public void promote() {
        setLevel(this.level + 1);
    }

    public void consume() {
        this.level = 0;
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
            if (neighbour.getLevel() == level && !group.contains(neighbour)) {
                group.add(neighbour);
                neighbour.findGroup(group);
            }
        }
        return group;
    }

}
