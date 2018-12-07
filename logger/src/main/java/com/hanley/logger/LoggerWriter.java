package com.hanley.logger;

import android.annotation.SuppressLint;
import android.app.ApplicationErrorReport;
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

public class LoggerWriter extends Handler implements Thread.UncaughtExceptionHandler {

    private static final int MAX_BYTES = 5 * 1024 * 1024;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HHmmss");

    private final Context mContext;
    private final String mFolder;
    private final CrashCall mCrashCall;

    private String mFileName;
    private Thread.UncaughtExceptionHandler mHandler = Thread.getDefaultUncaughtExceptionHandler();

    public LoggerWriter(Looper looper, Context context, String folder, CrashCall crashCall) {
        super(looper);
        this.mContext = context;
        this.mFolder = Utils.checkNotNull(folder);
        this.mCrashCall = crashCall;
        this.mFileName = String.format("%s.log", TIME_FORMAT.format(new Date()));
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void handleMessage(Message msg) {
        writeLog((String) msg.obj);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && mHandler != null) {
            mHandler.uncaughtException(t, e);
        } else {
            exitSystem();
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
        String date = DATE_FORMAT.format(new Date());
        File folder = new File(mFolder, date);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, mFileName);
        if (!file.exists()) {
            file.createNewFile();
        } else if (file.length() >= MAX_BYTES) {
            mFileName = String.format("%s.log", TIME_FORMAT.format(new Date()));
            file = new File(folder, mFileName);
        }

        return file;
    }

    private boolean handleException(Throwable t) {
        Logger.log(ApplicationErrorReport.TYPE_CRASH, Logger.TAG, "Error", t);
        if (t == null) {
            return false;
        }
        if (mCrashCall != null) {
            mCrashCall.handle();
        } else {
            exitSystem();
        }
        return true;
    }

    private void exitSystem() {
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
