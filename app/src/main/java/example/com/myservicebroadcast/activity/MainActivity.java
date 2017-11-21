package example.com.myservicebroadcast.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import example.com.myservicebroadcast.util.Constant;
import example.com.myservicebroadcast.broadcast.MyBroadcastReceiver;
import example.com.myservicebroadcast.service.MyService;
import example.com.myservicebroadcast.R;

public class MainActivity extends AppCompatActivity {

    private MyBroadcastReceiver myBroadcastReceiver;
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv = ((TextView) findViewById(R.id.tv));
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myBroadcastReceiver = new MyBroadcastReceiver();
                IntentFilter filter = new IntentFilter(Constant.ACTION);
                registerReceiver(myBroadcastReceiver, filter);
            }
        });

        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("example.com.myservicebroadcast.service");
        MainActivity.this.registerReceiver(receiver, filter);
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
        unregisterReceiver(myBroadcastReceiver);

        //停止服务
        stopService(new Intent(MainActivity.this, MyService.class));
    }
}
