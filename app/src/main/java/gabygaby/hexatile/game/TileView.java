package gabygaby.hexatile.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import gabygaby.hexatile.BoardView;
import gabygaby.hexatile.R;

/**
 * This view shows a single Tile
 *
 * Created by remi on 06/10/15.
 */
public class TileView extends View {

    private Paint decoPaint;
    private Paint[] tilePaint = new Paint[10];
    private Paint textPaint;
    private int decoColor = Color.WHITE;
    private Tile tile;
    private Path drawing;
    private Path hexa;
    private Path[] levelPath;
    private Matrix rot_matrix;
    private Matrix scale_matrix;
    private boolean frozen;
    private float flip;
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


        a.recycle();


        decoPaint = new Paint();
        decoPaint.setStrokeWidth(4);
        decoPaint.setStyle(Paint.Style.STROKE);
        decoPaint.setColor(decoColor);

        int[] levelColors = getContext().getResources().getIntArray(R.array.teal_theme);
        for (int i = 0; i < 10; i++) {
            tilePaint[i] = new Paint();
            tilePaint[i].setStyle(Paint.Style.FILL);
            tilePaint[i].setColor(levelColors[i]);
        }

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        //textPaint.setColor();
        textPaint.setTextSize(10);

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

        levelPath = new Path[8];
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
        levelPath[4].moveTo(0.2f, 0.4f);
        levelPath[4].lineTo(0, 0.5f);
        levelPath[4].lineTo(0, 0.7f);
        levelPath[4].lineTo(0.1f, 0.7f);
        levelPath[4].lineTo(0, 0.9f);
        levelPath[4].lineTo(-0.1f, 0.7f);
        levelPath[4].lineTo(0, 0.7f);
        levelPath[4].lineTo(0, 0.5f);
        levelPath[4].moveTo(-0.2f, 0.4f);

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



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);





        if (tile != null) {
            if (! frozen) {
                drawnLevel = tile.getLevel();
            }
            drawing.reset();
            hexa.transform(flip_matrix, drawing);
            //canvas.drawPath(drawing, meshPaint);

            if (drawnLevel > 0) {
                drawing.transform(scale_matrix);
                canvas.drawPath(drawing, tilePaint[drawnLevel]);
                canvas.drawText(String.format("%d", drawnLevel), 0, 0, textPaint);
            }

            if (drawnLevel >= 2 && levelPath[drawnLevel - 2] != null) {
                drawing.reset();
                rot_matrix.reset();
                for (int k = 0; k < 6; k++) {
                    rot_matrix.setRotate(60 * k);
                    levelPath[drawnLevel - 2].transform(rot_matrix, drawing);
                    drawing.transform(flip_matrix);
                    drawing.transform(scale_matrix);

                    canvas.drawPath(drawing, decoPaint);//TODO: add decoPaint
                }
            }

        }
    }

    public void setAlpha(int value) {
        for (Paint p : tilePaint) {
            p.setAlpha(value);
        }


    }

    /**
     * Achieve a "coin flipping" effect
     * @param value from 1.0 to -1.0
     */
    public void setFlip(float value) {
        flip_matrix.setScale(Math.abs(value), 1);
        if (value < 0) {
            //freeze drawnLevel from tile
            frozen = true;
        } else {
            frozen = false;
        }
    }




    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }
}
