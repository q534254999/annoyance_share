package com.example.shares;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shares.model.AddFriendMessage;
import com.example.shares.model.Friend;
import com.example.shares.model.MyUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.shares.MainActivity.user;
import static com.example.shares.MyApplication.getContext;
import static com.example.shares.R.id.btn_chat;

public class UserInfoActivity extends AppCompatActivity {

    private BmobIMUserInfo info;
    private MyUser anotherUser;
    private ImageView userInfoAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Button btn_add_friend =(Button)findViewById(R.id.btn_add_friend);
        btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddFriendMessage();
            }
        });
        Button btn_chat =(Button)findViewById(R.id.btn_chat) ;

        userInfoAvatar = (ImageView)findViewById(R.id.user_info_avator);
        userInfoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(anotherUser.getaPath()!=null) {
                    Intent intent = new Intent(UserInfoActivity.this, ImageShowerActivity.class);
                    intent.putExtra("picture",anotherUser.getaPath());
                    startActivity(intent);
                }

            }
        });
        TextView userInfoName= (TextView)findViewById(R.id.user_info_name);
        Bundle bundle=new Bundle();
        if(getIntent()!=null && getIntent().hasExtra(getPackageName())) {
            bundle = getIntent().getBundleExtra(getPackageName());
            anotherUser = (MyUser)bundle.getSerializable("u");
        }
        if(anotherUser.getObjectId().equals(user.getObjectId())){
            btn_add_friend.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
        }else{
            btn_add_friend.setVisibility(View.VISIBLE);
            btn_chat.setVisibility(View.VISIBLE);
        }
        info=new BmobIMUserInfo(anotherUser.getObjectId(),anotherUser.getNickname(),anotherUser.getaPath());
        if(anotherUser.getaPath()!=null)
            Glide.with(this).load(anotherUser.getaPath()).into(userInfoAvatar);
        else
            userInfoAvatar.setImageResource(R.mipmap.default_head);
        userInfoName.setText(anotherUser.getNickname());
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobIM.getInstance().updateUserInfo(info);
                //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                    @Override
                    public void done(BmobIMConversation bmobIMConversation, BmobException e) {
                        if (e==null){
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("c", bmobIMConversation);
                            Intent intent = new Intent(UserInfoActivity.this,ChatActivity.class);
                            intent.putExtra(getPackageName(),bundle);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private void sendAddFriendMessage(){
        //启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
        //设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true,null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        final BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        final AddFriendMessage msg =new AddFriendMessage();
        final MyUser user =BmobUser.getCurrentUser(getContext(), MyUser.class);

        MyUser friend = new MyUser();
        friend.setObjectId(info.getUserId());
        user.isFriend(friend,new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                Toast.makeText(getContext(), "你已添加此人为好友", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, String s) {
                msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
                Map<String,Object> map =new HashMap<>();
                map.put("name", user.getNickname());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
                map.put("avatar",user.getaPath());//发送者的头像
                map.put("uid",user.getObjectId());//发送者的uid
                msg.setExtraMap(map);
                conversation.sendMessage(msg, new MessageSendListener() {
                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {//发送成功
                            Toast.makeText(UserInfoActivity.this, "好友请求发送成功，等待验证", Toast.LENGTH_SHORT).show();
                        } else {//发送失败
                            Toast.makeText(UserInfoActivity.this, "发送失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
