package com.example.shares;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shares.model.Topic;

import java.util.List;

/**
 * Created by Wenpzhou on 2017/4/15 0015.
 */

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private List<Topic> mtopicList;
    private Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder{
         View fullview;
        ImageView topicimage;
        TextView topicContentTitle;
        TextView topicContentText;
        //不加topic前缀表示不需要调用set函数
        Button comment;

        public ViewHolder(View view){
            super(view);
           fullview=view;
            topicimage=(ImageView)view.findViewById(R.id.tp_image) ;
            topicContentText=(TextView)view.findViewById(R.id.content_tp);
            topicContentTitle=(TextView)view.findViewById(R.id.title_tp);

         //   comment=(Button)view.findViewById(R.id.comment_tp);

        }
    }
    public TopicAdapter(List<Topic> topicList){
        mtopicList=topicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null)
            mContext=parent.getContext();
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.fullview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Topic topic=mtopicList.get(position);
                //点击评论之后的跳转事件，你来实现吧
                //我自己写吧
                Intent intent =new Intent(mContext,TopicInfoActivity.class);
                intent.putExtra("topic_data",topic);
                mContext.startActivity(intent);

                //Toast.makeText(v.getContext(),topic.getNickName(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Topic topic=mtopicList.get(position);
      //  holder.topicimage.setImageBitmap(topic.getAvater());
        holder.topicContentTitle.setText(topic.getContentTitle());
        holder.topicContentText.setText(topic.getContentText());
      //  holder.topicimage.setImageBitmap(topic.getAvater());
        //使用Glide轻松加载网络图片
        Glide.with(mContext).load(topic.getaPath()).into(holder.topicimage);

    }

    @Override
    public int getItemCount() {
        return mtopicList.size();
    }
}
