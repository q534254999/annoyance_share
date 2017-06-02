package com.example.shares;

import android.content.Context;
import android.view.View;

import com.example.shares.base.BaseRecyclerAdapter;
import com.example.shares.base.BaseRecyclerHolder;
import com.example.shares.base.IMutlipleItem;
import com.example.shares.db.NewFriendManager;
import com.example.shares.model.Friend;
import com.example.shares.model.MyUser;

import java.util.Collection;



/**联系人
 * 一种简洁的Adapter实现方式，可用于多种Item布局的recycleView实现，不用再写ViewHolder啦
 * @author :
 * @project:ContactNewAdapter
 * @date :
 */
public class ContactAdapter extends BaseRecyclerAdapter<Friend> {

    public static final int TYPE_NEW_FRIEND = 0;
    public static final int TYPE_ITEM = 1;

    public ContactAdapter(Context context, IMutlipleItem<Friend> items, Collection<Friend> datas) {
        super(context,items,datas);
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, Friend friend, int position) {
        if(holder.layoutId==R.layout.item_contact){
            MyUser user =friend.getFriendUser();
            //好友头像
            holder.setImageView(user == null ? null : user.getaPath(), R.mipmap.head, R.id.iv_recent_avatar);
            //好友名称
            holder.setText(R.id.tv_recent_name,user==null?"未知":user.getUsername());
        }else if(holder.layoutId==R.layout.header_new_friend){
            if(NewFriendManager.getInstance(context).hasNewFriendInvitation()){
                holder.setVisible(R.id.iv_msg_tips,View.VISIBLE);
            }else{
                holder.setVisible(R.id.iv_msg_tips, View.GONE);
            }
        }
    }


}
