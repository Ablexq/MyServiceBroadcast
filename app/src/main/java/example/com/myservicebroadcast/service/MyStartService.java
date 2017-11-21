package example.com.myservicebroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 下载服务
 *
 * 参考：
 * http://blog.csdn.net/jianghuiquan/article/details/8641152
 *
 */
/*
开启Service有两种不同的方式：startService和bindService。不同的开启方式，Service执行的生命周期方法也不同。

//  生命周期：
onCreate() ==> onStartCommand() ==> onDestroy
启动服务时，调用多次startService，onCreate只有第一次会被执行，而onStartCommand会执行多次。
结束服务时，调用stopService，生命周期执行onDestroy方法，并且多次调用stopService时，onDestroy只有第一次会被执行。
startService开启服务以后，与activity就没有关联，不受影响，独立运行。

//  开启服务：
Intent service = new Intent(this, MyStartService.class);
startService(service);

//  结束服务：
stopService(service);

*/

public class MyStartService extends Service {

    private int count = 0;
    private boolean threadDisable = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("111", "MyStartService   onCreate   加载数据");

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
        Log.e("111", "MyStartService   onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("111", "MyStartService   onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e("111", "MyStartService   onDestroy");

        count = 0;
        threadDisable = true;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("111", "MyStartService   onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


}
