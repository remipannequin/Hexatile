package gabygaby.hexatile.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import gabygaby.hexatile.R;
import gabygaby.hexatile.game.Tile;

/**
 * This view shows a single Tile
 *
 * Created by remi on 06/10/15.
 */
public class TileView extends View {

    private Paint decoPaint;
    private Paint[][] tilePaint = new Paint[4][8];
    private int decoColor = Color.WHITE;
    private Tile tile;
    private Path drawing;
    private Path hexa;
    private Path[] levelPath;
    private Matrix rot_matrix;
    private Matrix scale_matrix;
    private Matrix flip_matrix;
    private int drawnLevel;

    public TileView(Context context) {
        super(context);
        init(null, 0);
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BoardView, defStyle, 0);
        decoColor = a.getColor(
                R.styleable.TileView_decoColor,
                decoColor);

        a.recycle();


        if (this.isInEditMode()) {
            Tile t = new Tile(6,0);
            setTile(t);
        }

        decoPaint = new Paint();
        decoPaint.setStrokeWidth(4);
        decoPaint.setStyle(Paint.Style.STROKE);
        decoPaint.setColor(decoColor);

        int[] color_arrays = new int[] {
                R.array.vegetal_colors,
                R.array.water_colors,
                R.array.fire_colors,
                R.array.live_colors};
        for (int k : color_arrays) {
            int[] levelColors = getContext().getResources().getIntArray(k);
            for (int i = 0; i < 8; i++) {
                tilePaint[k][i]=new Paint();
                tilePaint[k][i].setStyle(Paint.Style.FILL);
                tilePaint[k][i].setColor(levelColors[i]);
            }
        }
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        drawing = new Path();
    }

    private void invalidateTextPaintAndMeasurements() {

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

        levelPath = new Path[6];
        levelPath[0] = new Path();
        levelPath[0].moveTo(0, 0.9f);
        levelPath[0].lineTo(0, 0.7f);

        levelPath[1] = new Path();
        levelPath[1].moveTo(-0.1f, 0.7f);
        levelPath[1].lineTo(0, 0.9f);
        levelPath[1].lineTo(0.1f, 0.7f);

        levelPath[2] = new Path();
        levelPath[2].moveTo(-0.1f, 0.7f);
        levelPath[2].lineTo(0, 0.9f);
        levelPath[2].lineTo(0.1f, 0.7f);
        levelPath[2].close();

        levelPath[3] = new Path();
        levelPath[3].moveTo(0, 0.5f);
        levelPath[3].lineTo(0, 0.7f);
        levelPath[3].lineTo(0.1f, 0.7f);
        levelPath[3].lineTo(0, 0.9f);
        levelPath[3].lineTo(-0.1f, 0.7f);
        levelPath[3].lineTo(0, 0.7f);

        levelPath[4] = new Path();
        levelPath[4].moveTo(BoardView.COS/4f, 0.375f);
        levelPath[4].lineTo(0, 0.5f);
        levelPath[4].lineTo(0, 0.7f);
        levelPath[4].lineTo(0.1f, 0.7f);
        levelPath[4].lineTo(0, 0.9f);
        levelPath[4].lineTo(-0.1f, 0.7f);
        levelPath[4].lineTo(0, 0.7f);
        levelPath[4].lineTo(0, 0.5f);
        levelPath[4].lineTo(-BoardView.COS/4f, 0.375f);

        levelPath[5] = new Path();
        levelPath[5].moveTo(BoardView.COS/4f, 0.125f);
        levelPath[5].lineTo(0, 0.5f);
        levelPath[5].lineTo(0, 0.7f);
        levelPath[5].lineTo(0.1f, 0.7f);
        levelPath[5].lineTo(0, 0.9f);
        levelPath[5].lineTo(-0.1f, 0.7f);
        levelPath[5].lineTo(0, 0.7f);
        levelPath[5].lineTo(0, 0.5f);
        levelPath[5].lineTo(-BoardView.COS/4f, 0.125f);

        scale_matrix = new Matrix();
        rot_matrix = new Matrix();
        flip_matrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //int paddingLeft = getPaddingStart();
        //int paddingTop = getPaddingTop();
        //int paddingRight = getPaddingEnd();
        //int paddingBottom = getPaddingBottom();
        int width= MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        scale_matrix.setScale((float)width/(2f*BoardView.COS), (float)height/2f);
        scale_matrix.postTranslate(width / 2f, height / 2f);
        setPivotX(width/2f);
        setPivotY(height/2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tile != null) {
            drawing.reset();
            hexa.transform(flip_matrix, drawing);
            //canvas.drawPath(drawing, meshPaint);

            if (drawnLevel > 0) {
                int kind = tile.getKind();
                drawing.transform(scale_matrix);
                canvas.drawPath(drawing, tilePaint[kind][drawnLevel]);
            }

            if (drawnLevel >= 2 && levelPath[drawnLevel - 2] != null) {
                drawing.reset();
                rot_matrix.reset();
                for (int k = 0; k < 6; k++) {
                    rot_matrix.setRotate(60 * k);
                    levelPath[drawnLevel - 2].transform(rot_matrix, drawing);
                    drawing.transform(flip_matrix);
                    drawing.transform(scale_matrix);
                    canvas.drawPath(drawing, decoPaint);
                }
            }
        }
    }

    /**
     * Achieve a "coin flipping" effect
     * @param value from 1.0 to -1.0
     */
    public void setFlip(float value) {
        flip_matrix.setScale(Math.abs(value), 1);
        //freeze drawnLevel from tile
        //frozen = value < 0;
    }

    public void setDecoColor(int decoColor) {
        this.decoColor = decoColor;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public void incrDrawnLevel() {
        drawnLevel++;
    }

    public void syncDrawnLevel() {
        if (tile != null) {
            drawnLevel = tile.getLevel();
        }
    }

    public void setDrawnLevel(int level) {
        if (tile != null) {
            drawnLevel = level;
        }
    }

}
