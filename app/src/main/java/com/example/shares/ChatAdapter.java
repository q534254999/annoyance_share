package com.example.shares;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shares.base.BaseViewHolder;
import com.example.shares.model.MyUser;
import com.example.shares.util.PopupList;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;

import static com.example.shares.MainActivity.user;
import com.example.shares.listener.OnRecyclerViewListener;

/**
 * Created by Wenpzhou on 2017/5/11 0011.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE =6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //视频
    private final int TYPE_SEND_VIDEO =8;
    private final int TYPE_RECEIVER_VIDEO = 9;

    //同意添加好友成功后的样式
    private final int TYPE_AGREE = 10;



    /**
     * 显示时间间隔:10分钟
     */
    private final long TIME_INTERVAL = 10 * 60 * 1000;

    public List<BmobIMMessage> msgList = new ArrayList<>();
    private String currentUid="";
    public static ChatAdapter instance;
    BmobIMConversation c;
    public static ChatAdapter getInstance(){
        return instance;
    }

    public ChatAdapter(Context context,BmobIMConversation c){
        instance=this;
        try{
            currentUid= user.getObjectId();
        }catch (Exception e){
            Logger.e(e.getMessage());
        }
        this.c=c;
    }
    public int findPosition (BmobIMMessage message){
        int index=this.getCount();
        int position = -1;
        while(index-- >0){
            if (message.equals(this.getItemId(index))){
                position=index;
                break;
            }
        }
        return position;
    }
    public int getCount() {
        return this.msgList == null?0:this.msgList.size();
    }

    public void addMessages(List<BmobIMMessage> messages){

        msgList.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        msgList.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }

    /**获取消息
     * @param position
     * @return
     */
    public BmobIMMessage getItem(int position){
        return this.msgList == null?null:(position >= this.msgList.size()?null:this.msgList.get(position));
    }

    /**移除消息
     * @param position
     */
    public void remove(int position){
        msgList.remove(position);
        notifyDataSetChanged();
    }

    public BmobIMMessage getFirstMessage(){
        if(msgList!=null&&msgList.size()>0){
            return msgList.get(0);
        }else
            return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_SEND_TXT) {
            return new SendTextHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } else if (viewType == TYPE_SEND_IMAGE) {
            return new SendImageHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } /*else if (viewType == TYPE_SEND_LOCATION) {
            return new SendLocationHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } else if (viewType == TYPE_SEND_VOICE) {
            return new SendVoiceHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } */else if (viewType == TYPE_RECEIVER_TXT) {
            return new ReceiveTextHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_IMAGE) {
            return new ReceiveImageHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } /*else if (viewType == TYPE_RECEIVER_LOCATION) {
            return new ReceiveLocationHolder(parent.getContext(), parent,onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_VOICE) {
            return new ReceiveVoiceHolder(parent.getContext(), parent,onRecyclerViewListener);
        } else if (viewType == TYPE_SEND_VIDEO) {
            return new SendVideoHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_VIDEO) {
            return new ReceiveVideoHolder(parent.getContext(), parent,onRecyclerViewListener);
        }*/else if(viewType ==TYPE_AGREE) {
            return new AgreeHolder(parent.getContext(),parent,onRecyclerViewListener);
        }else{//开发者自定义的其他类型，可自行处理
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder)holder).bindData(msgList.get(position));
        if (holder instanceof ReceiveTextHolder) {
            ((ReceiveTextHolder)holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveImageHolder) {
            ((ReceiveImageHolder)holder).showTime(shouldShowTime(position));
        }/*else if (holder instanceof ReceiveLocationHolder) {
            ((ReceiveLocationHolder)holder).showTime(shouldShowTime(position));
        }else if (holder instanceof ReceiveVoiceHolder) {
            ((ReceiveVoiceHolder)holder).showTime(shouldShowTime(position));*/
        else if (holder instanceof SendTextHolder) {
            ((SendTextHolder)holder).showTime(shouldShowTime(position));
        }else if (holder instanceof SendImageHolder) {
            ((SendImageHolder)holder).showTime(shouldShowTime(position));
        }/*else if (holder instanceof SendLocationHolder) {
            ((SendLocationHolder)holder).showTime(shouldShowTime(position));
        }else if (holder instanceof SendVoiceHolder) {
            ((SendVoiceHolder)holder).showTime(shouldShowTime(position));
        }else if (holder instanceof SendVideoHolder) {//随便模拟的视频类型
            ((SendVideoHolder)holder).showTime(shouldShowTime(position));
        }else if (holder instanceof ReceiveVideoHolder) {
            ((ReceiveVideoHolder)holder).showTime(shouldShowTime(position));
        }*/else if (holder instanceof AgreeHolder) {//同意添加好友成功后的消息
            ((AgreeHolder)holder).showTime(shouldShowTime(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = msgList.get(position);
        if(message.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
        }else if(message.getMsgType().equals(BmobIMMessageType.LOCATION.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_LOCATION: TYPE_RECEIVER_LOCATION;
        }else if(message.getMsgType().equals(BmobIMMessageType.VOICE.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VOICE: TYPE_RECEIVER_VOICE;
        }else if(message.getMsgType().equals(BmobIMMessageType.TEXT.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }else if(message.getMsgType().equals(BmobIMMessageType.VIDEO.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VIDEO: TYPE_RECEIVER_VIDEO;
        }else if(message.getMsgType().equals("agree")) {//显示欢迎
            return TYPE_AGREE;
        }else{
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = msgList.get(position - 1).getCreateTime();
        long curTime = msgList.get(position).getCreateTime();
        return curTime - lastTime > TIME_INTERVAL;
    }
}
