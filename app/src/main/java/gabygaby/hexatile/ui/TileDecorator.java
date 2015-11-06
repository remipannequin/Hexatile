package gabygaby.hexatile.ui;

import android.graphics.Path;
import android.graphics.RectF;

import gabygaby.hexatile.game.Tile;

/**
 * Create (mostly static) Path to decorate tiles
 *
 */
public class TileDecorator {
    private static final TileDecorator ourInstance = new TileDecorator();
    private final Path hexagon;

    private final Path plus;
    private final Path empty;

    private final Path[] vegetalLevel;
    private final Path[] waterLevel;
    private final Path[] fireLevel;
    private final Path[] lifeLevel;
    private Path mesh;
    private long meshHash = 0;


    public static TileDecorator getInstance() {
        return ourInstance;
    }

    private TileDecorator() {


        hexagon = new Path();
        hexagon.moveTo(0, 1);
        hexagon.lineTo(BoardView.COS, BoardView.SIN);
        hexagon.lineTo(BoardView.COS, -BoardView.SIN);
        hexagon.lineTo(0, -1);
        hexagon.lineTo(-BoardView.COS, -BoardView.SIN);
        hexagon.lineTo(-BoardView.COS, BoardView.SIN);
        //hexagon.lineTo(0, 1);
        hexagon.close();
        hexagon.setFillType(Path.FillType.WINDING);

        vegetalLevel = new Path[Tile.MAX_TILE_LEVEL - 1];
        waterLevel = new Path[Tile.MAX_TILE_LEVEL - 2];
        fireLevel = new Path[Tile.MAX_TILE_LEVEL - 3];
        lifeLevel = new Path[Tile.MAX_TILE_LEVEL - 4];

        // grass
        vegetalLevel[0] = new Path();
        vegetalLevel[0].moveTo(0, 0.9f);
        vegetalLevel[0].lineTo(0, 0.7f);

        // grass tuft
        vegetalLevel[1] = new Path();
        vegetalLevel[1].moveTo(-0.1f, 0.7f);
        vegetalLevel[1].lineTo(0, 0.9f);
        vegetalLevel[1].lineTo(0.1f, 0.7f);

        // bush
        vegetalLevel[2] = new Path();
        vegetalLevel[2].moveTo(-0.1f, 0.7f);
        vegetalLevel[2].lineTo(0, 0.9f);
        vegetalLevel[2].lineTo(0.1f, 0.7f);
        vegetalLevel[2].close();

        //single tree
        vegetalLevel[3] = new Path();
        vegetalLevel[3].moveTo(0, 0.5f);
        vegetalLevel[3].lineTo(0, 0.7f);
        vegetalLevel[3].lineTo(0.1f, 0.7f);
        vegetalLevel[3].lineTo(0, 0.9f);
        vegetalLevel[3].lineTo(-0.1f, 0.7f);
        vegetalLevel[3].lineTo(0, 0.7f);

        // tree with roots
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

        //forest
        vegetalLevel[5] = new Path();
        vegetalLevel[5].moveTo(BoardView.COS / 4f, 0.375f);
        vegetalLevel[5].lineTo(0, 0.5f);
        vegetalLevel[5].lineTo(0, 0.7f);
        vegetalLevel[5].lineTo(0.1f, 0.7f);
        vegetalLevel[5].lineTo(0, 0.9f);
        vegetalLevel[5].lineTo(-0.1f, 0.7f);
        vegetalLevel[5].lineTo(0, 0.7f);
        vegetalLevel[5].lineTo(0, 0.5f);
        vegetalLevel[5].lineTo(-BoardView.COS / 4f, 0.375f);
        vegetalLevel[5].addArc(new RectF(-.45f, 0.50f, -.25f, 0.70f), 270, 270 + 180);
        vegetalLevel[5].moveTo(-BoardView.COS / 4f, 0.375f);
        vegetalLevel[5].lineTo(-.29f, 0.51f);

        //jungle
        vegetalLevel[6] = new Path();
        vegetalLevel[6].moveTo(BoardView.COS / 4f, 0.375f);
        vegetalLevel[6].lineTo(0, 0.5f);
        vegetalLevel[6].lineTo(-BoardView.COS / 4f, 0.375f);
        vegetalLevel[6].lineTo(-.29f, 0.51f);
        vegetalLevel[6].addArc(new RectF(-.45f, 0.50f, -.25f, 0.70f), 270, 270 + 180);

        vegetalLevel[6].moveTo(0.00f, 0.50f);
        vegetalLevel[6].lineTo(0.00f, 0.80f);
        vegetalLevel[6].lineTo(-.20f, 0.80f);
        vegetalLevel[6].lineTo(0.00f, 0.90f);
        vegetalLevel[6].lineTo(0.20f, 0.80f);
        vegetalLevel[6].lineTo(0.00f, 0.80f);

        vegetalLevel[6].moveTo(-.08f, 0.45f);
        vegetalLevel[6].lineTo(-.04f, 0.57f);
        vegetalLevel[6].lineTo(-.10f, 0.48f);
        vegetalLevel[6].lineTo(-.10f, 0.62f);
        vegetalLevel[6].lineTo(-.12f, 0.48f);
        vegetalLevel[6].lineTo(-.22f, 0.57f);
        vegetalLevel[6].lineTo(-.14f, 0.46f);
        vegetalLevel[6].lineTo(-.24f, 0.47f);
        vegetalLevel[6].lineTo(-.12f, 0.43f);

        vegetalLevel[6].moveTo(0.08f, 0.45f);
        vegetalLevel[6].lineTo(0.04f, 0.57f);
        vegetalLevel[6].lineTo(0.10f, 0.48f);
        vegetalLevel[6].lineTo(0.10f, 0.62f);
        vegetalLevel[6].lineTo(0.12f, 0.48f);
        vegetalLevel[6].lineTo(0.22f, 0.57f);
        vegetalLevel[6].lineTo(0.14f, 0.46f);
        vegetalLevel[6].lineTo(0.24f, 0.47f);
        vegetalLevel[6].lineTo(0.12f, 0.43f);



        // drop
        waterLevel[0] = new Path();
        waterLevel[0].addCircle(0, .85f, .05f, Path.Direction.CW);

        // rain
        waterLevel[1] = new Path();
        waterLevel[1].addCircle(0, .85f, .05f, Path.Direction.CW);
        waterLevel[1].addCircle(0, .65f, .1f, Path.Direction.CW);

        // river
        waterLevel[2] = new Path();
        waterLevel[2].moveTo(0, 0.9f);
        waterLevel[2].lineTo(0, 0.5f);
        waterLevel[2].moveTo(-.1f, 0.85f);
        waterLevel[2].lineTo(-.1f, 0.6f);
        waterLevel[2].cubicTo(-.1f, 0.55f, -.15f, .5f, -.2f, .5f);
        waterLevel[2].moveTo(0.1f, 0.85f);
        waterLevel[2].lineTo(0.1f, 0.6f);
        waterLevel[2].cubicTo(0.1f, 0.55f, 0.15f, .5f, 0.2f, .5f);

        // lake
        waterLevel[3] = new Path();
        waterLevel[3].moveTo(0, 0.9f);
        waterLevel[3].cubicTo(0, 0.9f, 0, .55f, -.25f, .4f);
        waterLevel[3].moveTo(0, 0.9f);
        waterLevel[3].cubicTo(0, 0.9f, 0, .55f, 0.25f, .4f);
        waterLevel[3].moveTo(-.04f, 0.70f);
        waterLevel[3].lineTo(-.32f, 0.54f);
        waterLevel[3].moveTo(0.04f, 0.70f);
        waterLevel[3].lineTo(0.32f, 0.54f);


        // sea
        waterLevel[4] = new Path();
        waterLevel[4].moveTo(0, 0.9f);
        waterLevel[4].cubicTo(0, 0.9f, 0, .55f, -.25f, .4f);
        waterLevel[4].moveTo(0, 0.9f);
        waterLevel[4].cubicTo(0, 0.9f, 0, .55f, 0.25f, .4f);
        waterLevel[4].moveTo(0f, 0.9f);
        waterLevel[4].cubicTo(0f, 0.9f, -.02f, 0.82f, -.05f, 0.8f);
        waterLevel[4].cubicTo(-.08f, 0.78f, -.12f, 0.82f, -.15f, 0.8f);
        waterLevel[4].cubicTo(-.18f, 0.78f, -.17f, 0.72f, -.2f, 0.7f);
        waterLevel[4].cubicTo(-.23f, 0.68f, -.23f, 0.72f, -.3f, 0.7f);
        waterLevel[4].cubicTo(-.33f, 0.68f, -.32f, 0.62f, -.35f, 0.6f);
        waterLevel[4].moveTo(0f, 0.9f);
        waterLevel[4].cubicTo(0f, 0.9f, 0.02f, 0.82f, 0.05f, 0.8f);
        waterLevel[4].cubicTo(0.08f, 0.78f, 0.12f, 0.82f, 0.15f, 0.8f);
        waterLevel[4].cubicTo(0.18f, 0.78f, 0.17f, 0.72f, 0.2f, 0.7f);
        waterLevel[4].cubicTo(0.23f, 0.68f, 0.23f, 0.72f, 0.3f, 0.7f);
        waterLevel[4].cubicTo(0.33f, 0.68f, 0.32f, 0.62f, 0.35f, 0.6f);

        //Ocean
        waterLevel[5] = new Path();
        waterLevel[5].moveTo(0.37f, 0.64f);
        waterLevel[5].cubicTo(0.37f, 0.64f, 0.10f, 0.55f, 0.30f, 0.74f);
        waterLevel[5].cubicTo(0.15f, 0.80f, 0.00f, 0.50f, 0.10f, 0.79f);
        waterLevel[5].cubicTo(-.05f, 0.85f, -.15f, 0.45f, -.10f, 0.79f);
        waterLevel[5].cubicTo(-.25f, 0.80f, -.35f, 0.35f, -.30f, 0.75f);
        waterLevel[5].cubicTo(-.40f, 0.65f, -.37f, 0.64f, -.37f, 0.64f);
        waterLevel[5].moveTo(0.10f, 0.55f);
        waterLevel[5].cubicTo(0.00f, 0.45f, -.05f, 0.55f, -.10f, 0.60f);
        waterLevel[5].cubicTo(-.09f, 0.55f, -.09f, 0.55f, -.10f, 0.50f);
        waterLevel[5].cubicTo(-.05f, 0.55f, 0.00f, 0.65f, 0.10f, 0.55f);


        //spark
        fireLevel[0] = new Path();
        fireLevel[0].moveTo(-.05f, 0.60f);
        fireLevel[0].lineTo(0.05f, 0.50f);
        fireLevel[0].moveTo(0.00f, 0.60f);
        fireLevel[0].lineTo(0.00f, 0.50f);
        fireLevel[0].moveTo(-.15f, 0.55f);
        fireLevel[0].lineTo(0.15f, 0.55f);
        fireLevel[0].moveTo(-.10f, 0.50f);
        fireLevel[0].lineTo(0.10f, 0.60f);

        //smoke
        fireLevel[1] = new Path();
        fireLevel[1].moveTo(-.05f, 0.60f);
        fireLevel[1].lineTo(0.05f, 0.50f);
        fireLevel[1].moveTo(-.15f, 0.55f);
        fireLevel[1].lineTo(0.15f, 0.55f);
        fireLevel[1].moveTo(-.10f, 0.50f);
        fireLevel[1].lineTo(0.10f, 0.60f);
        fireLevel[1].moveTo(0, 0.50f);
        fireLevel[1].cubicTo(0.00f, 0.65f, 0.05f, 0.65f, 0.05f, 0.70f);
        fireLevel[1].cubicTo(0.05f, 0.75f, -.05f, 0.75f, -.05f, 0.80f);
        fireLevel[1].cubicTo(-.05f, 0.85f, 0.10f, 0.80f, 0.10f, 0.85f);
        fireLevel[1].cubicTo(0.10f, 0.90f, 0.05f, 0.85f, -.10f, 0.910f);

        //flame
        fireLevel[2] = new Path();
        fireLevel[2].moveTo(-.15f, 0.55f);
        fireLevel[2].lineTo(0.15f, 0.55f);
        fireLevel[2].moveTo(-.00f, 0.55f);
        fireLevel[2].lineTo(-.10f, 0.50f);
        fireLevel[2].moveTo(-.00f, 0.55f);
        fireLevel[2].lineTo(0.05f, 0.50f);
        fireLevel[2].moveTo(-.00f, 0.55f);
        fireLevel[2].cubicTo(-.05f, 0.55f, -.10f, 0.60f, -.10f, 0.65f);
        fireLevel[2].cubicTo(-.10f, 0.70f, -.05f, 0.80f, 0.00f, 0.85f);
        fireLevel[2].cubicTo(0.00f, 0.75f, 0.05f, 0.70f, 0.10f, 0.65f);
        fireLevel[2].cubicTo(0.10f, 0.60f, 0.05f, 0.55f, 0.00f, 0.55f);

        // fire
        fireLevel[3] = new Path();
        fireLevel[3].moveTo(-.15f, 0.55f);
        fireLevel[3].lineTo(0.15f, 0.55f);
        fireLevel[3].moveTo(-.00f, 0.55f);
        fireLevel[3].lineTo(-.10f, 0.50f);
        fireLevel[3].moveTo(-.00f, 0.55f);
        fireLevel[3].lineTo(0.05f, 0.50f);
        fireLevel[3].moveTo(-.00f, 0.55f);
        fireLevel[3].cubicTo(-.05f, 0.55f, -.10f, 0.60f, -.10f, 0.65f);
        fireLevel[3].cubicTo(-.10f, 0.70f, -.05f, 0.75f, 0.00f, 0.80f);
        fireLevel[3].cubicTo(0.00f, 0.70f, 0.05f, 0.70f, 0.10f, 0.65f);
        fireLevel[3].cubicTo(0.10f, 0.60f, 0.05f, 0.55f, 0.00f, 0.55f);
        fireLevel[3].moveTo(-.00f, 0.55f);
        fireLevel[3].cubicTo(-.05f, 0.55f, -.10f, 0.55f, -.15f, 0.60f);
        fireLevel[3].cubicTo(-.20f, 0.65f, -.25f, 0.70f, -.15f, 0.85f);
        fireLevel[3].cubicTo(-.15f, 0.80f, -.15f, 0.75f, -.15f, 0.70f);
        fireLevel[3].cubicTo(-.10f, 0.80f, -.05f, 0.90f, 0.05f, 0.95f);
        fireLevel[3].cubicTo(0.05f, 0.85f, 0.10f, 0.80f, 0.15f, 0.85f);
        fireLevel[3].cubicTo(0.25f, 0.70f, 0.20f, 0.65f, 0.15f, 0.60f);
        fireLevel[3].cubicTo(0.10f, 0.55f, 0.05f, 0.55f, 0.00f, 0.55f);

        //f*cking fire everywhere !!!
        fireLevel[4] = new Path();
        fireLevel[4].moveTo(0.30f, 0.50f);
        fireLevel[4].cubicTo(0.20f, 0.55f, 0.15f, 0.55f, 0.25f, 0.65f);
        fireLevel[4].cubicTo(0.10f, 0.65f, 0.00f, 0.70f, 0.00f, 0.80f);
        fireLevel[4].cubicTo(-.05f, 0.75f, -.05f, 0.50f, -.25f, 0.70f);
        fireLevel[4].cubicTo(-.25f, 0.65f, -.20f, 0.55f, -.30f, 0.50f);
        fireLevel[4].moveTo(0.35f, 0.60f);
        fireLevel[4].cubicTo(0.25f, 0.55f, 0.30f, 0.65f, 0.35f, 0.70f);
        fireLevel[4].cubicTo(0.25f, 0.70f, 0.20f, 0.70f, 0.35f, 0.75f);
        fireLevel[4].cubicTo(0.20f, 0.75f, 0.15f, 0.65f, 0.15f, 0.85f);
        fireLevel[4].cubicTo(0.10f, 0.80f, 0.05f, 0.85f, 0.05f, 0.95f);
        fireLevel[4].cubicTo(-.05f, 0.90f, -.10f, 0.80f, -.15f, 0.70f);
        fireLevel[4].cubicTo(-.15f, 0.75f, -.15f, 0.80f, -.15f, 0.85f);
        fireLevel[4].cubicTo(-.25f, 0.70f, -.20f, 0.70f, -.25f, 0.80f);
        fireLevel[4].cubicTo(-.30f, 0.70f, -.35f, 0.65f, -.35f, 0.75f);
        fireLevel[4].cubicTo(-.40f, 0.70f, -.35f, 0.65f, -.35f, 0.60f);

        // unicellular
        lifeLevel[0] = new Path();
        lifeLevel[0].addCircle(0.00f, 0.75f, 0.15f, Path.Direction.CW);
        lifeLevel[0].addCircle(0.05f, 0.78f, 0.05f, Path.Direction.CW);
        lifeLevel[0].moveTo(-.03f, 0.82f);
        lifeLevel[0].cubicTo(-.05f, 0.82f, -.08f, 0.79f, -.08f, 0.77f);
        lifeLevel[0].cubicTo(-.08f, 0.75f, -.03f, 0.74f, -.03f, 0.72f);
        lifeLevel[0].cubicTo(-.03f, 0.70f, -.06f, 0.67f, -.08f, 0.67f);

        // worm
        lifeLevel[1] = new Path();
        lifeLevel[1].moveTo(-.25f, 0.75f);
        lifeLevel[1].cubicTo(-.20f, 0.70f, -.20f, 0.65f, -.10f, 0.65f);
        lifeLevel[1].cubicTo(0.00f, 0.65f, 0.00f, 0.75f, 0.10f, 0.75f);
        lifeLevel[1].cubicTo(0.20f, 0.75f, 0.20f, 0.65f, 0.30f, 0.70f);


        // insect
        lifeLevel[2] = new Path();
        lifeLevel[2].addArc(new RectF(-.05f, 0.75f, 0.05f, 0.85f), 0, 360);
        lifeLevel[2].addArc(new RectF(-.10f, 0.50f, 0.10f, 0.75f), 0, 360);
        lifeLevel[2].moveTo( -.03f, .83f);
        lifeLevel[2].cubicTo(-.03f, 0.83f, -.04f, 0.93f, -.08f, 0.88f);
        lifeLevel[2].moveTo(0.03f, .83f);
        lifeLevel[2].cubicTo(0.03f, 0.83f, 0.04f, 0.93f, 0.08f, 0.88f);
        lifeLevel[2].moveTo(-.09f, .69f);
        lifeLevel[2].cubicTo(-.09f, 0.69f, -.15f, 0.75f, -.15f, 0.80f);
        lifeLevel[2].moveTo(0.09f, .69f);
        lifeLevel[2].cubicTo(0.09f, 0.69f, 0.15f, 0.75f, 0.15f, 0.80f);
        lifeLevel[2].moveTo(-.10f, 0.625f);
        lifeLevel[2].lineTo(-.15f, 0.625f);
        lifeLevel[2].moveTo(0.10f, 0.625f);
        lifeLevel[2].lineTo(0.15f, 0.625f);
        lifeLevel[2].moveTo(-.09f, .56f);
        lifeLevel[2].cubicTo(-.09f, 0.56f, -.15f, 0.50f, -.15f, 0.45f);
        lifeLevel[2].moveTo(0.09f, .56f);
        lifeLevel[2].cubicTo(0.09f, 0.56f, 0.15f, 0.50f, 0.15f, 0.45f);


        // tiger / wolf face
        lifeLevel[3] = new Path();
        lifeLevel[3].addArc(new RectF(-.05f, 0.575f, 0.05f, 0.625f), 0, 360);
        lifeLevel[3].moveTo(0.00f, 0.50f);
        lifeLevel[3].lineTo( -.05f, 0.45f);
        lifeLevel[3].lineTo(-.10f, 0.55f);
        lifeLevel[3].lineTo(-.15f, 0.55f);
        lifeLevel[3].lineTo(-.10f, .60f);
        lifeLevel[3].lineTo(-.15f, 0.60f);
        lifeLevel[3].cubicTo(-.16f, 0.64f, -.16f, 0.71f, -.15f, 0.75f);
        lifeLevel[3].lineTo(-.15f, 0.85f);
        lifeLevel[3].lineTo(-.10f, 0.80f);
        lifeLevel[3].cubicTo(-.03f, 0.82f, 0.03f, 0.82f, 0.10f, 0.80f);
        lifeLevel[3].lineTo(0.15f, 0.85f);
        lifeLevel[3].lineTo(0.15f, 0.75f);
        lifeLevel[3].cubicTo(0.16f, 0.71f, 0.16f, 0.64f, 0.15f, 0.60f);
        lifeLevel[3].lineTo(0.10f, .60f);
        lifeLevel[3].lineTo(0.15f, 0.55f);
        lifeLevel[3].lineTo(0.10f, 0.55f);
        lifeLevel[3].lineTo(0.05f, 0.45f);
        lifeLevel[3].lineTo(0.00f, 0.50f);
        lifeLevel[3].moveTo(-.10f, 0.75f);
        lifeLevel[3].lineTo(-.05f, 0.725f);
        lifeLevel[3].lineTo(-.05f, 0.60f);
        lifeLevel[3].moveTo(0.10f, 0.75f);
        lifeLevel[3].lineTo(0.05f, 0.725f);
        lifeLevel[3].lineTo(0.05f, 0.60f);



        plus = new Path();
        float a = 0.08f;
        float b = 0.24f;
        plus.moveTo(a, a);
        plus.lineTo(a, b);
        plus.lineTo(-a, b);
        plus.lineTo(-a, a);
        plus.lineTo(-b, a);
        plus.lineTo(-b, -a);
        plus.lineTo(-a, -a);
        plus.lineTo(-a, -b);
        plus.lineTo(a, -b);
        plus.lineTo(a, -a);
        plus.lineTo(b, -a);
        plus.lineTo(b, a);
        plus.lineTo(a, a);


        empty = new Path();
    }


