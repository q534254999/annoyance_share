package com.example.shares;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shares.base.BaseViewHolder;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.util.PopupList;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

import static com.example.shares.MainActivity.user;

/**
 * 发送的文本类型
 */
public class SendTextHolder extends BaseViewHolder implements View.OnClickListener,View.OnLongClickListener {


    protected ImageView iv_avatar;
    protected ImageView iv_fail_resend;
    protected TextView tv_time;
    protected TextView tv_message;
    protected TextView tv_send_status;
    protected ProgressBar progress_load;
    List<String> popupMenuItemList;
    public float mRawX,mRawY;
    BmobIMConversation c;
    Context mContext;


    public SendTextHolder(Context context, ViewGroup root, BmobIMConversation c, OnRecyclerViewListener listener) {
        super(context, root, R.layout.item_chat_sent_message, listener);
        mContext=context;

        iv_avatar =(ImageView)itemView.findViewById(R.id.iv_avatar);
        iv_fail_resend =(ImageView)itemView.findViewById(R.id.iv_fail_resend);
        tv_message=(TextView)itemView.findViewById(R.id.tv_message) ;
        tv_send_status=(TextView)itemView.findViewById(R.id.tv_send_status) ;
        tv_time=(TextView)itemView.findViewById(R.id.tv_time) ;
        progress_load=(ProgressBar)itemView.findViewById(R.id.progress_load) ;
        this.c =c;
    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage)o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        if (user.getaPath()!=null) {
            Glide.with(mContext).load(user.getaPath()).into(iv_avatar);
            //Logger.i(user.getaPath());
        }

        else
            iv_avatar.setImageResource(R.mipmap.default_head);
        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();
        tv_message.setText(content);
        tv_time.setText(time);

        int status =message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {
            iv_fail_resend.setVisibility(View.VISIBLE);
            progress_load.setVisibility(View.GONE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.VISIBLE);
        } else {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.GONE);
        }

        tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击"+message.getContent());
                if(onRecyclerViewListener!=null){
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
                        if(e==null){
                            tv_send_status.setVisibility(View.VISIBLE);
                            tv_send_status.setText("已发送");
                            iv_fail_resend.setVisibility(View.GONE);
                            progress_load.setVisibility(View.GONE);
                        }else{
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
