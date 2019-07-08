package com.example.myapp;

import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerActivity extends AppCompatActivity {
    MediaPlayer mPlayer;
    boolean isPlay=false;
    TextView start;
    SeekBar seekBar;
    ImageView iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String name=getIntent().getStringExtra("name");
        TextView tv=findViewById(R.id.filename);
        tv.setText(name);
        final String path=getIntent().getStringExtra("path");
        mPlayer = new MediaPlayer() ;
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlay=false;
                mPlayer.stop();
                mPlayer.release();
                mPlayer=null;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        try {
            //解决播放报错问题
            File tempFile = new File(path);
            FileInputStream fis = new FileInputStream(tempFile);
            mPlayer.setDataSource(fis.getFD());
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iv=findViewById(R.id.iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay){
                    //暂停
                    isPlay=false;
                    mPlayer.pause();
                    iv.setImageResource(R.drawable.play);
                }else {
                    isPlay=true;
                    mPlayer.start();
                    iv.setImageResource(R.drawable.stopi);
                    // 创建一个线程
                    Thread thread = new Thread(new MuiscThread());
                    // 启动线程
                    thread.start();
                }
            }
        });

        start=findViewById(R.id.startTime);
        start.setText("00:00");
        TextView end=findViewById(R.id.endTime);
        end.setText(formatime(mPlayer.getDuration()));
        seekBar=findViewById(R.id.seekBar);
        seekBar.setMax(mPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    //建立一个子线程实现Runnable接口
    class MuiscThread implements Runnable {
        @Override
        public void run() {
            //判断音乐的状态，在不停止与不暂停的情况下向总线程发出信息
            while (mPlayer != null) {
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发出的信息
                handler.sendEmptyMessage(mPlayer.getCurrentPosition());
            }
        }
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 将SeekBar位置设置到当前播放位置
            seekBar.setProgress(msg.what);
            //获得音乐的当前播放时间
            start.setText(formatime(msg.what));
        }
    };
    //时间转换类，将得到的音乐时间毫秒转换为时分秒格式
    private String formatime(int lengrh) {
        Date date = new Date(lengrh);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String totalTime = sdf.format(date);
        return totalTime;
    }

}
