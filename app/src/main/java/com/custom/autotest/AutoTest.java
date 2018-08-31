package com.custom.autotest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/8/30
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class AutoTest {
    private static final String TAG = "AutoTest";
    private static AutoTest autoTest;
    private Context context;
    private int delayTime = 5000;
    public static AutoTest getInstance() {
        if (autoTest == null) {
            autoTest = new AutoTest();
        }

        return autoTest;
    }

    public void init(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(new SimpleUncaughtExceptionHandler(context));
    }

    public void performKeyEvent() {

    }

    public void performTouch() {

    }

    /**
     * 获取sdcard下面的autotest文件，
     * 如果没有则从assets中获取。
     * 根据autotest文件中的时间，语音内容，去发送广播
     */
    public void performBroadcast() {
        File autotest = new File("/sdcard/autotest.txt");
        String testInfo = null;
        if (!autotest.exists()) {
            testInfo = getFromRaw();
        } else {
            testInfo = getFromFile(autotest);
        }
        Log.i(TAG, "file info : " + testInfo);
        String[] contents = testInfo.split(",");
        int lenght = contents.length;
        for (int i = 0 ; i < lenght ; i++) {
            try {
                String content = contents[i];
                if (content.startsWith("time")) {
                    delayTime = Integer.parseInt(content.substring(content.indexOf(":") + 1, content.length()));
                    Log.i(TAG, "file info time : " + delayTime);
                    continue;
                }
                Log.i(TAG, "file content : " + content);
            } catch (Exception e) {
                Log.e(TAG, "data file resolve failure, " + e.getMessage());
            }
        }
    }

    /**
     * 从assets中获取测试文件中的内容字符
     * @return
     */
    public String getFromRaw(){
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open("autotest.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder sb = new StringBuilder();
            while((line = bufReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从 /sdcard/ 目录下获取测试文件中的内容字符
     * @return
     */
    public String getFromFile(File file){
        try {
            InputStream is = new FileInputStream(file);
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(input);
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class SimpleUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Context context;
        public SimpleUncaughtExceptionHandler(Context context) {
            this.context = context;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            //读取stacktrace信息
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            ex.printStackTrace(printWriter);
            printWriter.close();
            String errorReport = result.toString();
            Log.i(TAG, "UncaughtException ErrorReport=\n" + errorReport);
            writeFileData(errorReport);

            restartApplication(context);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 设置闹钟(AlarmManager)，重启应用
     * @param context
     */
    private void restartApplication(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        am.restartPackage(context.getPackageName());
        Intent intent = context.getPackageManager().
                getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        /*
            AlarmManager.ELAPSED_REALTIME表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间（相对于系统启动开始），状态值为3；
            AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
            AlarmManager.RTC表示闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1；
            AlarmManager.RTC_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0；
            AlarmManager.POWER_OFF_WAKEUP表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，该状态下闹钟也是用绝对时间，状态值为4；不过本状态好像受SDK版本影响，某些版本并不支持；
        */
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
    }

    /**
     * 向指定的文件中写入错误的日志信息
     * 目录地址在 /sdcard/AutoTest/ 下
     */
    public void writeFileData(String content) {
        FileOutputStream fos = null;
        try {
            /* 判断sd的外部设置状态是否可以读写 */
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
                String dateString = formatter.format(currentTime);
                String filePath = "/sdcard/AutoTest/";
                makeDirectory(filePath);
                String fileName = "logcat_" + dateString + ".txt";
                File file = new File(filePath, fileName);
                // 先清空内容再写入
                fos = new FileOutputStream(file);
                //将要写入的字符串转换为byte数组
                byte[] bytes = content.getBytes();
                fos.write(bytes);//将byte数组写入文件
                fos.close();//关闭文件输出流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成文件夹
    public static void makeDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
}
