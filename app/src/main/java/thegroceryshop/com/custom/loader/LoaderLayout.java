package thegroceryshop.com.custom.loader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;


/**
 * Layout that extends frame layout. This is usable to wrap another content layout inside,
 * and display an empty text or progress circle, hiding the wrapped content layout.
 * Usable when content is loaded asynchronously and content error must be shown.
 */
public class LoaderLayout extends FrameLayout {
    private final Set<View> mExcludedViews = new HashSet<>();
    private ProgressCircle mProgressCircle;
    private TextView mTxvStatus;
    private boolean hideContentOnLoad = true;

    /**
     * {@inheritDoc}
     */
    public LoaderLayout(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Initialise this view.
     *
     * @param context context.
     */
    private void init(Context context, AttributeSet attrs) {
        int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;

        // add content status text view
        mTxvStatus = new TextView(context);

        LayoutParams textParams = new LayoutParams(wrapContent, wrapContent, Gravity.CENTER);
        addView(mTxvStatus, 0, textParams);
        mTxvStatus.setGravity(Gravity.CENTER);

        // add progress bar
        mProgressCircle = new ProgressCircle(context);
        LayoutParams progParams = new LayoutParams(wrapContent, wrapContent, Gravity.CENTER);
        addView(mProgressCircle, 0, progParams);

        // by default show content at start
        boolean showProgressAtStart = false;

        Typeface typeface = null;
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            typeface = ResourcesCompat.getFont(getContext(), R.font.montserrat_regular);
        }else{
            typeface = ResourcesCompat.getFont(getContext(), R.font.ge_ss_two_medium);
        }
        mTxvStatus.setTypeface(typeface);

        // apply styleable attributes, if set
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoaderLayout);

            if (ta.hasValue(R.styleable.LoaderLayout_loader_layout_default_text)) {
                mTxvStatus.setText(ta.getString(R.styleable.LoaderLayout_loader_layout_default_text));
            }

            if (ta.hasValue(R.styleable.LoaderLayout_loader_layout_text_color)) {
                mTxvStatus.setTextColor(ta.getColor(R.styleable.LoaderLayout_loader_layout_text_color, Color.BLACK));
            }

            if (ta.hasValue(R.styleable.LoaderLayout_loader_layout_text_size)) {
                mTxvStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        ta.getDimensionPixelSize(R.styleable.LoaderLayout_loader_layout_text_size, 0));
            }

            if (ta.hasValue(R.styleable.LoaderLayout_loader_layout_progress_color)) {
                mProgressCircle.setCircleColor(
                        ta.getColor(R.styleable.LoaderLayout_loader_layout_progress_color, Color.DKGRAY));
                mTxvStatus.setTextColor(
                        ta.getColor(R.styleable.LoaderLayout_loader_layout_progress_color, Color.DKGRAY));
            }

            if (ta.hasValue(R.styleable.LoaderLayout_loader_layout_show_progress_at_start)) {
                showProgressAtStart = ta
                        .getBoolean(R.styleable.LoaderLayout_loader_layout_show_progress_at_start, false);
            }

            ta.recycle();
        }

        // show progress on start if requested
        if (showProgressAtStart) {
            showProgress();
        }

    }

    /**
     * Display progress, hiding status text and wrapped content views.
     */
    public void showProgress() {
        mProgressCircle.setVisibility(VISIBLE);
        mTxvStatus.setVisibility(INVISIBLE);
        if (hideContentOnLoad) {
            setSubViewsVisibility(INVISIBLE);
        }
    }

    private void setSubViewsVisibility(int visibility) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View subView = getChildAt(i);
            if (subView != mTxvStatus && subView != mProgressCircle && !mExcludedViews.contains(subView)) {
                subView.setVisibility(visibility);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public LoaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * {@inheritDoc}
     */
    public LoaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Display wrapped content views, hiding status text and progress.
     */
    public void showContent() {
        mProgressCircle.setVisibility(INVISIBLE);
        mTxvStatus.setVisibility(INVISIBLE);
        setSubViewsVisibility(VISIBLE);
    }

    /**
     * Display status text, hiding progress and wrapped content views.
     */
    public void showStatusText() {
        mProgressCircle.setVisibility(INVISIBLE);
        mTxvStatus.setVisibility(VISIBLE);
        setSubViewsVisibility(INVISIBLE);
    }

    public void showStatusTextWithPadding() {
        mProgressCircle.setVisibility(INVISIBLE);
        mTxvStatus.setPadding(dp2px(8), 0, dp2px(8), 0);
        mTxvStatus.setVisibility(VISIBLE);
        setSubViewsVisibility(INVISIBLE);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public boolean isStatusTextShowing() {
        return mTxvStatus.getVisibility() == View.VISIBLE;
    }

    /**
     * Set a view inside this layout to be excluded or included in hiding to show progress or status text.
     *
     * @param v        view, must be a child view of this layout.
     * @param excluded true to exclude, false to include.
     */
    public void setViewAsExcluded(View v, boolean excluded) {

        if (v.getParent() != this) {
            throw new IllegalArgumentException("Given view is not a child of this loader layout.");
        }

        if (excluded) {
            mExcludedViews.add(v);
        } else {
            mExcludedViews.remove(v);
        }
    }

    /**
     * set the empty text view text size
     *
     * @param size size in pixel
     */
    public void setEmptyTextSize(float size) {
        if (mTxvStatus != null) {
            mTxvStatus.setTextSize(size);
        }
    }

    /**
     * set the empty text view text size
     *
     * @param size size in pixel
     */
    public void setEmptyTextSize(int unit, float size) {
        if (mTxvStatus != null) {
            mTxvStatus.setTextSize(unit, size);
        }
    }

    /**
     * set the string text to the view
     *
     * @param mText string fot the view to set
     */
    public void setStatuText(String mText) {
        if (mTxvStatus != null) {
            mTxvStatus.setText(mText);
        }
    }

    /**
     * Get status text view. used to set a custom content status. Or even add click listeners to retry
     * content loading.
     *
     * @return Status text view.
     */
    public TextView getStatusTextView() {
        return mTxvStatus;
    }

    /**
     * Get progress circle view. Used customise progress if need be.
     *
     * @return progress circle.
     */
    public ProgressCircle getProgressCircle() {
        return mProgressCircle;
    }

    public void setHideContentOnLoad(boolean hideContentOnLoad) {
        this.hideContentOnLoad = hideContentOnLoad;
    }

}
