package gabygaby.hexatile.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.CycleInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gabygaby.hexatile.R;
import gabygaby.hexatile.game.Board;
import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.game.TileGenerator;

/**
 * The view shows a Board instance, and enable the user to play with it i.e. select tiles
 */
public class BoardView extends ViewGroup implements Board.BoardEventListener {

    public static final float COS = 0.866025f;
    public static final float SIN = 0.5f;
    private int meshColor = Color.GRAY;
    private int decoColor = Color.WHITE;

    private Board board;
    private TileGenerator generator;

    private GestureDetector gestureDetector;
    private List<Path> mesh;
    private Map<PointF, Tile> centers;
    private int additionalPadding;
    private Paint meshPaint;

    private int tileHeight, tileWidth = 0;
    private CollapseAnimator collapseAnimator;

    private boolean moving = false;
    private boolean blockMoving;

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
        decoColor = a.getColor(
                R.styleable.BoardView_meshColor,
                meshColor);
        a.recycle();
        setWillNotDraw(false);

        collapseAnimator = new CollapseAnimator();
        centers = new HashMap<>();
        mesh = new ArrayList<>();

        Resources r = getResources();
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, r.getDisplayMetrics());
        meshPaint = new Paint();
        meshPaint.setStrokeWidth(strokeWidth);
        meshPaint.setStyle(Paint.Style.STROKE);
        meshPaint.setColor(meshColor);

        gestureDetector = new GestureDetector(BoardView.this.getContext(), new GestureListener());
        gestureDetector.setIsLongpressEnabled(true);

        // In edit mode it'animatorSet nice to have some demo data, so add that here.
        if (this.isInEditMode()) {
            setBoard(new Board(5, 6));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpret this event
        boolean result = gestureDetector.onTouchEvent(event);

        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                result = true;
                moving = false;
                blockMoving = false;//reset the block when a new gesture begin
            }

        }
        return result;
    }

    /**
     * Select the tile at coordinate (x, y)
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param tap true if tapping, false if hovering
     */
    private void selectTile(float x, float y, boolean tap) {
        Tile selected = findTileAt(x, y);
        if (selected != null) {
            if (selected.isFree()) {
                int value = generator.consume();
                board.fill(selected, value);
                // if the available tile in the generator is not the same than the last tile added,
                // block selection on move
                if (moving && generator.peekFutures().get(0) != value) {
                    blockMoving = true;
                }
            } else if (tap) {
                //animate group only on tapping
                Set<Tile> group = selected.findGroup();
                AnimatorSet groupAnim = new AnimatorSet();
                AnimatorSet.Builder builder = null;
                for (Tile t : group) {
                    TileView view = (TileView) getChildAt(t.getIndex());
                    ObjectAnimator a = ObjectAnimator.ofFloat(view, "rotation", 0, 15); //NON-NLS
                    if (builder == null) {
                        builder = groupAnim.play(a);
                    } else {
                        builder.with(a);
                    }
                }
                groupAnim.setDuration(800);
                groupAnim.setInterpolator(new CycleInterpolator(2));
                groupAnim.start();
            }
        }
    }

    /**
     * Return the tile at the coordinate, or null if there is no tile there.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the tile if found, or null
     */
    private Tile findTileAt(float x, float y) {
        Tile selected = null;
        for (Map.Entry<PointF, Tile> center : centers.entrySet()) {
            double d = Math.pow(x - center.getKey().x, 2) + Math.pow(y - center.getKey().y, 2);
            if (d < Math.pow(tileWidth / 2, 2)) {
                selected = center.getValue();
                break;
            }
        }
        return selected;
    }

    /**
     * Activate the tile at coordinate
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void activateTile(float x, float y) {
        Tile selected = findTileAt(x, y);
        if (selected != null) {
            //mutate the tile if possible
            if (selected.isMutable()) {
                selected.mutate();
                TileView view = (TileView) getChildAt(selected.getIndex());
                view.syncDrawnLevel();
                view.invalidate();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingStart();
        int paddingTop = getPaddingTop();
        int k;
        for (int i = 0; i < board.getHeight(); i++) {
            int offset = +(i % 2 == 1 ? 1 : 0);
            for (int j = 0; j < board.getWidth(); j++) {
                k = i * board.getWidth() + j;
                TileView v = (TileView) getChildAt(k);
                float tx = (j + offset / 2f) * tileWidth;
                float ty = (i * 0.75f) * tileHeight;

                v.layout(Math.round(tx + paddingLeft + additionalPadding), Math.round(ty + paddingTop), Math.round(tx + tileWidth + paddingLeft), Math.round(ty + tileHeight + paddingTop));
                centers.put(new PointF(tx + paddingLeft + tileWidth / 2, ty + paddingTop + tileHeight / 2), v.getTile());

            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //case where the width is the limiting dimention
        tileWidth = (int) Math.floor(width / (board.getWidth() + 0.5f)) - 1;
        additionalPadding = Math.round(((width) - (int) (tileWidth * (board.getWidth() + 0.5))) * 0.5f);
        tileHeight = Math.round(tileWidth / COS);
        setMeasuredDimension(width, (int) Math.round((board.getHeight() - (board.getHeight() - 1) * 0.25) * tileHeight));

        int child_height = MeasureSpec.makeMeasureSpec(tileHeight, MeasureSpec.EXACTLY);
        int child_width = MeasureSpec.makeMeasureSpec(tileWidth, MeasureSpec.EXACTLY);


        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(child_width, child_height);
        }
        Matrix tr = new Matrix();
        tr.setTranslate(getPaddingLeft() + additionalPadding / 2, getPaddingTop());
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
            child.setDecoColor(decoColor);
            child.setTile(t);
            this.addView(child, i++);
        }
        board.addListener(this);
    }


    public void setGenerator(TileGenerator generator) {
        this.generator = generator;
    }


    @Override
    public void onTileAdded(Tile newTile, boolean collapsing, final int origLevel) {
        //update selected tile (simple filling)
        final TileView view = (TileView) getChildAt(newTile.getIndex());
        ObjectAnimator flipInAnim = ObjectAnimator.ofFloat(view, "flip", 0, 1); //NON-NLS
        flipInAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
        flipInAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setDrawnLevel(origLevel);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        flipInAnim.setDuration(100);
        flipInAnim.setInterpolator(new AccelerateInterpolator(0.9f));
        if (collapsing) {
            collapseAnimator.reset(flipInAnim);
        } else {
            flipInAnim.start();
        }
    }


    @Override
    public void onGroupCollapsed(Iterable<Tile> group, Tile promoted) {
        //block hover selection
        if (moving) {
            blockMoving = true;
        }
        final TileView promotedView = (TileView) getChildAt(promoted.getIndex());
        collapseAnimator.setTarget(promotedView.getLeft(), promotedView.getTop());
        collapseAnimator.newGroup();

        for (Tile t : group) {
            final TileView view = (TileView) getChildAt(t.getIndex());
            collapseAnimator.addTranslation(view, promoted.getLevel());
        }

        final TileView view = (TileView) getChildAt(promoted.getIndex());
        collapseAnimator.addPromotion(view, promoted.getLevel());

    }

    @Override
    public void onCascadeFinished() {
        collapseAnimator.start();
    }

    @Override
    public void onGameOver() {

    }

    @Override
    public void onTileMutated(Tile selected, boolean collapsing) {
        //TODO
    }

    /**
     * Invalidate all the child tiles. Usefull when setting the board
     */
    public void invalidateAll() {
        for (int i = 0; i < getChildCount(); i++) {
            TileView child = (TileView) getChildAt(i);
            child.syncDrawnLevel();
            child.invalidate();
        }
    }


    /**
     * Extends {@link GestureDetector.SimpleOnGestureListener} to provide custom gesture
     * processing.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            selectTile(e.getX(), e.getY(), true);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            moving = true;
            if (!blockMoving) {
                selectTile(e2.getX(), e2.getY(), false);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            activateTile(e.getX(), e.getY());
        }
    }
}
