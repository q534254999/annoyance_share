package com.example.shares;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shares.model.Annoyance;

import java.util.List;

import cn.carbs.android.expandabletextview.library.ExpandableTextView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Wenpzhou on 2017/4/15 0015.
 */

public class AnnoyanceAdapter extends RecyclerView.Adapter<AnnoyanceAdapter.ViewHolder> implements ExpandableTextView.OnExpandListener{
    private int etvWidth;
    private List<Annoyance> mAnnoyanceList;
    private Context mContext;
    private SparseArray<Integer> mPositionsAndStates = new SparseArray<>();
    static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox hug;
        View annoyanceView;
        TextView annoyanceNickName;
        CircleImageView annoyanceImage;
        TextView annoyanceContentTitle;
        ExpandableTextView annoyanceContentText;
        TextView annoyanceAnswerNum;
        TextView annoyancePublishTime;
        //不加annoyance前缀表示不需要调用set函数
        Button comment;

        public ViewHolder(View view){
            super(view);
            annoyanceView=view;
            annoyanceAnswerNum=(TextView)view.findViewById(R.id.os_answer_num);
            annoyanceContentText=(ExpandableTextView)view.findViewById(R.id.etv_text);
            annoyanceContentTitle=(TextView)view.findViewById(R.id.os_content_title);
            annoyanceImage=(CircleImageView) view.findViewById(R.id.os_image_view);
            annoyanceNickName=(TextView)view.findViewById(R.id.os_nick_name);
            annoyancePublishTime=(TextView)view.findViewById(R.id.os_publish_time);
            comment=(Button)view.findViewById(R.id.os_comment);
            hug=(CheckBox) view.findViewById(R.id.hug);
        }
    }
    public AnnoyanceAdapter(List<Annoyance> annoyanceList){
        mAnnoyanceList=annoyanceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null)
            mContext=parent.getContext();
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annoyance_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Annoyance annoyance=mAnnoyanceList.get(position);
                //点击评论之后的跳转事件，你来实现吧
                //我自己写吧
                Intent intent =new Intent(mContext,AnnoyanceInfoActivity.class);
                intent.putExtra("annoyance_data",annoyance);
                mContext.startActivity(intent);

                //Toast.makeText(v.getContext(),annoyance.getNickName(),Toast.LENGTH_SHORT).show();


            }
        });
        holder.annoyanceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnnoyanceList.get(holder.getAdapterPosition()).getAnonymous().equals(false)) {
                    Intent infoIntent = new Intent(v.getContext(),UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("u",mAnnoyanceList.get(holder.getAdapterPosition()).getAuthor() );
                    infoIntent.putExtra(v.getContext().getPackageName(), bundle);
                    v.getContext().startActivity(infoIntent);
                }
                else
                    Toast.makeText(mContext, "该用户已匿名，无法查看其用户信息", Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Annoyance annoyance=mAnnoyanceList.get(position);
        holder.annoyanceAnswerNum.setText(annoyance.getAnswerNum()+"个回答");

        holder.annoyanceContentTitle.setText(annoyance.getContentTitle());

        if(etvWidth == 0){
            holder.annoyanceContentText.post(new Runnable() {
                @Override
                public void run() {
                    etvWidth = holder.annoyanceContentText.getWidth();
                }
            });
        }
        holder.annoyanceContentText.setTag(position);
        holder.annoyanceContentText.setExpandListener(this);
        Integer state = mPositionsAndStates.get(position);

        holder.annoyanceContentText.updateForRecyclerView(annoyance.getContentText(), etvWidth, state== null ? 0 : state);//第一次getview时肯定为etvWidth为0
        //holder.annoyanceImage.setImageBitmap(annoyance.getAvater());
        //使用Glide轻松加载网络图片
        if(annoyance.getAnonymous().equals(Boolean.FALSE)){
            if (annoyance.getAuthor().getaPath()!=null)
                Glide.with(mContext).load(annoyance.getAuthor().getaPath()).into(holder.annoyanceImage);
            holder.annoyanceNickName.setText(annoyance.getAuthor().getNickname()+ ":");
        }
        else {
            holder.annoyanceImage.setImageResource(R.mipmap.default_head);
            holder.annoyanceNickName.setText("匿名用户：");
        }
        holder.annoyancePublishTime.setText(annoyance.getPublishTime());

        final String tag=new String(annoyance.getObjectId());//初始化一个Integer实例，其值为position
        holder.hug.setTag(tag);
        if (MainActivity.checkboxMap.containsKey(tag)){
            holder.hug.setChecked(MainActivity.checkboxMap.get(tag));

        }else {
            holder.hug.setChecked(false);//true or false 都可以，看实际需求
        }
        holder.hug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.hug.isChecked()){
                    MainActivity.checkboxMap.put(tag,true);

                }else {
                    MainActivity.checkboxMap.put(tag,false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAnnoyanceList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public void onExpand(ExpandableTextView view) {
        Object obj = view.getTag();
        if(obj != null && obj instanceof Integer){
            mPositionsAndStates.put((Integer)obj, view.getExpandState());
        }
    }

    @Override
    public void onShrink(ExpandableTextView view) {
        Object obj = view.getTag();
        if(obj != null && obj instanceof Integer){
            mPositionsAndStates.put((Integer)obj, view.getExpandState());
        }
    }
}
