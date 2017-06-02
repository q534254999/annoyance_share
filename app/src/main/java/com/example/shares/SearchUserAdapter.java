package com.example.shares;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.model.MyUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wenpzhou on 2017/5/10 0010.
 */

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder>{
    private List<MyUser> searchUserList = new ArrayList<>();

    public SearchUserAdapter(List<MyUser> userList) {
        searchUserList.addAll(userList);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public void setDatas(List<MyUser> list) {
        searchUserList.clear();
        if (null != list) {
            searchUserList.addAll(list);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView avater ;
        TextView nickName;
        Button lookForDetails;
        public ViewHolder(View view){
            super(view);
            avater=(ImageView)view.findViewById(R.id.search_user_avatar);
            nickName=(TextView)view.findViewById(R.id.search_user_name);
            lookForDetails=(Button)view.findViewById(R.id.btn_for_details);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyUser user=searchUserList.get(position);
        if(user.getaPath()!=null)
            Glide.with(MyApplication.getContext()).load(user.getaPath()).into(holder.avater);
        else
            holder.avater.setImageResource(R.mipmap.default_head);
        holder.nickName.setText(user.getNickname());
        holder.lookForDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看个人详情
                Bundle bundle= new Bundle();
                bundle.putSerializable("u",user);
                Intent infoIntent = new Intent(MyApplication.getContext(),UserInfoActivity.class);
                infoIntent.putExtra(MyApplication.getContext().getPackageName(),bundle);
                infoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                MyApplication.getContext().startActivity(infoIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchUserList.size();
    }
}
