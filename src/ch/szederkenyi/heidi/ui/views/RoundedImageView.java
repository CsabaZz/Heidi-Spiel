
package ch.szederkenyi.heidi.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
    
    private float mCornerRadius;

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = (Drawable) getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap fullSizeBitmap = drawableToBitmap(drawable);
        if (fullSizeBitmap == null) {
            return;
        }

        int scaledWidth = getMeasuredWidth();
        int scaledHeight = getMeasuredHeight();

        Bitmap mScaledBitmap;
        if (scaledWidth == fullSizeBitmap.getWidth() && scaledHeight == fullSizeBitmap.getHeight()) {
            mScaledBitmap = fullSizeBitmap;
        } else {
            mScaledBitmap = scaleCenterCrop(fullSizeBitmap, scaledWidth, scaledHeight);
        }

        Bitmap roundBitmap = getRoundedCornerBitmap(mScaledBitmap);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
        if (source == null) {
            return null;
        }
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Bitmap dest = null;

        try {
            dest = Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
        } catch (Exception e) {
        }

        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return dest;
    }
    
    public float getCornerRadius() {
        return mCornerRadius;
    }
    
    public void setCornerRadius(float radius) {
        mCornerRadius = radius;
    }

}
