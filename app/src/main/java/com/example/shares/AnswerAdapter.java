package com.example.shares;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shares.model.Answer;

import java.util.List;

/**
 * Created by hoem- on 2017/5/16.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private List<Answer> mAnswerList;
    private Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View answerView;
        TextView answerNickName;
        ImageView answerImage;
       // TextView answerContentTitle;
        TextView answerContentText;
        TextView answerCommentNum;
        TextView answerPublishTime;
        public ViewHolder(View itemView) {
            super(itemView);
            answerView=itemView;
            answerNickName=(TextView)itemView.findViewById(R.id.nick_name_a);
            answerImage=(ImageView)itemView.findViewById(R.id.icon_a);
            answerContentText=(TextView)itemView.findViewById(R.id.content_a);
           // answerCommentNum=(TextView)itemView.findViewById(R.id.answer_num);
            answerPublishTime=(TextView)itemView.findViewById(R.id.date_time);
        }
    }
    public AnswerAdapter (List<Answer> answerlist){mAnswerList=answerlist;}

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Answer answer=mAnswerList.get(position);
       // holder.answerContentTitle.setText(topic.getContentTitle());
        holder.answerContentText.setText(answer.getContent());
   //     holder.answerImage.setImageBitmap(answer.getAvater());
     //   使用Glide轻松加载网络图片
       //    Glide.with(mContext).load(topic.getaPath()).into(holder.topicImage);
//        holder.answerCommentNum.setText(answer.getAnswerNum()+"个评论");
        Glide.with(mContext).load(answer.getaPath()).into(holder.answerImage);
        holder.answerPublishTime.setText(answer.getPublishTime());
        holder.answerNickName.setText(answer.getNickName());
    }

    @Override
    public int getItemCount() {
        return mAnswerList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null)
            mContext=parent.getContext();
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
//        holder.answerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position=holder.getAdapterPosition();
//                Answer answer=mAnswerList.get(position);
//                //点击评论之后的跳转事件，你来实现吧
//                //我自己写吧
//                Intent intent =new Intent(mContext,TopicInfoActivity.class);
//                intent.putExtra("answer_data",answer);
//                mContext.startActivity(intent);
//                //Toast.makeText(v.getContext(),answer.getNickName(),Toast.LENGTH_SHORT).show();
//            }
//        });

        return holder;
    }
}
