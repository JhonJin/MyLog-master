package com.example.mylog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String extpath;
    private EditText mEdit;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
//        d = new sd();
        mEdit = (EditText) findViewById(R.id.EditText);
        getSDPath();
        extpath = getExtSDpath();//外置SD的路径
        Log.e("", "1.外置SD卡的路径为extpath=" + extpath);
        Log.e("", "sd=====================" + sd.getExtSDCardPaths());
    }

    //外部存储设备的路径,get this method
    private String getStoragePath(Context mContext, boolean is_removale) {//外部存储设备的路径

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取外置SD卡路径
    public String getExtSDpath() {
        String sdCard_path = null;
        String sdCard_default = Environment.getExternalStorageDirectory().getAbsolutePath();//外置存储卡的绝对路径
        if (sdCard_default.equals("/")) {
            sdCard_default = sdCard_default.substring(0, sdCard_default.length() - 1);
        }
        //路径
        Runtime runtime = Runtime.getRuntime();
        try {
            java.lang.Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line = null;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String colums[] = line.split(" ");
                    if (colums != null && colums.length > 1) {
                        if (sdCard_default.trim().equals(colums[1].trim())) {
                            continue;
                        }
                        sdCard_path = colums[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String colums[] = line.split(" ");
                    if (colums != null && colums.length > 1) {
                        if (sdCard_default.trim().equals(colums[1].trim())) {
                            continue;
                        }
                        sdCard_path = colums[1];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sdCard_path;
    }

    //获取外置SD卡路径
    public void getSDPath() {
        File sdDir = null;
        boolean sdCardExit = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断SD卡是否存在
        if (sdCardExit) {//判断SD卡是否存在，如果存在获取SD卡的根目录
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        Log.e("", "2.外置SD卡的根目录：" + sdDir);
    }

    //向存储卡写入内容,同时可以读取数据
    public void saveToextSDCard(String filename, String content) {//判断是否有数据传递过来
        File file = new File(Environment.getExternalStorageDirectory(), filename);//文件路径,文件名
        //Environment.getExternalStorageDirectory()路径/storage/sdcard0
        Log.e("", "Environment.getExternalStorageDirectory()路径" + Environment.getExternalStorageDirectory());
        FileWriter fw = null;
        PrintWriter log = null;
        //先判断content是否有数据
        if (content == null) {
            Toast.makeText(this, "未发现数据,无法导出", Toast.LENGTH_SHORT).show();
        }
        try {
            fw = new FileWriter(file, true);
            log = new PrintWriter(fw);
            log.println(content);
            Date date = new Date();
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = mFormat.format(date);
            log.println("---" + time + "---");
            log.flush();
            log.close();//关闭流
            fw.close();
            Log.e("", "saveToextSDCard向SD卡写入数据成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("", "外置储存卡的路径...:" + file);
    }

    //从文件中读取数据
//    public void loadData(String filename) {
//        Log.e("", "loadData方法被调用了.........................");
//        File file = new File(Environment.getExternalStorageDirectory(), filename);
//        BufferedReader br = null;
//        try {
//            FileInputStream fr = new FileInputStream(file);
//            br = new BufferedReader(new InputStreamReader(fr));
//            String line = null;
//            StringBuilder content = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                content.append(line);
//            }
//            Log.e("", "content-------" + content);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //将内部存储的文件读入到外部存储中

    /**
     * @param oldName 内部存储的文件
     * @param newName 外部存储的文件
     */
    public void inTooutContent(String oldName, String newName) {
        File file = new File(getStoragePath(getApplicationContext(), true), newName);//TF卡路径
        Log.e("", "Environment.getExternalStorageDirectory()==" + Environment.getExternalStorageDirectory());
        BufferedReader br = null;
        FileInputStream fis = null;
        FileWriter fw = null;//将读取的数据写入到外部存储中
        PrintWriter pw = null;
        //先将内部存储的内容读出来,将数据写入外部存储中
        try {
            fw = new FileWriter(file);//对数据不进行追加
            fis = openFileInput(oldName);
            pw = new PrintWriter(fw);
            br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            StringBuilder sb = new StringBuilder();//显示数据功能
            while ((line = br.readLine()) != null) {
                sb.append(line);//回显数据
                pw.println(line);
            }
            pw.flush();
            pw.close();
            br.close();
            Toast.makeText(this, "数据导出完成", Toast.LENGTH_SHORT).show();//提醒用户数据导出
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //.........file.delete
    public void deleteContent(String filename) {//删除外存设备数据
        File outfile = new File(Environment.getExternalStorageDirectory(), filename);
        outfile.delete();
        Log.e("", filename + "已经删除了");
    }

    //管理员用户写入内容保存数据
    public void saveData(String str) {//当程序退出时，才保存相关数据信息及日期
        FileOutputStream fos = null;
//      FileWriter fw = null;
        PrintWriter log = null;

        try {
            fos = openFileOutput("data.txt", Context.MODE_APPEND);//得到写入数据的文件在原有的数据后面继续追加内容.
//            fw = new FileWriter("data", true);
            log = new PrintWriter(fos);
            log.println(str);
            log.flush();
            log.close();//关闭流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData2(String str) {
        FileOutputStream fos = null;
        PrintWriter log = null;
        try {
            fos = openFileOutput("data.txt", Context.MODE_APPEND);
            log = new PrintWriter(fos);
            log.println(str);
            Date date = new Date();
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = mFormat.format(date);
            log.println("---" + time + "---");
            log.flush();
            log.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //在程序退出之前保存数据
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String str = mEdit.getText().toString();
        saveData("用户名1:" + str);
        saveData2("用户名2:" + str);
        inTooutContent("data.txt", "outdata.txt");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(90000);//时间设置为:90秒
//                    deleteContent("DataManager");//删除文件
//                    Log.e("", "DataManager文件被删除了");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.mylog/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.mylog/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
