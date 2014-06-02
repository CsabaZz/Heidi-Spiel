package ch.szederkenyi.heidi.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.utils.ConstantUtils;
import ch.szederkenyi.heidi.utils.Utils;

import java.io.IOException;

public class ImageLoader {
    private static final String TAG = Utils.makeTag(ImageLoader.class);
    
    private static int findOptimalSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        final float height = options.outHeight;
        final float width = options.outWidth;
        
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final float halfHeight = height / 2f;
            final float halfWidth = width / 2f;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    
    public static void loadImageFromAsset(final ImageView imageview, final String imagename) {
        if(imageview.getWidth() == 0 && imageview.getHeight() == 0) {
            imageview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                
                @Override
                public void onGlobalLayout() {
                    RemoveOGLListener.removeOGLListener(imageview, this);
                    privateLoadImageFromAsset(imageview, imagename);
                }
            });
        } else {
            privateLoadImageFromAsset(imageview, imagename);
        }
    }
    
    private static void privateLoadImageFromAsset(final ImageView imageview, final String imagename) {
        final Context context = StaticContextApplication.getAppContext();
        final AssetManager manager = context.getAssets();
        
        try {
            final BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inJustDecodeBounds = true;
            
            BitmapFactory.decodeStream(manager.open(imagename), null, opts);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = ImageLoader.findOptimalSampleSize(opts, imageview.getWidth(), imageview.getHeight());
            
            final Bitmap bitmap = BitmapFactory.decodeStream(manager.open(imagename), null, opts);
            final BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            imageview.setImageDrawable(drawable);
        } catch(IOException ex) {
            Log.e(TAG, "Can not load the image", ex);
        }
    }
    
    public static Spanned loadImageFromAsset(String imagename, float maxSize) {
        final SpannableString str = new SpannableString("I");
        
        final Context context = StaticContextApplication.getAppContext();
        final AssetManager manager = context.getAssets();
        
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(manager.open(imagename));
            final float scale = maxSize / Math.max(bitmap.getWidth(), bitmap.getHeight());
            
            final BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setBounds(0, 0, (int)(bitmap.getWidth() * scale), (int)(bitmap.getHeight() * scale));
            
            final ImageSpan image = new ImageSpan(drawable);
            str.setSpan(image, 0, str.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } catch(IOException ex) {
            Log.e(TAG, "Can not load the image", ex);
        }
        
        return str;
    }
    
    private static class RemoveOGLListener {
        public static void removeOGLListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
            if(android.os.Build.VERSION.SDK_INT < ConstantUtils.JELLY_BEAN) {
                RemoveOGLListenerApi1.removeOGLListener(v, listener);
            } else {
                RemoveOGLListenerApi16.removeOGLListener(v, listener);
            }
        }
    }
    
    @TargetApi(ConstantUtils.ANDROID_BASE)
    private static class RemoveOGLListenerApi1 {
        @SuppressWarnings("deprecation")
        public static void removeOGLListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }
    
    @TargetApi(ConstantUtils.JELLY_BEAN)
    private static class RemoveOGLListenerApi16 {
        public static void removeOGLListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}
