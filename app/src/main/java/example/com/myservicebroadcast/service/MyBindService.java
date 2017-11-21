package example.com.myservicebroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * 参考：
 * Android中startService和bindService的区别：
 * http://www.jianshu.com/p/d870f99b675c
 */

/*
当采用Context.bindService()方法启动服务，与之有关的生命周期方法
onCreate()--> onBind() --> onUnbind() --> onDestroy()



*/

public class MyBindService extends Service {

    private int count = 0;
    private boolean connecting = false;
    private boolean threadDisable = false;

    private DataCallback callback;

    public void setCallback(DataCallback callback) {
        this.callback = callback;
    }

    public interface DataCallback {
        void onDataChange(String data);
    }

    @Override
    public void onCreate() {
        Log.e("111", "MyBindService ===onCreate===");

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //bind多次也只会调用一次onBind方法
        Log.e("111", "MyBindService ===onBind===");
        //如果这边不返回一个IBinder的接口实例，那么ServiceConnection中的onServiceConnected就不会被调用
        //那么bind所具有的传递数据的功能也就体现不出来
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public MyBindService calleat() {
            eat();
            return MyBindService.this;
        }
    }

    public void eat() {
        Log.e("111", "MyBindService ===MyBinder===");
        Toast.makeText(getApplicationContext(), "toast开始吃东西了", Toast.LENGTH_SHORT).show();

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

                    if (callback != null) {
                        callback.onDataChange(count + "");
                    }
                    Log.e("111", "MyBinder ==  Count is " + count);
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("111", "MyBindService ===onStartCommand===");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("111", "MyBindService ===onUnbind===");

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e("111", "MyBindService ===onDestroy===");

        count = 0;
        threadDisable = true;

        super.onDestroy();
    }


}
