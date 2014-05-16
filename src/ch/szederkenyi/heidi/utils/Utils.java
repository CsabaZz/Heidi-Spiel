package ch.szederkenyi.heidi.utils;

import android.content.res.Configuration;
import android.content.res.Resources;

import ch.szederkenyi.heidi.StaticContextApplication;

import java.util.Locale;

public final class Utils {
    private static final String PATTERN_TAG = "Heidi::%s";
    
    private Utils() { }
    
    public static String makeTag(Class<?> clazz) {
        return String.format(PATTERN_TAG, clazz.getSimpleName());
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
}
