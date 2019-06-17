package com.liuhanling.logger;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class LogPrinter implements Printer {

    static final int CRASH = 8;

    private final ThreadLocal<String> mLocalTag = new ThreadLocal<>();
    private final Config mConfig;

    LogPrinter(Context context) {
        this(new LogConfig.Builder(context).build());
    }

    LogPrinter(Config config) {
        this.mConfig = Utils.checkNotNull(config);
    }

    @Override
    public Printer t(String tag) {
        if (tag != null) {
            mLocalTag.set(tag);
        }
        return this;
    }

    @Override
    public void v(Object object) {
        log(Log.VERBOSE, Utils.toString(object));
    }

    @Override
    public void v(String message, Throwable tr) {
        log(Log.VERBOSE, message, tr);
    }

    @Override
    public void v(String message, Object... args) {
        log(Log.VERBOSE, message, args);
    }

    @Override
    public void d(Object object) {
        log(Log.DEBUG, Utils.toString(object));
    }

    @Override
    public void d(String message, Throwable tr) {
        log(Log.DEBUG, message, tr);
    }

    @Override
    public void d(String message, Object... args) {
        log(Log.DEBUG, message, args);
    }

    @Override
    public void i(Object object) {
        log(Log.INFO, Utils.toString(object));
    }

    @Override
    public void i(String message, Throwable tr) {
        log(Log.INFO, message, tr);
    }

    @Override
    public void i(String message, Object... args) {
        log(Log.INFO, message, args);
    }

    @Override
    public void w(Object object) {
        log(Log.WARN, Utils.toString(object));
    }

    @Override
    public void w(String message, Throwable tr) {
        log(Log.WARN, message, tr);
    }

    @Override
    public void w(String message, Object... args) {
        log(Log.WARN, message, args);
    }

    @Override
    public void e(Object object) {
        log(Log.ERROR, Utils.toString(object));
    }

    @Override
    public void e(String message, Throwable tr) {
        log(Log.ERROR, message, tr);
    }

    @Override
    public void e(String message, Object... args) {
        log(Log.ERROR, message, Utils.toString(args));
    }

    @Override
    public void a(Object object) {
        log(Log.ASSERT, Utils.toString(object));
    }

    @Override
    public void a(String message, Throwable tr) {
        log(Log.ASSERT, message, tr);
    }

    @Override
    public void a(String message, Object... args) {
        log(Log.ASSERT, message, args);
    }

    @Override
    public void c(String message, Throwable tr) {
        log(CRASH, message, tr);
    }

    @Override
    public void j(String json) {
        j("", json);
    }

    @Override
    public void j(String message, String json) {
        Utils.checkNotNull(message);
        if (Utils.isEmpty(json)) {
            d(message + "Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject object = new JSONObject(json);
                message = Utils.isEmpty(message) ? message : message + '\n';
                d(message + object.toString(2));
                return;
            }
            if (json.startsWith("[")) {
                JSONArray object = new JSONArray(json);
                message = Utils.isEmpty(message) ? message : message + '\n';
                d(message + object.toString(2));
                return;
            }
            e(message + "Invalid Json");
        } catch (JSONException e) {
            e(message, e);
        }
    }

    @Override
    public void x(String xml) {
        x("", xml);
    }

    @Override
    public void x(String message, String xml) {
        Utils.checkNotNull(message);
        if (Utils.isEmpty(xml)) {
            d(message + "Empty/Null xml content");
            return;
        }
        try {
            Source source = new StreamSource(new StringReader(xml));
            StreamResult result = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            String msg = result.getWriter().toString().replaceFirst(">", ">\n");
            message = Utils.isEmpty(message) ? message : message + '\n';
            d(message + msg);
        } catch (TransformerException e) {
            e(message, e);
        }
    }

    @Override
    public void log(int priority, String message, Throwable tr) {
        log(priority, message + '\n' + Log.getStackTraceString(tr));
    }

    @Override
    public synchronized void log(int priority, String message, Object... args) {
        Utils.checkNotNull(message);
        String tag = getTempTag();
        String msg = Utils.isEmpty(args) ? message : String.format(message, args);
        mConfig.log(priority, tag, msg);
    }

    private String getTempTag() {
        String tag = mLocalTag.get();
        if (tag != null) {
            mLocalTag.remove();
        }
        return tag;
    }
}