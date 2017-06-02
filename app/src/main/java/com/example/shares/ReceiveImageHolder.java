package com.example.shares;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.shares.util.PopupList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

import static com.example.shares.MainActivity.user;

/**
 * 接收到的文本类型
 */
public class ReceiveImageHolder extends BaseViewHolder {



    List<String> popupMenuItemList;
    public float mRawX,mRawY;
    protected ImageView iv_avatar;
    protected TextView tv_time;
    protected ImageView iv_picture;
    protected ProgressBar progress_load;
    BmobIMConversation c;

    public ReceiveImageHolder(Context context, ViewGroup root,BmobIMConversation c ,OnRecyclerViewListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_received_image,onRecyclerViewListener);
        iv_avatar=(ImageView)itemView.findViewById(R.id.iv_avatar) ;
        iv_picture=(ImageView)itemView.findViewById(R.id.iv_picture);
        tv_time=(TextView)itemView.findViewById(R.id.tv_time);
        progress_load=(ProgressBar)itemView.findViewById(R.id.progress_load);
        this.c=c;
    }

    @Override
    public void bindData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = BmobIM.getInstance().getUserInfo(msg.getConversationId());
        if(info!=null&&info.getAvatar()!=null){
            Glide.with(context).load(info.getAvatar()).into(iv_avatar);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        //可使用buildFromDB方法转化为指定类型的消息
        final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(false,msg);
        //显示图片
        progress_load.setVisibility(View.VISIBLE);
        Glide.with(context).load(message.getRemoteUrl()).into(iv_picture);
        progress_load.setVisibility(View.INVISIBLE);


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

        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击图片:"+message.getRemoteUrl()+"");
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

    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}