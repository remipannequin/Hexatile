package gabygaby.hexatile.ui;

import android.graphics.Path;

/**
 * Created by remi on 26/10/15.
 */
public class TileDecorator {
    private static TileDecorator ourInstance = new TileDecorator();
    private final Path hexa;
    private final Path[] vegetalLevel;
    private final Path plus;


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

        vegetalLevel = new Path[6];
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
        vegetalLevel[4].lineTo(-BoardView.COS/4f, 0.375f);

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

        plus = new Path();
        plus.moveTo(0, 0.3f);
        plus.lineTo(0, -0.3f);
        plus.moveTo(0.3f, 0);
        plus.lineTo(-0.3f, 0);
    }


    public Path getHexagon() {
        return hexa;
    }

    public Path getLevelDecoration(int level, int kind) {
        switch (kind) {
            case 1:
                return vegetalLevel[level-2];
            case 2:

            case 3:

            case 4:

            default:
                return new Path();
        }
    }

    public Path getPlus() {
        return plus;
    }
}
