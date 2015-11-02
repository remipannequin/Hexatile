package gabygaby.hexatile.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
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
    private final Paint[][] tilePaint = new Paint[4][8];
    private int decoColor = Color.WHITE;
    private Tile tile;
    private Path drawing;
    private Matrix rot_matrix;
    private Matrix scale_matrix;
    private Matrix flip_matrix;
    private int drawnLevel;
    private boolean mutable = false;
    private int drawnKind;


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

        Resources r = getResources();
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, r.getDisplayMetrics());
        strokeWidth = a.getDimension(
            R.styleable.TileView_strokeWidth,
                strokeWidth);

        a.recycle();

        decoPaint = new Paint();
        decoPaint.setStrokeWidth(strokeWidth);
        decoPaint.setStyle(Paint.Style.STROKE);
        decoPaint.setColor(decoColor);

        int[] color_arrays = new int[] {
                R.array.vegetal_colors,
                R.array.water_colors,
                R.array.fire_colors,
                R.array.live_colors};
        for (int k = 0; k < color_arrays.length; k++) {
            int[] levelColors = getContext().getResources().getIntArray(color_arrays[k]);
            for (int i = 0; i < 8; i++) {
                tilePaint[k][i] = new Paint();
                tilePaint[k][i].setStyle(Paint.Style.FILL);
                tilePaint[k][i].setColor(levelColors[i]);
            }
        }

        drawing = new Path();
        scale_matrix = new Matrix();
        rot_matrix = new Matrix();
        flip_matrix = new Matrix();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width= MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        scale_matrix.setScale((float)width/(2f*BoardView.COS), (float)height/2f);
        scale_matrix.postTranslate(width / 2f, height / 2f);
        setPivotX(width / 2f);
        setPivotY(height / 2f);
    }

    public void setPivotTopLeft() {
        setPivotX(0);
        setPivotY(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tile != null) {
            drawing.reset();
            Path hexa = TileDecorator.getInstance().getHexagon();
            hexa.transform(flip_matrix, drawing);

            if (drawnLevel >= 1 && drawnKind >= 1) {
                drawing.transform(scale_matrix);
                canvas.drawPath(drawing, tilePaint[drawnKind - 1][drawnLevel - 1]);
            }

            if (drawnLevel >= 2) {
                drawing.reset();
                rot_matrix.reset();
                for (int k = 0; k < 6; k++) {
                    rot_matrix.setRotate(60 * k);
                    Path levelPath = TileDecorator.getInstance().getLevelDecoration(drawnLevel, drawnKind);
                    levelPath.transform(rot_matrix, drawing);
                    drawing.transform(flip_matrix);
                    drawing.transform(scale_matrix);
                    canvas.drawPath(drawing, decoPaint);
                }
            }

            if (mutable) {
                drawing.reset();
                Path plusPath = TileDecorator.getInstance().getPlus();
                plusPath.transform(flip_matrix, drawing);
                drawing.transform(scale_matrix);
                canvas.drawPath(drawing, decoPaint);
            }
        }
    }

    /**
     * Achieve a "coin flipping" effect
     * @param value from 1.0 to -1.0
     */
    public void setFlip(float value) {
        flip_matrix.setScale(Math.abs(value), 1);
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
        if (tile != null) {
            mutable = tile.isMutable();
        }
    }

    public void syncDrawnLevel() {
        if (tile != null) {
            drawnLevel = tile.getLevel();
            mutable = tile.isMutable();
            drawnKind = tile.getKind();
        }
    }

    public void setDrawnLevel(int level) {
        if (tile != null) {
            drawnLevel = level;
        }
    }

}
