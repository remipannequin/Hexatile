package gabygaby.hexatile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import gabygaby.hexatile.R;
import gabygaby.hexatile.game.Tile;
import gabygaby.hexatile.game.TileGenerator;

/**
 * This view enable to view and control the tile generator
 */
public class TileGeneratorView extends ViewGroup implements TileGenerator.GeneratorListener {

    private int meshColor = Color.GRAY;

    private int tileHeight, tileWidth = 0;
    private Paint meshPaint;
    private TileGenerator generator;
    private GestureDetector gestureDetector;
    private Tile[] tiles;
    private ObjectAnimator futureAnim;
    private ObjectAnimator stashAnim;


    public TileGeneratorView(Context context) {
        super(context);
        init(null, 0);
    }


    public TileGeneratorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }


    public TileGeneratorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TileGeneratorView, defStyle, 0);


        meshColor = a.getColor(
                R.styleable.BoardView_meshColor,
                meshColor);
        a.recycle();
        setWillNotDraw(false);

        meshPaint = new Paint();
        meshPaint.setStrokeWidth(5);
        meshPaint.setStyle(Paint.Style.FILL);
        meshPaint.setColor(meshColor);

        gestureDetector = new GestureDetector(TileGeneratorView.this.getContext(), new GestureListener());
        gestureDetector.setIsLongpressEnabled(true);

        // In edit mode it'animatorSet nice to have some demo data, so add that here.
        if (this.isInEditMode()) {
            TileGenerator g = new TileGenerator(4);
            g.stash();
            setGenerator(g);
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
        }
        return result;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        float x = 0.66f * getWidth();
        float t = 0.25f * tileHeight;
        for (int j = 0; j < generator.getSize(); j++) {
            float h = x / (0.66f * getWidth()) * tileHeight;
            float w = h * BoardView.COS;
            float p = x - 1.5f * w;
            TileView v = (TileView) getChildAt(j);

            int child_height = MeasureSpec.makeMeasureSpec(Math.round(h), MeasureSpec.EXACTLY);
            int child_width = MeasureSpec.makeMeasureSpec(Math.round(w), MeasureSpec.EXACTLY);

            v.measure(child_width, child_height);
            v.layout(Math.round(p), Math.round(t), Math.round(p + w), Math.round(t + h));
            x = p;
        }


        TileView v = (TileView) getChildAt(generator.getSize());
        v.measure(tileWidth, tileHeight);
        x = 0.875f * getWidth() - 0.5f * tileWidth;
        v.layout(Math.round(x), Math.round(t), Math.round(x + tileWidth), Math.round(t + tileHeight));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width / 4;

        tileHeight = (Math.round(height / 1.5f));
        tileWidth = Math.round(tileHeight * BoardView.COS);
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(0.875f * getWidth(), 0.5f * getHeight(), 0.625f * tileHeight, meshPaint);
    }


    public void setGenerator(TileGenerator generator) {
        removeAllViews();
        this.generator = generator;
        tiles = new Tile[generator.getSize() + 1];
        int i = 0;
        for (int v : generator.peekFutures()) {
            Tile t = new Tile(i, v);
            TileView child = new TileView(getContext());
            child.setTile(t);
            child.syncDrawnLevel();
            this.addView(child, i);
            tiles[i] = t;
            i++;
        }
        final TileView last = (TileView) getChildAt(0);
        futureAnim = ObjectAnimator.ofFloat(getChildAt(0), "flip", 0, 1); //NON-NLS
        futureAnim.setDuration(500);
        futureAnim.setRepeatMode(Animation.REVERSE);
        futureAnim.setRepeatCount(Animation.INFINITE);
        futureAnim.setInterpolator(new DecelerateInterpolator());
        futureAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                last.invalidate();
            }
        });
        futureAnim.addPauseListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationPause(Animator animation) {
                last.setFlip(1);
                last.invalidate();
            }
        });
        futureAnim.start();

        int v = generator.peekStash();
        Tile t = new Tile(i, v);
        final TileView stashView = new TileView(getContext());
        stashView.setTile(t);
        stashView.syncDrawnLevel();
        this.addView(stashView, i);
        tiles[i] = t;
        generator.addListener(this);

        stashAnim = ObjectAnimator.ofFloat(stashView, "flip", 0, 1); //NON-NLS
        stashAnim.setDuration(500);
        stashAnim.setRepeatMode(Animation.REVERSE);
        stashAnim.setRepeatCount(Animation.INFINITE);
        stashAnim.setInterpolator(new DecelerateInterpolator());
        stashAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                stashView.invalidate();
            }
        });
        stashAnim.addPauseListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationPause(Animator animation) {
                stashView.setFlip(1);
                stashView.invalidate();
            }
        });
    }


    @Override
    public void onTileConsumed() {
        int i = 0;
        for (int v : generator.peekFutures()) {
            TileView child = (TileView) getChildAt(i);
            Tile t = tiles[i];
            t.setLevel(v);
            child.syncDrawnLevel();
            child.invalidate();
            i++;
        }
    }


    @Override
    public void onStashChanged() {
        int i = generator.getSize();
        TileView child = (TileView) getChildAt(i);
        Tile t = tiles[i];
        t.setLevel(generator.peekStash());
        child.syncDrawnLevel();
        child.invalidate();
    }

    @Override
    public void onSourceChanged(boolean fromReserve) {
        if (fromReserve) {
            //stop stash animation
            stashAnim.start();
            //start future animation
            futureAnim.pause();
        } else {
            //stop stash animation
            stashAnim.pause();
            //start future animation
            futureAnim.start();
        }
    }


    private void selectReserve(boolean b) {
        if (b) {
            if (generator.isStashPlaceFree()) {
                //send tile to stash
                //don't select stash
                generator.stash();
            } else {
                //select stash
                generator.selectStash(true);
            }
        } else {
            //select futures
            generator.selectStash(false);
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
            selectReserve(e.getX() > 0.75f * getWidth());
            return false;
        }
    }
}
