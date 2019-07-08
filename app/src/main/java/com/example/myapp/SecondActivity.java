package com.example.myapp;

import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    SecondAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        RecyclerView rv=findViewById(R.id.rv);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        rv.setLayoutManager(manager);

        adapter=new SecondAdapter(this);
        rv.setAdapter(adapter);

        initData();
    }

    private void initData() {
        List<AudioBean> list=new ArrayList<AudioBean>();
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = absolutePath + "/aaa_record";
        File fileDir=new File(filePath);
        for (File file :fileDir.listFiles()){
            if(!file.isDirectory()&&file.getName().endsWith(".3gp")){
                AudioBean bean=new AudioBean();
                bean.setName(file.getName());
                bean.setSize(divide(file.length(),1024,2)+"KB");
                bean.setPath(file.getPath());
                list.add(bean);
            }
        }
        adapter.setNewData(list);
    }
    /**
     * valueOne:除数
     * valueTwo:被除数
     * scale:保留几位小数
     */
    public String divide(long valueOne,int valueTwo,int scale){
        BigDecimal b1 =new BigDecimal(valueOne);
        BigDecimal b2 =new BigDecimal(valueTwo);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();
    }
}
