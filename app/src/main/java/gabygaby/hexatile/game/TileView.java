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
    private Paint meshPaint;
    private int meshColor = Color.BLACK;
    private Matrix translate_matrix;
    private Matrix scale_matrix;

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


        meshPaint = new Paint();
        meshPaint.setStrokeWidth(4);
        meshPaint.setStyle(Paint.Style.STROKE);
        meshPaint.setColor(meshColor);

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
        scale_matrix.postTranslate(width/2f, height/2f);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if (tile != null) {
            drawing.reset();
            hexa.transform(scale_matrix, drawing);
            canvas.drawPath(drawing, meshPaint);

            int level = tile.getLevel();
            if (level > 0) {
                canvas.drawPath(drawing, tilePaint[level]);
                canvas.drawText(String.format("%d", level), 0, 0, textPaint);
            }

            if (level >= 2 && levelPath[level - 2] != null) {
                drawing.reset();
                rot_matrix.reset();
                for (int k = 0; k < 6; k++) {
                    rot_matrix.setRotate(60 * k);
                    levelPath[level - 2].transform(rot_matrix, drawing);
                    drawing.transform(scale_matrix);
                    canvas.drawPath(drawing, decoPaint);//TODO: add decoPaint
                }
            }

        }
    }


    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }
}
