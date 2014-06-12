package ch.szederkenyi.heidi.data.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ch.szederkenyi.heidi.StaticContextApplication;

public class StorageManager {
    
    public static void completeCategory(String categoryFileName) {
        final Context context = StaticContextApplication.getAppContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(categoryFileName, true).commit();
    }
    
    public static boolean isCategoryCompleted(String categoryFileName) {
        final Context context = StaticContextApplication.getAppContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(categoryFileName, false);
    }
}
