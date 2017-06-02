package com.example.shares;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.example.shares.base.IMutlipleItem;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.model.Friend;
import com.example.shares.model.MyUser;
import com.example.shares.util.PopupList;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    ContactAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar=(Toolbar)findViewById(R.id.add_friend_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_friend:
                        Intent addFriendIntent = new Intent(ContactActivity.this,SearchUserActivity.class);
                        startActivity(addFriendIntent);
                        break;
                    default:
                        break;
                }
                return true;
            }

        });
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        IMutlipleItem<Friend> mutlipleItem = new IMutlipleItem<Friend>() {

            @Override
            public int getItemViewType(int postion, Friend friend) {
                if(postion==0){
                    return ContactAdapter.TYPE_NEW_FRIEND;
                }else{
                    return ContactAdapter.TYPE_ITEM;
                }
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                if(viewtype== ContactAdapter.TYPE_NEW_FRIEND){
                    return R.layout.header_new_friend;
                }else{
                    return R.layout.item_contact;
                }
            }

            @Override
            public int getItemCount(List<Friend> list) {
                return list.size()+1;
            }
        };
        recyclerView=(RecyclerView)findViewById(R.id.contact_recyclerview);
        adapter = new ContactAdapter(this,mutlipleItem,null);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setListener();
    }

    private void setListener(){

        /*ContactActivity.this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                query();
            }
        });*/

        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {

            @Override
            public void onItemClick(int position) {
                if (position == 0) {//跳转到新朋友页面
                    Intent intent = new Intent(ContactActivity.this,NewFriendActivity.class);
                    startActivity(intent);
                } else {
                    Friend friend = adapter.getItem(position);
                    final MyUser anotherUser = friend.getFriendUser();
                    BmobIMUserInfo info = new BmobIMUserInfo(anotherUser.getObjectId(), anotherUser.getUsername(), anotherUser.getaPath());
                    BmobIM.getInstance().updateUserInfo(info);
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                        @Override
                        public void done(BmobIMConversation bmobIMConversation, BmobException e) {
                            if (e==null){
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("c", bmobIMConversation);
                                Intent intent = new Intent(ContactActivity.this,ChatActivity.class);
                                intent.putExtra(getPackageName(),bundle);
                                startActivity(intent);
                            }
                        }
                    });

                }
            }

            @Override
            public boolean onItemLongClick(final int position) {
                Logger.i("长按" + position);
                if(position==0){
                    return true;
                }
                PopupList popupList = new PopupList(ContactActivity.this);
                adapter.popupMenuItemList= new ArrayList<>();

                adapter.popupMenuItemList.add("删除");
                popupList.showPopupListWindow(getCurrentFocus(), position, adapter.mRawX, adapter.mRawY,
                        adapter.popupMenuItemList, new PopupList.PopupListListener() {
                            @Override
                            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                                return true;
                            }

                            @Override
                            public void onPopupListClick(View contextView, final int contextPosition, int clickPosition) {
                                switch (clickPosition){
                                    case 0:
                                        AlertDialog.Builder dialog=new AlertDialog.Builder(ContactActivity.this);
                                        dialog.setTitle("删除");
                                        dialog.setMessage("确定要删除此好友？");
                                        dialog.setCancelable(false);
                                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                MyUser.getInstance().deleteFriend(adapter.getItem(position), new DeleteListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Toast.makeText(ContactActivity.this, "好友删除成功", Toast.LENGTH_SHORT).show();
                                                        adapter.remove(position);
                                                    }

                                                    @Override
                                                    public void onFailure(int i, String s) {
                                                        Toast.makeText(ContactActivity.this, "好友删除失败"+i+" "+s, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
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


    public void query(){
        MyUser.getInstance().queryFriends(new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                adapter.bindDatas(list);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(int i, String s) {
                adapter.bindDatas(null);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_add,menu);
        return true;
    }
}
