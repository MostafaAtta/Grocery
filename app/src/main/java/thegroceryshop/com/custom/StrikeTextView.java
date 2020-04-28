package thegroceryshop.com.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import thegroceryshop.com.R;

public class StrikeTextView extends AppCompatTextView {
    private int dividerColor;
    private Paint paint;

    public StrikeTextView(Context context) {
        super(context);
        init(context);
    }

    public StrikeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StrikeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources resources = context.getResources();
        //replace with your color
        dividerColor = ContextCompat.getColor(getContext(), R.color.purple_bg);

        paint = new Paint();
        paint.setColor(dividerColor);
        //replace with your desired width
        paint.setStrokeWidth(resources.getDimension(R.dimen.dp2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, getHeight(), getWidth(), 0, paint);
    }
}