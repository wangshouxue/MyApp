package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yhd.hdmediaplayer.MediaPlayerHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class SecondAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<AudioBean> list;

    public SecondAdapter(Context mContext){
        this.mContext=mContext;
    }
    public void setNewData(List<AudioBean> list){
        this.list=list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_second,parent,false);
        return new ItemHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AudioBean bean=list.get(position);
        ItemHodel itemHodel= (ItemHodel) holder;
        itemHodel.name.setText(bean.getName());
        itemHodel.size.setText(bean.getSize());

        itemHodel.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mPlayer = new MediaPlayer() ;
                try {
                    //解决播放报错问题
                    File tempFile = new File(bean.getPath());
                    FileInputStream fis = new FileInputStream(tempFile);
                    mPlayer.setDataSource(fis.getFD());
                    mPlayer.prepare();
                    mPlayer.start();
                    Log.i("===p",mPlayer.getDuration()+"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(mContext,PlayerActivity.class);
                intent.putExtra("name",bean.getName());
                intent.putExtra("path",bean.getPath());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (list==null||list.size()==0){
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    static class ItemHodel extends RecyclerView.ViewHolder{
        TextView name,size;

        public ItemHodel(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            size=itemView.findViewById(R.id.size);
        }
    }
}
