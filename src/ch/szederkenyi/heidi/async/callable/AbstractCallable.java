package ch.szederkenyi.heidi.async.callable;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.async.AbstractTask;
import ch.szederkenyi.heidi.utils.NetworkUtil;
import ch.szederkenyi.heidi.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.concurrent.Callable;

public abstract class AbstractCallable<E> implements Callable<E> {
    
    private AbstractTask.Entity mEntity;
    
    protected abstract E processContent(String content);
    
    public AbstractCallable(AbstractTask.Entity entity) {
        mEntity = entity;
    }

    @Override
    public E call() throws Exception {
        if(TextUtils.isEmpty(mEntity.filename)) {
            return loadHttp();
        } else {
            return loadLocale();
        }
    }

    protected E loadHttp() throws Exception {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpGet httpGet = new HttpGet(mEntity.url + getParameters());
    
        final HttpResponse response = httpClient.execute(httpGet);
        final BufferedInputStream bufferedStream = readBufferedStream(response);
        final String content = NetworkUtil.getBytes(bufferedStream);
        
        return processContent(content);
    }
    
    protected E loadLocale() throws Exception {
        final Context context = StaticContextApplication.getAppContext();
        final AssetManager manager = context.getAssets();
        final InputStream in = manager.open(mEntity.filename);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Utils.copyStream(in, out);
        
        final String content = out.toString();
        return processContent(content);
    }

    private String getParameters() {
    	StringBuilder sb = new StringBuilder();
        
        int paramCount = null == mEntity.params ? 0 : mEntity.params.size();
        if(paramCount == 0) {
        	return "";
        }
        
        final char firstChar = mEntity.url.contains("?") ? '&' : '?';
        final char sndChar = '&';
        
        for(int i = 0; i < paramCount; ++i) {
            final BasicNameValuePair param = mEntity.params.get(i);
            sb.append(i == 0 ? firstChar : sndChar).append(param.getName());
            sb.append('=').append(Uri.encode(param.getValue()));
        }
        
        return sb.toString();
	}

	private BufferedInputStream readBufferedStream(HttpResponse response) 
            throws IllegalStateException, IOException {
        final HttpEntity entity = response.getEntity();
        final InputStream incomingStream = entity.getContent();
        return new BufferedInputStream(incomingStream);
    }
    
    public static void writeStringToFile(String filename, String content) 
            throws IOException {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/VB2014DebugFiles");
        directory.mkdirs();
        
        File file = new File(directory, filename);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
        Writer out = new BufferedWriter(osw);
        out.append(content);
        out.flush();
        out.close();
    }
    
    protected int optInt(String key, JSONObject object) {
        if(null == object) {
            return 0;
        } else {
            return object.optInt(key);
        }
    }
    
    protected long optLong(String key, JSONObject object) {
        if(null == object) {
            return 0L;
        } else {
            return object.optLong(key);
        }
    }
    
    protected String optString(String key, JSONObject object) {
        if(null == object) {
            return "";
        } else {
            final String text = object.optString(key);
            if(TextUtils.isEmpty(text)) {
                return "";
            } else {
                return text.trim();
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    protected Date optDate(String key, JSONObject object) {
        if(null == object) {
            return null;
        } else {
            final String dateStr = object.optString(key);
            if(TextUtils.isEmpty(dateStr)) {
                return null;
            } else {
                return new Date(Date.parse(dateStr));
            }
        }
    }

}
