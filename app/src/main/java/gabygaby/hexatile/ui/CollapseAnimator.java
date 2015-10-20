package gabygaby.hexatile.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateInterpolator;

/**
 * This class facilitate the animation of a collapse event. This event is described as a cascade of
 * subevents when a group of tiles collapse into a promoted tile.
 */
public class CollapseAnimator {


    public static final int PROMOTION_DURATION = 50;
    public static final int COLLAPSE_DURATION = 200;
    private AnimatorSet set;
    private int targetY;
    private int targetX;
    private Animator lastInGroup;
    /**
     * True if there was no group previously in the animation
     */
    private boolean firstGroup;
    /**
     * True if two group should be chained toghether
     */
    private boolean groupBegin;

    /**
     * Reset the animator set
     */
    public void reset() {
        set = new AnimatorSet();
        firstGroup = true;
    }

    /**
     * Reset the animator set with a starting animation
     * @param a the animator to play first
     */
    public void reset(Animator a) {
        set = new AnimatorSet();
        firstGroup = false;
        lastInGroup = a;
    }


    /**
     * Set the coordinates of the target tile (being promoted)
     *
     * @param left the coordinate of the left of the promoted tile
     * @param top the coordinate of the top of the promoted tile
     */
    public void setTarget(int left, int top) {
        this.targetX = left;
        this.targetY = top;
    }

    /**
     * Begin the definition of a group
     */
    public void newGroup() {
        if (!firstGroup) {
            groupBegin = true;
            firstGroup = false;
        }
    }

    /**
     * Add a Translation animation into a group
     *
     * @param view the tile to translate to the target
     */
    public void addTranslation(final TileView view, int level) {
        float deltaX = targetX - view.getX();
        float deltaY = targetY - view.getY();
        ObjectAnimator a1 = ObjectAnimator.ofFloat(view, "translationX", 0, deltaX);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(view, "translationY", 0, deltaY);
        a1.setDuration((long)(COLLAPSE_DURATION * (1 + level*0.5)));
        a2.setDuration((long)(COLLAPSE_DURATION * (1 + level*0.5)));
        a1.setInterpolator(new AccelerateInterpolator());
        a2.setInterpolator(new AccelerateInterpolator());

        a1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.syncDrawnLevel();
                view.invalidate();
                view.setTranslationX(0);
                view.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.play(a1).with(a2);
        if (!firstGroup) {
            if (groupBegin) {
                set.play(lastInGroup).before(a1);
                groupBegin = false;
            } else {
                set.play(lastInGroup).with(a1);
            }
        } else {
            firstGroup = false;
        }
        lastInGroup = a2;
    }

    /**
     * add a promotion animation into the group
     *
     * @param view the tile to promote
     * @param new_level the new level of the tile
     */
    public void addPromotion(final TileView view, final int new_level) {

        ObjectAnimator fadeout = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        fadeout.setDuration(PROMOTION_DURATION);
        fadeout.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
        fadeout.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.incrDrawnLevel();
                view.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ObjectAnimator fadein = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        fadein.setDuration(PROMOTION_DURATION);
        fadein.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
        fadein.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.invalidate();
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


        set.play(lastInGroup).before(fadeout);
        set.play(fadeout).before(fadein);
        lastInGroup = fadeout;
    }

    /**
     * Play all group event sequentially
     */
    public void start() {
        set.start();
    }
}
