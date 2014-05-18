package ch.szederkenyi.heidi.data;

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
import android.widget.ImageView;

import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.utils.Utils;
import ch.szederkenyi.heidi.utils.ViewUtils;

import java.io.IOException;

public class ImageLoader {
    private static final String TAG = Utils.makeTag(ImageLoader.class);
    
    public static void loadImageFromAsset(ImageView imageview, String imagename) {
        final Context context = StaticContextApplication.getAppContext();
        final AssetManager manager = context.getAssets();
        
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(manager.open(imagename));
            imageview.setImageBitmap(bitmap);
        } catch(IOException ex) {
            Log.e(TAG, "Can not load the image", ex);
        }
    }
    
    public static void loadImageFromAsset(ImageView imageview, String imagename, float maxSize) {
        final Context context = StaticContextApplication.getAppContext();
        final AssetManager manager = context.getAssets();
        
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(manager.open(imagename));
            final float scale = maxSize / Math.max(bitmap.getWidth(), bitmap.getHeight());
            
            final BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            imageview.setImageDrawable(drawable);
            
            drawable.setBounds(0, 0, (int)(bitmap.getWidth() * scale), (int)(bitmap.getHeight() * scale));
            drawable.invalidateSelf();
        } catch(IOException ex) {
            Log.e(TAG, "Can not load the image", ex);
        }
    }
    
    public static void loadBackgroundFromAsset(View view, String imagename) {
        final Context context = StaticContextApplication.getAppContext();
        final AssetManager manager = context.getAssets();
        
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(manager.open(imagename));
            final BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            ViewUtils.setBackground(view, drawable);
        } catch(IOException ex) {
            Log.e(TAG, "Can not load the image", ex);
        }
    }
    
    public static Spanned loadImageFromAsset(String imagename) {
        final SpannableString str = new SpannableString(imagename);
        
        final Context context = StaticContextApplication.getAppContext();
        final AssetManager manager = context.getAssets();
        
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(manager.open(imagename));
            final BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            
            final ImageSpan image = new ImageSpan(drawable);
            str.setSpan(image, 0, str.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } catch(IOException ex) {
            Log.e(TAG, "Can not load the image", ex);
        }
        
        return str;
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
}
