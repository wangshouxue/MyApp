package com.example.myapp;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 来去电监听
 */

public class CustomPhoneStateListener extends PhoneStateListener {

    private MediaRecorder recorder; //录音的一个实例
    private static String lastNumber="";
    private static int lastStatus=0;//0挂断，1，响铃，2接听

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        Log.d("===L", "CustomPhoneStateListener onServiceStateChanged: " + serviceState);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d("===L", "CustomPhoneStateListener state: " + state + " incomingNumber: " + incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                Log.i("===L","电话挂断");
                lastStatus=0;
                try {
                    //抬起手指，停止录音
                    if (recorder!=null) {
                        //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                        //报错为：RuntimeException:stop failed
                        recorder.setOnErrorListener(null);
                        recorder.setOnInfoListener(null);
                        recorder.setPreviewDisplay(null);
                        recorder.stop();//停止录音
                        recorder.release();//释放资源
                        recorder = null;
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

                break;
            case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                Log.i("===L","电话响铃");
                lastStatus=1;
//                HangUpTelephonyUtil.endCall(mContext);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:    // 来电接通 或者 去电，去电接通  但是没法区分
                Log.i("===L","电话接通");
                try {
//                    if (!lastNumber.equals(incomingNumber)){
                    if(lastStatus!=2){
                        if (recorder==null){
                            recorder=new MediaRecorder();//初始化录音对象
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置录音的输入源(麦克)
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//设置音频格式(3gp)
                            recorder.setOutputFile(getRecorderFile()+"/"+incomingNumber+"_"+ getCurrentTime()+".3gp"); //设置录音保存的文件
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码
                            recorder.prepare();//准备录音
                            recorder.start(); //接听的时候开始录音

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lastStatus=2;

                break;
        }
        lastNumber=incomingNumber;

    }

    private String getRecorderFile() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = absolutePath + "/aaa_record";
        File file=new File(filePath);
        if (!file.exists()){
            file.mkdir();
        }
        return filePath;
    }
    //获取当前时间，以其为名来保存录音
    private String getCurrentTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        Date date=new Date();
        String str=format.format(date);
        return str;

    }
}
