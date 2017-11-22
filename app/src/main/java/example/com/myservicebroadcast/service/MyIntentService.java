package example.com.myservicebroadcast.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 1、它本质是一种特殊的Service,继承自Service并且本身就是一个抽象类
 2、它可以用于在后台执行耗时的异步任务，当任务完成后会自动停止
 3、它拥有较高的优先级，不易被系统杀死（继承自Service的缘故），因此比较适合执行一些高优先级的异步任务
 4、它内部通过HandlerThread和Handler实现异步操作
 5、创建IntentService时，只需实现onHandleIntent和构造方法，onHandleIntent为异步方法，可以执行耗时操作
 */

/*
    生命周期走向：
    构造方法-->onCreate-->onStartCommand-->onStart-->onHandleIntent(异步)-->onDestroy
*/
public class MyIntentService extends IntentService {

    public static final String DOWNLOAD_URL = "download_url";
    public static final String INDEX_FLAG = "index_flag";

    public static UpdateUI updateUI;

    public interface UpdateUI {
        void updateUI(Message message);
    }

    public static void setUpdateUI(UpdateUI updateUIInterface){
        updateUI=updateUIInterface;
    }

    /*-----------------------------------------必须的构造方法和onHandleIntent-----------------------------*/

    public MyIntentService() {
        super("MyIntentService");
        Log.e("999", "------构造方法-------");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {//线程名IntentService[DownLoadApkService]：异步方法，可以执行耗时操作
        Log.e("999", "------onHandleIntent-------");
        //下载上传操作

        //在子线程中进行网络请求
        Bitmap bitmap;
        if (intent != null) {
            bitmap = downloadUrlBitmap(intent.getStringExtra(DOWNLOAD_URL));
            Message msg1 = new Message();
            msg1.what = intent.getIntExtra(INDEX_FLAG, 0);
            msg1.obj = bitmap;
            //通知主线程去更新UI
            if (updateUI != null) {
                updateUI.updateUI(msg1);
            }
        }
    }


    /*-------------------------------------------以下非必须仅供测试-------------------------------------------*/

    @Override
    public void onCreate() {
        Log.e("999", "------onCreate-------");
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.e("999", "------onStartCommand-------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Log.e("999", "------onStart-------");
        super.onStart(intent, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("999", "------onBind-------");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("999", "------onUnbind-------");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e("999", "------onDestroy-------");
        super.onDestroy();
    }

    /*---------------------------------------------------------*/

    private Bitmap downloadUrlBitmap(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
