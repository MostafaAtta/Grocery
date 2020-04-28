package thegroceryshop.com.custom.loader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import thegroceryshop.com.R;

public class ProgressCircle1 extends View {

    private static final float ROTATE_BY = 30f;
    private static final long  DELAY     = 83;

    private float    mRotation;
    private Drawable mDrawable;
    private Updater  mUpdater;

    /**
     * {@inheritDoc}
     */
    public ProgressCircle1(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Initialise the view.
     *
     * @param context context.
     * @param attrs   layout attributes
     */
    private void init(Context context, AttributeSet attrs) {
        // setup drawable
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.img_loader_circle1);
        assert drawable != null;
        mDrawable = drawable.mutate();

        // gray color by default
        mDrawable.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);

        // animation updater
        mUpdater = new Updater();

        // apply attribute values, if provided
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressCircle);
            mDrawable.setColorFilter(ta.getColor(R.styleable.ProgressCircle_loader_circle_color, Color.DKGRAY), PorterDuff.Mode.SRC_IN);
            ta.recycle();
        }
    }

    /**
     * {@inheritDoc}
     */
    public ProgressCircle1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * {@inheritDoc}
     */
    public ProgressCircle1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Set color of progress circle.
     *
     * @param color color of progress circle.
     */
    public void setCircleColor(int color) {
        mDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // drawable always fits view size with padding
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int width = mDrawable.getIntrinsicWidth() - getPaddingRight() - getPaddingLeft();
        int height = mDrawable.getIntrinsicWidth() - getPaddingTop() - getPaddingBottom();
        mDrawable.setBounds(left, top, Math.max(0, width), Math.max(0, height));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        // rotate canvas at view center
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        canvas.rotate(mRotation);
        canvas.translate(-getWidth() / 2f, -getHeight() / 2f);

        // draw drawable
        mDrawable.draw(canvas);

        canvas.restore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // start draw updates
        postDelayed(mUpdater, DELAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // stop draw updates
        removeCallbacks(mUpdater);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // if warp content on any side, view is as big as the progress drawable, other wise as specified by parent
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT
                || getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Runnable to update progress animation.
     */
    private final class Updater implements Runnable {

        @Override
        public void run() {

            // update rotation
            mRotation += ROTATE_BY;
            if (mRotation > 360f) {
                mRotation = mRotation - 360f;
            }

            // schedule view for redraw
            ProgressCircle1.this.postInvalidate();

            // schedule next update
            ProgressCircle1.this.postDelayed(this, DELAY);
        }
    }
}