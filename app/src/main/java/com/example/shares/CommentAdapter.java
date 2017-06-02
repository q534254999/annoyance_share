package com.example.shares;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shares.model.Comment;

import java.util.List;

/**
 * Created by Wenpzhou on 2017/5/7 0007.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mCommentList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView commentContent;
        public ViewHolder(View view){
            super(view);
            commentContent=(TextView)view.findViewById(R.id.comment_content);
        }
    }
    public CommentAdapter(List<Comment> commentList){
        mCommentList=commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment=mCommentList.get(position);
        if(comment.getReply()==null)
        holder.commentContent.setText(comment.getAuthor().getNickname()+":"+comment.getContent());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }
}
