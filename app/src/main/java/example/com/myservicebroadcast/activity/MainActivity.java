package example.com.myservicebroadcast.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import example.com.myservicebroadcast.service.MyBindService;
import example.com.myservicebroadcast.service.MyIntentService;
import example.com.myservicebroadcast.util.Constant;
import example.com.myservicebroadcast.broadcast.MyBroadcastReceiver;
import example.com.myservicebroadcast.service.MyStartService;
import example.com.myservicebroadcast.R;

/**
 * 参考：
 * 广播启动服务：
 * http://blog.csdn.net/jianghuiquan/article/details/8641152
 * service与activity通信
 * 广播：https://www.cnblogs.com/linjiqin/p/3147764.html
 * 接口：https://www.cnblogs.com/Fndroid/p/5187444.html
 * 网络工具类：
 * http://blog.csdn.net/sinat_31057219/article/details/77651705
 */

/*
第一个不同之处，启动服务出来传入的参数不同外，还有调用的方法不同。
第二个不同之处，就是bindService在Activity销毁的时候要使用unbindService，而是用startService则不用，
startService会在Android内存存在压力的时候才调用stopService，又或者开发者主动调用stopService
第三个不同之处，在于bindService是个隐藏的服务，无法在运行的进程中找到，而startService则可以在运行的进程与服务中找到
*/
public class MainActivity extends AppCompatActivity {

    /**
     * 图片地址集合
     */
    private String url[] = {
            "http://img.blog.csdn.net/20160903083245762",
            "http://img.blog.csdn.net/20160903083252184",
            "http://img.blog.csdn.net/20160903083257871",
            "http://img.blog.csdn.net/20160903083257871",
            "http://img.blog.csdn.net/20160903083311972",
            "http://img.blog.csdn.net/20160903083319668",
            "http://img.blog.csdn.net/20160903083326871"
    };
    private static ImageView imageView;
    private static final Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    };


    private MyBroadcastReceiver myBroadcastReceiver;
    private TextView mTv;
    private TextView mTv1;
    private MyConnection conn;
    private MyBindService.MyBinder mybind;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTv1.setText(msg.obj.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image);

        Intent intent = new Intent(this,MyIntentService.class);
        for (int i=0;i<7;i++) {//循环启动任务
            intent.putExtra(MyIntentService.DOWNLOAD_URL,url[i]);
            intent.putExtra(MyIntentService.INDEX_FLAG,i);
            startService(intent);
        }
        MyIntentService.setUpdateUI(new MyIntentService.UpdateUI() {
            @Override
            public void updateUI(Message message) {
                mUIHandler.sendMessageDelayed(message,message.what * 1000);
            }
        });

        /*--------------------------------------------------------*/

        mTv = ((TextView) findViewById(R.id.tv));
        mTv1 = ((TextView) findViewById(R.id.tv1));
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //网络监听，下载服务
                //a. 广播接收者的生命周期非常短暂的，在接收到广播的时候创建，onReceive()方法结束之后销 毁；
                //b. 广播接收者中不要做一些耗时的工作，否则会弹出 Application No Response 错误对话框；
                //c. 最好也不要在广播接收者中创建子线程做耗时的工作，因为广播接收者被销毁后进程就成为了空进程，很容易被系统杀掉；
                //d. 耗时的较长的工作最好放在服务中完成；
                myBroadcastReceiver = new MyBroadcastReceiver();
                IntentFilter filter = new IntentFilter(Constant.ACTION);
                registerReceiver(myBroadcastReceiver, filter);


                //开启MyBindService服务
                Intent service = new Intent(MainActivity.this, MyBindService.class);
                conn = new MyConnection();
                //第一个参数：Intent意图
                //第二个参数：是一个接口，通过这个接口接收服务开启或者停止的消息，并且这个参数不能为null
                //第三个参数：开启服务时的操作，BIND_AUTO_CREATE代表自动创建service
                bindService(service, conn, BIND_AUTO_CREATE);
            }
        });

        //接受service传递过来的数据
        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("example.com.myservicebroadcast.service");
        registerReceiver(receiver, filter);

    }

    private class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //只有当我们自己写的MyBindService的onBind方法返回值不为null时，才会被调用
            Log.e("111", "MyConnection == onServiceConnected");
            mybind = (MyBindService.MyBinder) service;

            MyBindService myService = mybind.calleat();
            myService.setCallback(new MyBindService.DataCallback() {
                @Override
                public void onDataChange(String data) {
                    Log.e("111", "onDataChange==线程==" + Thread.currentThread().getName());
                    Message msg = new Message();
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //这个方法只有出现异常时才会调用，服务器正常退出不会调用。
            Log.e("111", "MyConnection == onServiceDisconnected");
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int count = bundle.getInt("count");
            mTv.setText(count + "");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //停止广播
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
        }

        //停止MyStartService服务
        stopService(new Intent(MainActivity.this, MyStartService.class));

        //bindService启动服务，退出时要用unbindService关闭服务。否则报错
        //结束MyBindService服务
        if (conn != null) {
            unbindService(conn);
        }
    }
}
