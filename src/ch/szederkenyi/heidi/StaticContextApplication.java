
package ch.szederkenyi.heidi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaticContextApplication extends Application {

    private static Application sInstance;
    private static List<Activity> sActivityContextList = new ArrayList<Activity>();

    public StaticContextApplication() {
        super();
        sInstance = this;
    }

    public static Context getAppContext() {
        return sInstance;
    }

    public static Resources getStaticResources() {
        return sInstance.getResources();
    }

    public static Locale getStaticLocale() {
        final Resources resources = StaticContextApplication.getStaticResources();
        final Configuration config = resources.getConfiguration();
        return config.locale;
    }

    public static String getStaticString(int resId) {
        return sInstance.getString(resId);
    }

    public static String[] getStaticArray(int resId) {
        if(null == sInstance) {
            return null;
        } else {
            return getStaticResources().getStringArray(resId);
        }
    }

    public static boolean getStaticBoolean(int resId) {
        return sInstance.getResources().getBoolean(resId);
    }

    public static Activity getCurrentActivity() {
        if (sActivityContextList.isEmpty()) {
            return null;
        }

        return sActivityContextList.get(sActivityContextList.size() - 1);
    }

    public static void addActivity(Activity context) {
        sActivityContextList.add(context);
    }

    public static void removeActivity(Activity context) {
        sActivityContextList.remove(context);
    }

}
