package example.com.myservicebroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 下载服务
 */

public class MyService extends Service {

    private int count = 0;
    private boolean threadDisable = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("111", "MyService   onCreate   加载数据");


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!threadDisable) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                    Log.e("111", "Count is " + count);

                    //发送广播
                    Intent intent = new Intent();
                    intent.putExtra("count", count);
                    intent.setAction("example.com.myservicebroadcast.service");
                    sendBroadcast(intent);
                }
            }
        }).start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("111", "MyService   onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("111", "MyService   onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e("111", "MyService   onDestroy");

        count = 0;
        threadDisable = true;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("111", "MyService   onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


}
