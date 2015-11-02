package gabygaby.hexatile.ui;

import android.graphics.Path;

import gabygaby.hexatile.game.Tile;

/**
 * Created by remi on 26/10/15.
 */
public class TileDecorator {
    private static final TileDecorator ourInstance = new TileDecorator();
    private final Path hexa;
    private final Path[] vegetalLevel;
    private final Path plus;
    private final Path[] waterLevel;
    private final Path empty;


    public static TileDecorator getInstance() {
        return ourInstance;
    }

    private TileDecorator() {


        hexa = new Path();
        hexa.moveTo(0, 1);
        hexa.lineTo(BoardView.COS, BoardView.SIN);
        hexa.lineTo(BoardView.COS, -BoardView.SIN);
        hexa.lineTo(0, -1);
        hexa.lineTo(-BoardView.COS, -BoardView.SIN);
        hexa.lineTo(-BoardView.COS, BoardView.SIN);
        //hexa.lineTo(0, 1);
        hexa.close();
        hexa.setFillType(Path.FillType.WINDING);

        vegetalLevel = new Path[Tile.MAX_TILE_LEVEL - 1];
        waterLevel = new Path[Tile.MAX_TILE_LEVEL - 2];


        vegetalLevel[0] = new Path();
        vegetalLevel[0].moveTo(0, 0.9f);
        vegetalLevel[0].lineTo(0, 0.7f);

        vegetalLevel[1] = new Path();
        vegetalLevel[1].moveTo(-0.1f, 0.7f);
        vegetalLevel[1].lineTo(0, 0.9f);
        vegetalLevel[1].lineTo(0.1f, 0.7f);

        vegetalLevel[2] = new Path();
        vegetalLevel[2].moveTo(-0.1f, 0.7f);
        vegetalLevel[2].lineTo(0, 0.9f);
        vegetalLevel[2].lineTo(0.1f, 0.7f);
        vegetalLevel[2].close();

        vegetalLevel[3] = new Path();
        vegetalLevel[3].moveTo(0, 0.5f);
        vegetalLevel[3].lineTo(0, 0.7f);
        vegetalLevel[3].lineTo(0.1f, 0.7f);
        vegetalLevel[3].lineTo(0, 0.9f);
        vegetalLevel[3].lineTo(-0.1f, 0.7f);
        vegetalLevel[3].lineTo(0, 0.7f);

        vegetalLevel[4] = new Path();
        vegetalLevel[4].moveTo(BoardView.COS / 4f, 0.375f);
        vegetalLevel[4].lineTo(0, 0.5f);
        vegetalLevel[4].lineTo(0, 0.7f);
        vegetalLevel[4].lineTo(0.1f, 0.7f);
        vegetalLevel[4].lineTo(0, 0.9f);
        vegetalLevel[4].lineTo(-0.1f, 0.7f);
        vegetalLevel[4].lineTo(0, 0.7f);
        vegetalLevel[4].lineTo(0, 0.5f);
        vegetalLevel[4].lineTo(-BoardView.COS / 4f, 0.375f);

        vegetalLevel[5] = new Path();
        vegetalLevel[5].moveTo(BoardView.COS / 4f, 0.125f);
        vegetalLevel[5].lineTo(0, 0.5f);
        vegetalLevel[5].lineTo(0, 0.7f);
        vegetalLevel[5].lineTo(0.1f, 0.7f);
        vegetalLevel[5].lineTo(0, 0.9f);
        vegetalLevel[5].lineTo(-0.1f, 0.7f);
        vegetalLevel[5].lineTo(0, 0.7f);
        vegetalLevel[5].lineTo(0, 0.5f);
        vegetalLevel[5].lineTo(-BoardView.COS/4f, 0.125f);

        vegetalLevel[6] = new Path();





        //single circle center (0, 0.85), radius 0.5
        waterLevel[0] = new Path();
        waterLevel[0].addCircle(0, .85f, .05f, Path.Direction.CW);

        //previous + circle center (0, 0.65), radius 0.1
        waterLevel[1] = new Path();
        waterLevel[1].addCircle(0, .85f, .05f, Path.Direction.CW);
        waterLevel[1].addCircle(0, .65f, .1f, Path.Direction.CW);

        //vertical line from (0,0.9) to (0, 0.5)
        // + line from (-.01, 0.9) to (-0.1, 0.6) and bezier from (-0.1, 0.6) / (-0.1, 0.55)
        // to (-0.2, 0.5) / (-0.15, 0.5)
        // + and symetric right
        waterLevel[2] = new Path();
        waterLevel[2].moveTo( 0,    0.9f);
        waterLevel[2].lineTo( 0,    0.5f);
        waterLevel[2].moveTo( -.1f, 0.9f);
        waterLevel[2].lineTo( -.1f, 0.6f);
        waterLevel[2].cubicTo(-.1f, 0.55f, -.15f, .5f, -.2f, .5f);
        waterLevel[2].moveTo(0.1f, 0.9f);
        waterLevel[2].lineTo(0.1f, 0.6f);
        waterLevel[2].cubicTo(0.1f, 0.55f, 0.15f, .5f, 0.2f, .5f);

        //empty "lake"
        waterLevel[3] = new Path();
        waterLevel[3].moveTo(0, 0.9f);
        waterLevel[3].lineTo( 0, 0.5f);
        waterLevel[3].moveTo(0, 0.9f);
        waterLevel[3].cubicTo(0, 0.9f, 0, .55f, -.35f, .4f);
        waterLevel[3].moveTo(0, 0.9f);
        waterLevel[3].cubicTo(0, 0.9f, 0, .55f, 0.35f, .4f);


        //calm lake
        waterLevel[4] = new Path();
        waterLevel[4].moveTo(0, 0.9f);
        waterLevel[4].lineTo( 0, 0.5f);
        waterLevel[4].moveTo(0, 0.9f);
        waterLevel[4].cubicTo(0, 0.9f, 0, .55f, -.35f, .4f);
        waterLevel[4].moveTo(0, 0.9f);
        waterLevel[4].cubicTo(0, 0.9f, 0, .55f, 0.35f, .4f);
        waterLevel[4].moveTo(0, 0.8f);
        waterLevel[4].lineTo(-.35f, 0.6f);
        waterLevel[4].moveTo(0, 0.8f);
        waterLevel[4].lineTo( 0.35f, 0.6f);


        //calm sea
        waterLevel[5] = new Path();
        waterLevel[5].moveTo(0, 0.9f);
        waterLevel[5].lineTo(0, 0.5f);
        waterLevel[5].moveTo(0, 0.9f);
        waterLevel[5].cubicTo(0, 0.9f, 0, .55f, -.35f, .4f);
        waterLevel[5].moveTo(0, 0.9f);
        waterLevel[5].cubicTo(0, 0.9f, 0, .55f, 0.35f, .4f);
        waterLevel[5].moveTo(    0f, 0.9f);
        waterLevel[5].cubicTo(0f, 0.9f, -.02f, 0.82f, -.05f, 0.8f);
        waterLevel[5].cubicTo(-.08f, 0.78f, -.12f, 0.82f, -.15f, 0.8f);
        waterLevel[5].cubicTo(-.18f, 0.78f, -.17f, 0.72f, -.2f, 0.7f);
        waterLevel[5].cubicTo(-.23f, 0.68f, -.23f, 0.72f, -.3f, 0.7f);
        waterLevel[5].cubicTo(-.33f, 0.68f, -.32f, 0.62f, -.35f, 0.6f);
        waterLevel[5].moveTo(0f, 0.9f);
        waterLevel[5].cubicTo(0f, 0.9f, 0.02f, 0.82f, 0.05f, 0.8f);
        waterLevel[5].cubicTo(0.08f, 0.78f, 0.12f, 0.82f, 0.15f, 0.8f);
        waterLevel[5].cubicTo(0.18f, 0.78f, 0.17f, 0.72f, 0.2f, 0.7f);
        waterLevel[5].cubicTo(0.23f, 0.68f, 0.23f, 0.72f, 0.3f, 0.7f);
        waterLevel[5].cubicTo(0.33f, 0.68f, 0.32f, 0.62f, 0.35f, 0.6f);

        plus = new Path();
        plus.moveTo(0, 0.3f);
        plus.lineTo(0, -0.3f);
        plus.moveTo(0.3f, 0);
        plus.lineTo(-0.3f, 0);

        empty = new Path();
    }


    public Path getHexagon() {
        return hexa;
    }

    public Path getLevelDecoration(int level, int kind) {
        if (level < (kind + 1)) {
            return empty;
        }
        switch (kind) {
            case 1:
                return vegetalLevel[level - kind - 1];
            case 2:
                return waterLevel[level - kind - 1];
            case 3:

            case 4:

            default:
                return empty;
        }
    }

    public Path getPlus() {
        return plus;
    }
}
