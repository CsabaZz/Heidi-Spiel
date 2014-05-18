
package ch.szederkenyi.heidi.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import android.util.Log;

public final class NetworkUtil {
    private static final String TAG = Utils.makeTag(NetworkUtil.class);
    
    private static final int BUFFER_SIZE = 32768;
    
    private NetworkUtil() { }
    
    public static String getBytes(BufferedInputStream bis) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            byte[] bytes = new byte[BUFFER_SIZE];
            int count = bis.read(bytes, 0, BUFFER_SIZE);
            while (count > -1) {
                out.write(bytes, 0, count);
                count = bis.read(bytes, 0, BUFFER_SIZE);
            }
            out.flush();
            return new String(out.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            Log.e(TAG, "There was an error during reading bytes from input stream", ex);
        } finally {
            Utils.closeInputStream(bis, "");
            Utils.closeOutputStream(out, "");
        }
        return null;
    }
}
