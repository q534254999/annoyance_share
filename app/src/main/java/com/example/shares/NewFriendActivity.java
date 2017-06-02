package com.example.shares;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.example.shares.base.IMutlipleItem;
import com.example.shares.db.NewFriend;
import com.example.shares.db.NewFriendManager;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.model.MyUser;
import com.example.shares.util.PopupList;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.DeleteListener;

import static cn.bmob.v3.c.darkness.log;

public class NewFriendActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NewFriendAdapter adapter;
    LinearLayoutManager layoutManager;
    Toolbar tb_new_friend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        IMutlipleItem<NewFriend> mutlipleItem = new IMutlipleItem<NewFriend>() {

            @Override
            public int getItemViewType(int postion, NewFriend c) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_new_friend;
            }

            @Override
            public int getItemCount(List<NewFriend> list) {
                return list.size();
            }
        };
        tb_new_friend=(Toolbar)findViewById(R.id.tb_new_friend);
        tb_new_friend.setTitle("");
        setSupportActionBar(tb_new_friend);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView=(RecyclerView)findViewById(R.id.new_friend_recyclerview);
        adapter = new NewFriendAdapter(this,mutlipleItem,null);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NewFriendManager.getInstance(this).updateBatchStatus();
        setListener();
    }
    private void setListener(){
        /*ll_root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                query();
            }
        });*/

        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {

            @Override
            public void onItemClick(int position) {
                log("点击："+position);
            }

            @Override
            public boolean onItemLongClick(final int position) {
                //删除
                PopupList popupList = new PopupList(NewFriendActivity.this);
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
                                        AlertDialog.Builder dialog=new AlertDialog.Builder(NewFriendActivity.this);
                                        dialog.setTitle("删除");
                                        dialog.setMessage("确定要删除此消息？");
                                        dialog.setCancelable(false);
                                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                NewFriendManager.getInstance(NewFriendActivity.this).deleteNewFriend(adapter.getItem(position));
                                                adapter.remove(position);
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
        adapter.bindDatas(NewFriendManager.getInstance(this).getAllNewFriend());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return true;
    }
}