    /**
     * Generate a mesh to draw the background mesh of the board
     * @param tileHeight the height of a tile
     * @param tileWidth the width of a tile
     * @param boardHeight the number of rows in the board
     * @param boardWidth the number of column in the board
     * @return the mesh generated
     */
    public Path getMesh(int tileHeight, int tileWidth, int boardHeight, int boardWidth) {
        //test if new generation is required
        long hash = (tileHeight & 0xFFFF) << 48 +
                (tileWidth & 0xFFFF) << 32 +
                (boardHeight & 0xFFFF) << 16 +
                (boardWidth & 0xFFFF);

        if (mesh == null || this.meshHash != hash) {
            meshHash = hash;
            mesh = new Path();
            for (float j = 0; j < boardHeight / 1.5; j += 1.5) {

                mesh.moveTo(0.5f * tileWidth, (j + 1f) * tileHeight);
                mesh.lineTo(0, (0.75f + j) * tileHeight);
                for (int i = 0; i < boardWidth; i++) {
                    mesh.moveTo(i * tileWidth, (0.75f + j) * tileHeight);
                    mesh.lineTo(i * tileWidth, (0.25f + j) * tileHeight);
                    mesh.lineTo(tileWidth * (i + 0.5f), j * tileHeight);
                    mesh.lineTo(tileWidth * (i + 1), (0.25f + j) * tileHeight);
                    mesh.lineTo(tileWidth * (i + 1), (0.75f + j) * tileHeight);
                }

                for (int i = 0; i < boardWidth; i++) {
                    mesh.moveTo((i + 0.5f) * tileWidth, (1.5f + j) * tileHeight);
                    mesh.lineTo((i + 0.5f) * tileWidth, (1f + j) * tileHeight);
                    mesh.lineTo(tileWidth * (i + 1f), (0.75f + j) * tileHeight);
                    mesh.lineTo(tileWidth * (i + 1.5f), (1f + j) * tileHeight);
                    mesh.lineTo(tileWidth * (i + 1.5f), (1.5f + j) * tileHeight);
                }
                mesh.lineTo(tileWidth * (boardWidth), (1.75f + j) * tileHeight);
            }

            for (int i = 0; i < boardWidth; i++) {
                mesh.moveTo((i + 0.5f) * tileWidth, (boardHeight * 0.75f) * tileHeight);
                mesh.lineTo((i + 1f) * tileWidth, (0.25f + boardHeight * 0.75f) * tileHeight);
                mesh.lineTo(tileWidth * (i + 1.5f), (boardHeight * 0.75f) * tileHeight);
            }
        }
        return mesh;
    }


    public Path getHexagon() {
        return hexagon;
    }

    public Path getLevelDecoration(int level, int kind) {
        if (level < (kind + 1)) {
            return empty;
        }
        switch (kind) {
            case 1:
                if (level - kind - 1 >= vegetalLevel.length) {return empty;}
                return vegetalLevel[level - kind - 1];
            case 2:
                if (level - kind - 1 >= waterLevel.length) {return empty;}
                return waterLevel[level - kind - 1];
            case 3:
                if (level - kind - 1 >= fireLevel.length) {return empty;}
                return fireLevel[level - kind - 1];
            case 4:
                if (level - kind - 1 >= lifeLevel.length) {return empty;}
                return lifeLevel[level - kind - 1];
            default:
                return empty;
        }
    }

    public Path getPlus() {
        return plus;
    }
}
