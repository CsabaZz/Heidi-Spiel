
package ch.szederkenyi.heidi.utils;

import android.annotation.TargetApi;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public final class ViewUtils {
    private static final int JELLY_BEAN = 16;
    
    private ViewUtils() { }

    public static void setBackground(View view, Drawable background) {
        if (VERSION.SDK_INT >= JELLY_BEAN) {
            SDK16.setBackground(view, background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    public static void setTypeface(TextView textview, Typeface typeface) {
        textview.setPaintFlags(textview.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        textview.setTypeface(typeface);
    }
    
    public static void appendSpan(SpannableStringBuilder ssb, Object span, String data) {
        final int length = ssb.length();
        ssb.append(data);
        ssb.setSpan(span, length, ssb.length(), 0);
    }

    @TargetApi(16)
    private static class SDK16 {

        public static void setBackground(View view, Drawable background) {
            view.setBackground(background);
        }

        private SDK16() { }
    }

}
