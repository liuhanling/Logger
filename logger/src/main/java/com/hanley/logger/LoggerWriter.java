package com.hanley.logger;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoggerWriter extends Handler implements Thread.UncaughtExceptionHandler {

    private static final int MAX_BYTES = 5 * 1024 * 1024;

    private final String mFolder;
    private final int mMaxSize;

    private String mFileName;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private SimpleDateFormat mTimeFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.CHINA);

    private Context mContext;
    private Thread.UncaughtExceptionHandler mHandler = Thread.getDefaultUncaughtExceptionHandler();

    public LoggerWriter(Looper looper, String folder) {
        this(looper, null, folder);
    }

    public LoggerWriter(Looper looper, Context context, String folder) {
        this(looper, context, folder, MAX_BYTES);
    }

    public LoggerWriter(Looper looper, Context context, String folder, int maxSize) {
        super(looper);
        this.mContext = context;
        this.mFolder = Utils.checkNotNull(folder);
        this.mMaxSize = maxSize;
        this.mFileName = String.format("%s.log", mDateFormat.format(new Date()));
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void handleMessage(Message msg) {
        writeLog((String) msg.obj);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (mHandler != null && e == null) {
            mHandler.uncaughtException(t, e);
        } else {
            handleException(e);
        }
    }

    private synchronized void writeLog(String log) {
        FileWriter fileWriter = null;
        try {
            File file = getFile();
            fileWriter = new FileWriter(file, true);
            fileWriter.append(log);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e1) {
                    // ignore
                }
            }
        }
    }

    private File getFile() throws IOException {
        String date = mDateFormat.format(new Date());
        File folder = new File(mFolder, date);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, mFileName);
        if (!file.exists()) {
            file.createNewFile();
        } else if (file.length() >= mMaxSize) {
            mFileName = String.format("%s.log", mTimeFormat.format(new Date()));
            file = new File(folder, mFileName);
        }

        return file;
    }

    private void handleException(Throwable tr) {
        Logger.e(tr, "Error");
        exit();
    }

    private void exit() {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出现异常，即将退出。", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }

        Process.killProcess(Process.myPid());
        System.exit(1);
    }
}
