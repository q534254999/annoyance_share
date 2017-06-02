package com.example.shares;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shares.base.BaseViewHolder;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.model.MyUser;
import com.example.shares.util.PopupList;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

import static com.example.shares.MainActivity.user;

/**
 * 接收到的文本类型
 */

public class ReceiveTextHolder extends BaseViewHolder {

    List<String> popupMenuItemList;
    public float mRawX,mRawY;
    protected ImageView iv_avatar;

    protected TextView tv_time;

    protected TextView tv_message;
    BmobIMConversation c;
    public ReceiveTextHolder(Context context, ViewGroup root,BmobIMConversation c ,OnRecyclerViewListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_received_message, onRecyclerViewListener);
        tv_message = (TextView) itemView.findViewById(R.id.tv_message);
        tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        this.c=c;
    }


    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage) o;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        tv_time.setText(time);
        final BmobIMUserInfo info = BmobIM.getInstance().getUserInfo(message.getConversationId());
        if(info!=null&&info.getAvatar()!=null) {
            Logger.i(info.getAvatar());
            Glide.with(context).load(info.getAvatar()).into(iv_avatar);
        }

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                MyUser chatUser = new MyUser();
                chatUser.setNickname(info.getName());
                chatUser.setaPath(info.getAvatar());
                chatUser.setObjectId(info.getUserId());
                bundle.putSerializable("u",chatUser);
                Intent infoIntent = new Intent(context, UserInfoActivity.class);
                infoIntent.putExtra(getContext().getPackageName(),bundle);
                context.startActivity(infoIntent);
            }
        });
        String content = message.getContent();
        tv_message.setText(content);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(info!=null&&info.getAvatar()!=null) {
                    Intent intent = new Intent(getContext(), ImageShowerActivity.class);
                    intent.putExtra("picture",info.getAvatar());
                    getContext().startActivity(intent);
                }
            }
        });
        tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击" + message.getContent());
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });

        tv_message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRawX=event.getRawX();
                mRawY=event.getRawY();
                return false;
            }
        });
        tv_message.setOnLongClickListener(new View.OnLongClickListener() {
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
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }



}