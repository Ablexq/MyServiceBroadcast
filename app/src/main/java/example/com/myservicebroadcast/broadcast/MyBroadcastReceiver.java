package example.com.myservicebroadcast.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import example.com.myservicebroadcast.util.Constant;
import example.com.myservicebroadcast.service.MyStartService;
import example.com.myservicebroadcast.util.NetUtils;

/**
 * 广播接收器
 */

/*
参考：https://mp.weixin.qq.com/s?__biz=MzI3OTU0MzI4MQ==&mid=2247484940&idx=1&sn=b12d68f545753bd73aeb909961715cfa&chksm=eb476a92dc30e384ccbb9b1710d88dffce2570ba95080b70341d651701057ea50e513be6c182&mpshare=1&scene=23&srcid=1120aFAnOdiOklb7uwo9PPcP#rd
BroadCastReceiver 的生命周期
a. 广播接收者的生命周期非常短暂的，在接收到广播的时候创建，onReceive()方法结束之后销毁；
b. 广播接收者中不要做一些耗时的工作，否则会弹出 Application No Response 错误对话框；
c. 最好也不要在广播接收者中创建子线程做耗时的工作，因为广播接收者被销毁后进程就成为了空进程，很容易被系统杀掉；
d. 耗时的较长的工作最好放在服务中完成；
*/

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Constant.ACTION.equals(intent.getAction())) {

            if (NetUtils.isNetworkConnected(context)) {
                Log.e("000", "MyBroadcastReceiver   接受广播onReceive  网络正常");
                //启动MyStartService服务
                Intent intentService = new Intent(context, MyStartService.class);
                context.startService(intentService);
            } else {
                Log.e("000", "MyBroadcastReceiver   接受广播onReceive  网络异常");
            }
        }

    }

}
