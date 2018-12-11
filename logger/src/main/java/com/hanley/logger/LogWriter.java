package com.hanley.logger;

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

    private static final int MAX_BYTES = 5 * 1024 * 1024;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HHmmss");

    private final String mFolder;

    private String mFileName;

    public LogWriter(Looper looper, String folder) {
        super(looper);
        this.mFolder = Utils.checkNotNull(folder);
        this.mFileName = String.format("%s.log", TIME_FORMAT.format(new Date()));
    }

    @Override
    public void handleMessage(Message msg) {
        writeLog((String) msg.obj);
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
}
