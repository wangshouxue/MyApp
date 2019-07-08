package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("===R_"+getCurrentTime(),"re---Service");
        Intent intent1 = new Intent(context, PhoneListenService.class);
        context.startService(intent1);
    }
    //获取当前时间，以其为名来保存录音
    private String getCurrentTime(){
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        Date date=new Date();
        String str=format.format(date);
        return str;

    }
}
