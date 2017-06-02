package com.example.shares.model;

import android.widget.Toast;

import com.example.shares.MyApplication;
import com.example.shares.listener.QueryUserListener;
import com.example.shares.listener.UpdateCacheListener;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.QueryListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.shares.MainActivity.user;
import static com.example.shares.MyApplication.getContext;

/**
 * Created by Wenpzhou on 2017/4/9 0009.
 * 用户信息 BmobUser已经包括用户名和密码等信息
 */

public class MyUser extends BmobUser implements Serializable{
    private String nickname;
    private BmobFile avatar;
    private String aPath;
    private static MyUser instance = new MyUser();

    public static MyUser getInstance(){

        instance=BmobUser.getCurrentUser(MyApplication.getContext(),MyUser.class);
        return instance;

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getaPath() {
        return aPath;
    }

    public void setaPath(String aPath) {
        this.aPath = aPath;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    /**更新用户资料和会话资料
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event,final UpdateCacheListener listener){
        final BmobIMConversation conversation=event.getConversation();
        final BmobIMUserInfo info =event.getFromUserInfo();
        final BmobIMMessage msg =event.getMessage();
        String username =info.getName();
        String title =conversation.getConversationTitle();
        Logger.i("" + username + "," + title);
        //sdk内部，将新会话的会话标题用objectId表示，因此需要比对用户名和会话标题--单聊，后续会根据会话类型进行判断
        if(!username.equals(title)) {
            MyUser.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(MyUser s, BmobException e) {
                    if(e==null){
                        String name =s.getUsername();
                        String avatar = s.getaPath();
                        Logger.i("query success："+name+","+avatar);
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //更新用户资料
                        BmobIM.getInstance().updateUserInfo(info);
                        //更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if(!msg.isTransient()){
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    }else{
                        Logger.e(e.getMessage());
                    }
                    listener.done(null);
                }
            });
        }else{
            BmobIM.getInstance().updateUserInfo(info);
            listener.internalDone(null);
        }
    }

    /**查询用户信息
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener){
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(getContext(), new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if(list!=null && list.size()>0){
                    listener.internalDone(list.get(0), null);
                }else{
                    listener.internalDone(new BmobException(000, "查无此人"));
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.internalDone(new BmobException(i, s));
            }
        });
    }

    /**查询用户
     * @param nickname
     * @param listener
     */
    public void queryUsers(final String nickname, final FindListener<MyUser> listener){
        BmobQuery<MyUser> query = new BmobQuery<>();
        //去掉当前用户
        try {
            MyUser user = BmobUser.getCurrentUser(getContext(),MyUser.class);
            query.addWhereNotEqualTo("nickname",user.getNickname());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final List<MyUser> tmplist=new ArrayList<>();
        query.order("-createdAt");
        query.findObjects(getContext(), new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list != null && list.size() > 0) {
                    for (int i=0;i<list.size();i++) {
                        if (list.get(i).getNickname().contains(nickname))
                            tmplist.add(list.get(i));
                    }
                    listener.onSuccess(tmplist);

                } else {
                    listener.onError(1000, "查无此人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }


    /**
     * 查询是否好友关系
     * @param friend
     * @param listener
     */
    public void isFriend(final MyUser friend,final FindListener<Friend> listener){
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("user",MyUser.getInstance());
        query.include("friendUser");
        query.findObjects(getContext(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                int flag=0;
                for(Friend f:list) {
                    if (friend.getObjectId().equals(f.getFriendUser().getObjectId())) {
                        listener.onSuccess(list);
                        Logger.i(friend.getObjectId());
                        flag=1;
                    }
                }
                if(flag==0)
                    listener.onError(0, "无此好友");

            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    /**
     * 同意添加好友
     * @param friend
     * @param listener
     */


    public void agreeAddFriend(final MyUser friend,final SaveListener listener){
        final Friend f = new Friend();
        final MyUser user =BmobUser.getCurrentUser(getContext(), MyUser.class);
        user.isFriend(friend,new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                Toast.makeText(getContext(), "你已添加此人为好友", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, String s) {
                f.setUser(user);
                f.setFriendUser(friend);
                f.save(getContext(),listener);
            }
        });

    }

    /**
     * 查询好友
     * @param listener
     */
    public void queryFriends(final FindListener<Friend> listener){
        BmobQuery<Friend> query = new BmobQuery<>();
        MyUser user =BmobUser.getCurrentUser(getContext(), MyUser.class);
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(getContext(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(0, "暂无联系人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    /**
     * 删除好友
     * @param f
     * @param listener
     */
    public void deleteFriend(Friend f,DeleteListener listener){
        Friend friend =new Friend();
        friend.delete(getContext(),f.getObjectId(),listener);
    }
}
