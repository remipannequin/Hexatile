package gabygaby.hexatile.game;

import android.content.Context;

import gabygaby.hexatile.R;

/**
 * Names of the tiles kinds and levels
 *
 * the ordinal is the kind id
 */
public enum TileKind {
    VOID(new int[] {}),
    VEGETAL(new int[] {R.string.tile11,
            R.string.tile12,
            R.string.tile13,
            R.string.tile14,
            R.string.tile15,
            R.string.tile16,
            R.string.tile17,
            R.string.tile18}),
    WATER(new int[] {R.string.tile22,
            R.string.tile23,
            R.string.tile24,
            R.string.tile25,
            R.string.tile26,
            R.string.tile27,
            R.string.tile28}),
    FIRE(new int[] {R.string.tile33,
            R.string.tile34,
            R.string.tile35,
            R.string.tile36,
            R.string.tile37,
            R.string.tile38}),
    LIFE(new int[] {R.string.tile44,
            R.string.tile45,
            R.string.tile46,
            R.string.tile47,
            R.string.tile48});


    private int[] levelNames;

    TileKind(int[] levelNames) {
        this.levelNames = levelNames;
    }

    public String getName(Context ctx, int level) {
        int i = level - ordinal();
        if (i >= 0 && i < levelNames.length) {
            return ctx.getString(levelNames[i]);
        } else {
            return "error";
        }
    }


}



