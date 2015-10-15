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


    private AnimatorSet set;
    private int targetY;
    private int targetX;
    private ObjectAnimator lastInGroup;
    /**
     * True if there was no group previously in the animation
     */
    private boolean firstGroup;
    /**
     * True if two group should be chained toghether
     */
    private boolean groupBegin;

    public void reset() {
        set = new AnimatorSet();
        firstGroup = true;
    }

    /**
     * Set the coordinates of the target tile (being promoted)
     * @param left
     * @param top
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
     * @param view
     */
    public void addTranslation(final TileView view) {
        float deltaX = targetX - view.getX();
        float deltaY = targetY - view.getY();
        ObjectAnimator a1 = ObjectAnimator.ofFloat(view, "translationX", 0, deltaX);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(view, "translationY", 0, deltaY);
        a1.setDuration(500);
        a2.setDuration(500);
        a1.setInterpolator(new AccelerateInterpolator());
        a2.setInterpolator(new AccelerateInterpolator());

        a1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.freeze();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.thaw();
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
     * @param view
     */
    public void addPromotion(final TileView view) {
        ObjectAnimator flipInAnim = ObjectAnimator.ofFloat(view, "flip", 0, 1);
        flipInAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
        flipInAnim.setDuration(250);
        flipInAnim.setInterpolator(new AccelerateInterpolator(0.9f));
        flipInAnim.start();


    }

    /**
     * Play all group event sequentially
     */
    public void start() {
        set.start();
    }
}
