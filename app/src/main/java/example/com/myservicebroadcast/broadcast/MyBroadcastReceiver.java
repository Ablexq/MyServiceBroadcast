package example.com.myservicebroadcast.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import example.com.myservicebroadcast.util.Constant;
import example.com.myservicebroadcast.service.MyService;
import example.com.myservicebroadcast.util.NetUtils;

/**
 * 广播接收器
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Constant.ACTION.equals(intent.getAction())) {

            if (NetUtils.isNetworkConnected(context)) {
                Log.e("000", "MyBroadcastReceiver   接受广播onReceive  网络正常");
                context.startService(new Intent(context, MyService.class));
            } else {
                Log.e("000", "MyBroadcastReceiver   接受广播onReceive  网络异常");
            }
        }

    }

}
