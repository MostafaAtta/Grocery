package thegroceryshop.com.custom;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

public class CustomGridLayoutManager extends GridLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 100f;
    private Context mContext;
 
    public CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        mContext = context;
    }
 
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {
 
        LinearSmoothScroller smoothScroller =
            new LinearSmoothScroller(mContext) {
 
            //This controls the direction in which smoothScroll looks
            //for your view
            @Override
            public PointF computeScrollVectorForPosition
            (int targetPosition) {
                return CustomGridLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
            }
 
            //This returns the milliseconds it takes to 
            //scroll one pixel.
            @Override
            protected float calculateSpeedPerPixel
                (DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH/displayMetrics.densityDpi;
            }
        };
 
    smoothScroller.setTargetPosition(position);
    startSmoothScroll(smoothScroller);
    }
}