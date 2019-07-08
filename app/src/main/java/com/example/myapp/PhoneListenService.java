package com.example.myapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 来去电监听服务
 */

public class PhoneListenService extends Service {
    CustomPhoneStateListener customPhoneStateListener;
    TelephonyManager telephonyManager;

    @Override
    public void onCreate() {
        super.onCreate();
        customPhoneStateListener = new CustomPhoneStateListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //app进程被杀掉会失效
        registerPhoneStateListener();
        Log.d("===S_"+getCurrentTime(), "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("===S_"+getCurrentTime(), "onStartCommand");
        //定时器
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, PhoneStateReceiver.class);
        PendingIntent pi =PendingIntent.getBroadcast(this,0, i, 0);
        //SystemClock.elapsedRealtime():返回系统启动到现在的毫秒数，包含休眠时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pi);
        } else {
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),1000*60*3, pi);
        }
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    //获取当前时间，以其为名来保存录音
    private String getCurrentTime(){
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        Date date=new Date();
        String str=format.format(date);
        return str;

    }
    private void registerPhoneStateListener() {
        try {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        Log.d("===S_"+getCurrentTime(), "onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
