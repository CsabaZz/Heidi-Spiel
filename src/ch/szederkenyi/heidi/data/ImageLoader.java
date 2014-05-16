package ch.szederkenyi.heidi.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.utils.Utils;

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
}
