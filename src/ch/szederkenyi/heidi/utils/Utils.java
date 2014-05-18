package ch.szederkenyi.heidi.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import ch.szederkenyi.heidi.StaticContextApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public final class Utils {
    private static final String PATTERN_TAG = "Heidi::%s";
    
    private static final String TAG = makeTag(Utils.class);
    
    private static final int BUFFER_SIZE = 32768;
    
    private Utils() { }
    
    public static String makeTag(Class<?> clazz) {
        return String.format(PATTERN_TAG, clazz.getSimpleName());
    }
    
    public static float getDip(float value) {
        final Context context = StaticContextApplication.getAppContext();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
    }

    public static float getSip(float value) {
        final Context context = StaticContextApplication.getAppContext();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
                context.getResources().getDisplayMetrics());
    }

    public static int getDip(int value) {
        return (int) getDip((float)value);
    }

    public static int getSip(int value) {
        return (int) getSip((float)value);
    }


    public static void changeLanguage(String langCode, String countryCode) {
        Locale def = Locale.getDefault();
        if (!langCode.equalsIgnoreCase(def.getLanguage())) {
            return;
        }
        
        Locale locale = new Locale(langCode, countryCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        final Resources resources = StaticContextApplication.getStaticResources();
        resources.updateConfiguration(config, null);
    }
    
    public static void copyStream(InputStream source, OutputStream destination) {
        try {
            byte[] bytes = new byte[Utils.BUFFER_SIZE];
            int count = source.read(bytes, 0, Utils.BUFFER_SIZE);
            while (count > -1) {
                destination.write(bytes, 0, count);
                count = source.read(bytes, 0, Utils.BUFFER_SIZE);
            }
            destination.flush();
        } catch (IOException ex) {
            Log.e(TAG, "Can not copy the incoming stream", ex);
        }
    }
    
    public static void closeInputStream(InputStream stream, String errorMessage) {
        try {
            if (null != stream) {
                stream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, errorMessage, e);
        }
    }

    public static void closeOutputStream(OutputStream stream, String errorMessage) {
        try {
            if (null != stream) {
                stream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, errorMessage, e);
        }
    }
}
