package gabygaby.hexatile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.game.TileView;

/**
 * The view shows a Board instance, and enable the user to play with it i.e. select tiles
 */
public class BoardView extends ViewGroup {

    public static final float COS = 0.866025f;
    public static final float SIN = 0.5f;
    private int meshColor = Color.RED; // TODO: use a default from R.color...

    private int tileHeight, tileWidth = 0; // TODO: use a default from R.dimen...
    private Paint meshPaint;

    private Board board;

    private GestureDetector gestureDetector;
    private List<Path> mesh;
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


        a.recycle();
        setWillNotDraw(false);
        centers = new HashMap<>();
        mesh = new ArrayList<>();
        meshPaint = new Paint();
        meshPaint.setStrokeWidth(4);
        meshPaint.setStyle(Paint.Style.STROKE);

        gestureDetector = new GestureDetector(BoardView.this.getContext(), new GestureListener());
        gestureDetector.setIsLongpressEnabled(true);

        // In edit mode it's nice to have some demo data, so add that here.
        if (this.isInEditMode()) {
            setBoard(new Board(6, 8));
        }


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
        for (Map.Entry<PointF, Tile> center : centers.entrySet()) {
            double d = Math.pow(x - center.getKey().x, 2) + Math.pow(y - center.getKey().y, 2);
            if (d < Math.pow(tileWidth / 2, 2)) {
                selected = center.getValue();
                break;
            }
        }
        if (selected != null) {
            if (selected.isFree()) {
                selected.fill();

                while (board.isDirty()) {
                    Set<Tile> group = board.compute(selected);
                    group.add(selected);
                    for (Tile t : group) {
                        int index = t.getIndex();
                        getChildAt(index).invalidate();
                    }

                }
            }
        }


    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();

        int paddingLeft = getPaddingStart();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingEnd();
        int paddingBottom = getPaddingBottom();
        int k;
        for (int i = 0; i < board.getHeight(); i++) {
            int offset = +(i % 2 == 1 ? 1 : 0);
            for (int j = 0; j < board.getWidth(); j++) {
                k = i * board.getWidth() + j;
                TileView v = (TileView) getChildAt(k);
                float tx = (j + offset / 2f) * tileWidth;
                float ty = (i * 0.75f) * tileHeight;

                v.layout(Math.round(tx + paddingLeft), Math.round(ty + paddingTop), Math.round(tx + tileWidth + paddingLeft), Math.round(ty + tileHeight + paddingTop));
                centers.put(new PointF(tx + paddingLeft + tileWidth / 2, ty + paddingTop + tileHeight / 2), v.getTile());

            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        tileHeight = Math.round(Math.min(height / board.getHeight(), width / board.getWidth()));
        tileWidth = Math.round(tileHeight * COS);
        int child_height = MeasureSpec.makeMeasureSpec(tileHeight, MeasureSpec.EXACTLY);
        int child_width = MeasureSpec.makeMeasureSpec(tileWidth, MeasureSpec.EXACTLY);

        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(child_width, child_height);
        }
        Matrix tr = new Matrix();
        tr.setTranslate(getPaddingLeft(), getPaddingTop());
        mesh.clear();
        for (float j = 0; j < board.getHeight() / 1.5; j += 1.5) {
            Path p1 = new Path();
            p1.moveTo(0.5f * tileWidth, (j + 1f) * tileHeight);
            p1.lineTo(0, (0.75f + j) * tileHeight);
            for (int i = 0; i < board.getWidth(); i++) {
                p1.moveTo(i * tileWidth, (0.75f + j) * tileHeight);
                p1.lineTo(i * tileWidth, (0.25f + j) * tileHeight);
                p1.lineTo(tileWidth * (i + 0.5f), j * tileHeight);
                p1.lineTo(tileWidth * (i + 1), (0.25f + j) * tileHeight);
                p1.lineTo(tileWidth * (i + 1), (0.75f + j) * tileHeight);
            }
            p1.transform(tr);
            mesh.add(p1);

            Path p2 = new Path();
            for (int i = 0; i < board.getWidth(); i++) {
                p2.moveTo((i + 0.5f) * tileWidth, (1.5f + j) * tileHeight);
                p2.lineTo((i + 0.5f) * tileWidth, (1f + j) * tileHeight);
                p2.lineTo(tileWidth * (i + 1f), (0.75f + j) * tileHeight);
                p2.lineTo(tileWidth * (i + 1.5f), (1f + j) * tileHeight);
                p2.lineTo(tileWidth * (i + 1.5f), (1.5f + j) * tileHeight);
            }
            p2.lineTo(tileWidth * (board.getWidth()), (1.75f + j) * tileHeight);
            p2.transform(tr);
            mesh.add(p2);
        }

        Path p3 = new Path();
        for (int i = 0; i < board.getWidth(); i++) {
            p3.moveTo((i + 0.5f) * tileWidth, (board.getHeight() * 0.75f) * tileHeight);
            p3.lineTo((i + 1f) * tileWidth, (0.25f + board.getHeight() * 0.75f) * tileHeight);
            p3.lineTo(tileWidth * (i + 1.5f), (board.getHeight() * 0.75f) * tileHeight);
        }
        p3.transform(tr);
        mesh.add(p3);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path p : mesh) {
            canvas.drawPath(p, meshPaint);
        }
    }

    public void setBoard(Board board) {
        this.board = board;
        int i = 0;
        for (Tile t : board.getTiles()) {
            TileView child = new TileView(getContext());
            child.setTile(t);
            this.addView(child, i++);
        }


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
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getTileSize() {
        return tileHeight;
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
