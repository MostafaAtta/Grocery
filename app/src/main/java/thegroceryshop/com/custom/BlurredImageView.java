package thegroceryshop.com.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BlurredImageView extends ImageView {

    private static final int BLUR_RADIUS = 25;

    public BlurredImageView(Context context) {
        super(context);
    }

    public BlurredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlurredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageResource(int resId) {
        setBlurredImage(resId);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setBlurredImage(bm);
    }

    public void setBlurredImage(int resId) {
        setBlurredImage(BitmapFactory.decodeResource(getResources(), resId));
    }

    public void setBlurredImage(Bitmap bitmap) {
        Bitmap blurred = bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurred = blurBitmapRenderscript(bitmap);
        } else {
            // TODO: Support older apis?
        }

        super.setImageBitmap(blurred);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blurBitmapRenderscript(Bitmap bitmap) {
        Bitmap outBitmap =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(getContext().getApplicationContext());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        blurScript.setRadius(BLUR_RADIUS);

        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        allOut.copyTo(outBitmap);

        bitmap.recycle();

        rs.destroy();

        return outBitmap;
    }
}
