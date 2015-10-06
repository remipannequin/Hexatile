package gabygaby.hexatile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;

/**
 * The view shows a Board instance, and enable the user to play with it i.e. select tiles
 */
public class BoardView extends View {

    public static final float COS = 0.866025f;
    public static final float SIN = 0.5f;
    private int meshColor = Color.RED; // TODO: use a default from R.color...
    private int decoColor = Color.WHITE;
    private float tileSize = 0; // TODO: use a default from R.dimen...
    private Paint meshPaint;
    private Paint decoPaint;
    private Paint[] tilePaint = new Paint[10];
    private Paint textPaint;

    private Board board;

    private GestureDetector gestureDetector;
    private Path hexa, drawing;
    private Path[] levelPath = new Path[8];
    private Matrix trans_matrix, rot_matrix;
    private Map<PointF, Tile> centers;



    public BoardView(Context context) {
        super(context);
        init(null, 0);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BoardView, defStyle, 0);


        meshColor = a.getColor(
                R.styleable.BoardView_meshColor,
                meshColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        tileSize = a.getDimension(
                R.styleable.BoardView_tileSize,
                tileSize);

        a.recycle();

        centers = new HashMap<>();

        meshPaint = new Paint();
        meshPaint.setStrokeWidth(4);
        meshPaint.setStyle(Paint.Style.STROKE);

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

        gestureDetector = new GestureDetector(BoardView.this.getContext(), new GestureListener());
        gestureDetector.setIsLongpressEnabled(true);

        // In edit mode it's nice to have some demo data, so add that here.
        if (this.isInEditMode()) {
            board = new Board(6,8);
        }
        trans_matrix = new Matrix();
        drawing = new Path();
    }

    private void invalidateTextPaintAndMeasurements() {

        hexa = new Path();
        hexa.moveTo(0, 1);
        hexa.lineTo(COS, SIN);
        hexa.lineTo(COS, -SIN);
        hexa.lineTo(0, -1);
        hexa.lineTo(-COS, -SIN);
        hexa.lineTo(-COS, SIN);
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

        Matrix scale_matrix = new Matrix();
        //float sx = contentWidth / ((board.getWidth() * 2 + 1) * cos);
        //float sy = contentHeight / ((board.getHeight() * 3 + 1) / 2);
        scale_matrix.setScale(tileSize, tileSize);
        hexa.transform(scale_matrix);
        for (int i = 0; i < 5; i++) {
            levelPath[i].transform(scale_matrix);
        }

        meshPaint.setColor(meshColor);

        rot_matrix =  new Matrix();

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpret this event
        boolean result = gestureDetector.onTouchEvent(event);

        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {


                result = true;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

            }
        }




        return result;
    }

    private void selectTile(float x, float y) {
        Tile selected = null;
        for (Map.Entry<PointF, Tile> center: centers.entrySet()) {
            double d = Math.pow(x - center.getKey().x, 2) + Math.pow(y- center.getKey().y, 2);
            if (d < Math.pow(COS*tileSize,2)) {
                selected = center.getValue();
                break;
            }
        }
        if (selected != null) {
            if (selected.isFree()) {
                selected.fill();
                board.compute(selected);
            }
        }
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingStart();
        int paddingTop = getPaddingTop();
        //int paddingRight = getPaddingEnd();
        //int paddingBottom = getPaddingBottom();

        if (board != null) {
            for (int i = 0; i < board.getHeight(); i++) {
                int offset = + (i%2==1?1:0);
                for (int j = 0; j < board.getWidth(); j++) {
                    Tile current = board.getTile(i,j);
                    drawing.reset();
                    trans_matrix.reset();
                    float tx = (j * 2 + 1 + offset) * COS * tileSize;
                    float ty = (i * 1.5f + 1) * tileSize;
                    trans_matrix.setTranslate(tx+paddingLeft, ty+paddingTop);
                    centers.put(new PointF(tx + paddingLeft, ty+paddingTop), current);
                    hexa.transform(trans_matrix, drawing);
                    canvas.drawPath(drawing, meshPaint);

                    int level = current.getLevel();
                    if (level > 0) {
                        canvas.drawPath(drawing, tilePaint[level]);
                        canvas.drawText(String.format("%d",level), tx + paddingLeft, ty+paddingTop, textPaint);
                    }

                    if (level >= 2 && levelPath[level - 2] != null) {
                        drawing.reset();
                        rot_matrix.reset();
                        for (int k = 0; k < 6; k++) {
                            rot_matrix.setRotate(60*k);
                            levelPath[level - 2].transform(rot_matrix, drawing);
                            drawing.transform(trans_matrix);
                            canvas.drawPath(drawing, decoPaint);//TODO: add decoPaint
                        }
                    }

                }
            }
        }
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return this.board;
    }


    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getMeshColor() {
        return meshColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param color The example color attribute value to use.
     */
    public void setMeshColor(int color) {
        meshColor = color;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getTileSize() {
        return tileSize;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param size The example dimension attribute value to use.
     */
    public void setTileSize(float size) {
        tileSize = size;
        invalidateTextPaintAndMeasurements();
    }



      /**
     * Extends {@link GestureDetector.SimpleOnGestureListener} to provide custom gesture
     * processing.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent e) {
            //TODO: turn on acceleration ?
            return true;
        }

          @Override
          public boolean onSingleTapUp(MotionEvent e) {
              selectTile(e.getX(), e.getY());
              return false;
          }
      }



}
