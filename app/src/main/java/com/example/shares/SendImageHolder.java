package com.example.shares;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shares.base.BaseViewHolder;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.util.PopupList;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

import static com.example.shares.MainActivity.user;

/**
 * 发送的文本类型
 */

public class SendImageHolder extends BaseViewHolder {

    protected ImageView iv_avatar;
    protected ImageView iv_fail_resend;
    protected TextView tv_time;
    protected ImageView iv_picture;
    protected TextView tv_send_status;
    protected ProgressBar progress_load;
    List<String> popupMenuItemList;
    public float mRawX,mRawY;
    BmobIMConversation c;

    public SendImageHolder(Context context, ViewGroup root, BmobIMConversation c, OnRecyclerViewListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_sent_image,onRecyclerViewListener);
        iv_avatar=(ImageView)itemView.findViewById(R.id.iv_avatar) ;
        iv_fail_resend=(ImageView)itemView.findViewById(R.id.iv_fail_resend);
        iv_picture=(ImageView)itemView.findViewById(R.id.iv_picture);
        tv_time=(TextView)itemView.findViewById(R.id.tv_time);
        tv_send_status=(TextView)itemView.findViewById(R.id.tv_send_status) ;
        progress_load=(ProgressBar)itemView.findViewById(R.id.progress_load);
        this.c =c;
    }

    @Override
    public void bindData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;

        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = BmobIM.getInstance().getUserInfo(msg.getConversationId());
            Glide.with(context).load(user.getaPath()).into(iv_avatar);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        //
        final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(true, msg);
        int status =message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus() ||status == BmobIMSendStatus.UPLOADAILED.getStatus()) {
            iv_fail_resend.setVisibility(View.VISIBLE);
            progress_load.setVisibility(View.GONE);
            tv_send_status.setVisibility(View.INVISIBLE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
            progress_load.setVisibility(View.VISIBLE);
            iv_fail_resend.setVisibility(View.GONE);
            tv_send_status.setVisibility(View.INVISIBLE);
        } else {
            tv_send_status.setVisibility(View.VISIBLE);
            tv_send_status.setText("已发送");
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.GONE);
        }

        //发送的不是远程图片地址，则取本地地址
        if (!TextUtils.isEmpty(message.getRemoteUrl())){
            Glide.with(context).load(message.getRemoteUrl()).into(iv_picture);
            //Logger.i(message.getRemoteUrl());
        }else{
            Glide.with(context).load(message.getLocalPath()).into(iv_picture);
        }

//    ViewUtil.setPicture(TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl(), R.mipmap.ic_launcher, iv_picture,null);

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(info!=null&&info.getName()!=null) {
                    Intent intent = new Intent(getContext(), ImageShowerActivity.class);
                    intent.putExtra("picture",user.getaPath());
                    getContext().startActivity(intent);
                }
            }
        });
        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击图片:"+(TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl())+"");
                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });

        iv_picture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRawX=event.getRawX();
                mRawY=event.getRawY();
                return false;
            }
        });
        iv_picture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                PopupList popupList = new PopupList(v.getContext());
                popupMenuItemList= new ArrayList<>();
                popupMenuItemList.add("删除");
                final int position=getAdapterPosition();
                popupList.showPopupListWindow(v, position, mRawX, mRawY,
                        popupMenuItemList, new PopupList.PopupListListener() {
                            @Override
                            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                                return true;
                            }

                            @Override
                            public void onPopupListClick(View contextView, final int contextPosition, int clickPosition) {
                                switch (clickPosition){
                                    case 0:
                                        AlertDialog.Builder dialog=new AlertDialog.Builder(v.getContext());
                                        dialog.setTitle("删除");
                                        dialog.setMessage("确定要删除此信息？");
                                        dialog.setCancelable(false);
                                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                c.deleteMessage(ChatAdapter.getInstance().getItem(position));
                                                ChatAdapter.getInstance().remove(position);
                                            }
                                        });
                                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                        break;
                                }
                            }

                        });
                return true;
            }
        });

        //重发
        iv_fail_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        progress_load.setVisibility(View.VISIBLE);
                        iv_fail_resend.setVisibility(View.GONE);
                        tv_send_status.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            tv_send_status.setVisibility(View.VISIBLE);
                            tv_send_status.setText("已发送");
                            iv_fail_resend.setVisibility(View.GONE);
                            progress_load.setVisibility(View.GONE);
                        } else {
                            iv_fail_resend.setVisibility(View.VISIBLE);
                            progress_load.setVisibility(View.GONE);
                            tv_send_status.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }


    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);

    }
}
