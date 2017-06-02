package com.example.shares;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shares.base.IMutlipleItem;
import com.example.shares.db.NewFriend;
import com.example.shares.db.NewFriendManager;
import com.example.shares.event.RefreshEvent;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.model.Conversation;
import com.example.shares.model.MyUser;
import com.example.shares.model.NewFriendConversation;
import com.example.shares.model.PrivateConversation;
import com.example.shares.util.PopupList;
import com.orhanobut.logger.Logger;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.bmob.v3.c.darkness.log;
import static com.example.shares.MainActivity.user;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private View view;
    Toolbar newsToolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.news_fragment, container, false);
        IMutlipleItem<Conversation> mutlipleItem = new IMutlipleItem<Conversation>() {

            @Override
            public int getItemViewType(int postion, Conversation c) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_conversation;
            }

            @Override
            public int getItemCount(List<Conversation> list) {
                return list.size();
            }
        };
        newsToolbar=(Toolbar)view.findViewById(R.id.tb_news);
        newsToolbar.setTitle("");
        CircleImageView iv_avater=(CircleImageView) view.findViewById(R.id.iv_avatar);
        if (user!=null&&user.getaPath()!=null){
            Glide.with(view.getContext()).load(user.getaPath()).placeholder(R.mipmap.default_head).into(iv_avater);
        }
        else
            iv_avater.setImageResource(R.mipmap.default_head);
        iv_avater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        recyclerView=(RecyclerView)view.findViewById(R.id.news_recyclerview) ;
        adapter = new ConversationAdapter(getActivity(),mutlipleItem,null);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        setListener();


        return view;

    }

    private void setListener() {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                query();
            }
        });

        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            private float mRawX,mRawY;

            @Override
            public void onItemClick(int position) {
                adapter.getItem(position).onClick(getActivity());
            }

            @Override
            public boolean onItemLongClick(final int position) {
                PopupList popupList = new PopupList(getContext());
                List<String> popupMenuItemList;
                popupMenuItemList= new ArrayList<>();
                popupMenuItemList.add("删除");
                popupList.showPopupListWindow(recyclerView, position, adapter.mRawX, adapter.mRawY,
                        popupMenuItemList, new PopupList.PopupListListener() {
                            @Override
                            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                                return true;
                            }

                            @Override
                            public void onPopupListClick(View contextView, final int contextPosition, int clickPosition) {
                                switch (clickPosition){
                                    case 0:
                                        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                                        dialog.setTitle("删除");
                                        dialog.setMessage("确定要删除此对话？");
                                        dialog.setCancelable(false);
                                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                adapter.remove(position);
                                                adapter.getItem(position).onLongClick(getActivity());
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
    @Override
    public void onResume() {
        super.onResume();
        query();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    /**
     查询本地会话
     */
    public void query(){
        //adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }
    /**
     * 获取会话列表的数据：增加新朋友会话
     * @return
     */
    private List<Conversation> getConversations(){
        //添加会话
        List<Conversation> conversationList = new ArrayList<>();
        conversationList.clear();
        List<BmobIMConversation> list =BmobIM.getInstance().loadAllConversation();
        if(list!=null && list.size()>0){
            for (BmobIMConversation item:list){
                switch (item.getConversationType()){
                    case 1://私聊
                        conversationList.add(new PrivateConversation(item));
                        break;
                    default:
                        break;
                }
            }
        }
        //添加新朋友会话-获取好友请求表中最新一条记录
        List<NewFriend> friends = NewFriendManager.getInstance(getActivity()).getAllNewFriend();
        if(friends!=null && friends.size()>0){
            conversationList.add(new NewFriendConversation(friends.get(0)));
        }
        //重新排序
        Collections.sort(conversationList);
        return conversationList;
    }




    private void createCoversation(){
        BmobIMUserInfo info=new BmobIMUserInfo(user.getObjectId(),user.getNickname(),user.getaPath());
        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
            @Override
            public void done(BmobIMConversation bmobIMConversation, BmobException e) {
                if(e==null){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("bombIMConversation",bmobIMConversation);
                    Intent intent = new Intent(MyApplication.getContext(),ChatActivity.class);
                    startActivity(intent,bundle);
                }else{
                    Toast.makeText(MyApplication.getContext(), e.getMessage()+"("+e.getErrorCode()+")", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        log("---会话页接收到自定义消息---");
        //因为新增`新朋友`这种会话类型
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        //重新刷新列表
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }

    /**注册消息接收事件
     * @param event
     * 1、与用户相关的由开发者自己维护，SDK内部只存储用户信息
     * 2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        //重新获取本地消息并刷新列表
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }
}
