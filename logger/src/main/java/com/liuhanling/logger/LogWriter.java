package com.liuhanling.logger;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter extends Handler {

    private static final int MAX_BYTES = 10 * 1024 * 1024;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HHmmss");

    private final String folderName;

    private long crashTime;
    private String crashFile;
    private String otherFile;

    LogWriter(Looper looper, String folder) {
        super(looper);
        this.folderName = Utils.checkNotNull(folder);
        this.otherFile = String.format("%s.log", TIME_FORMAT.format(new Date()));
    }

    @Override
    public void handleMessage(Message msg) {
        writeLog(msg.what, (String) msg.obj);
    }

    private synchronized void writeLog(int level, String log) {
        FileWriter fileWriter = null;
        try {
            File file = getFile(level);
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

    private File getFile(int level) throws IOException {
        if (level == LogPrinter.CRASH) {
            return getCrashFile();
        } else {
            return getOtherFile();
        }
    }

    private File getCrashFile() throws IOException {
        long time = System.currentTimeMillis();

        File folder = new File(folderName, DATE_FORMAT.format(time));
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (time - crashTime > 5 * 1000) {
            crashTime = time;
            crashFile = String.format("crash_%s.log", TIME_FORMAT.format(crashTime));
        }

        File file = new File(folder, crashFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private File getOtherFile() throws IOException {
        String date = DATE_FORMAT.format(new Date());
        File folder = new File(folderName, date);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, otherFile);
        if (!file.exists()) {
            file.createNewFile();
        } else if (file.length() >= MAX_BYTES) {
            otherFile = String.format("%s.log", TIME_FORMAT.format(new Date()));
            file = new File(folder, otherFile);
        }
        return file;
    }

}