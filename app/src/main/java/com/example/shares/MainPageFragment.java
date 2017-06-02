package com.example.shares;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.shares.MainActivity.radioGroup;
import static com.example.shares.MainActivity.user;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class MainPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.mainpage_fragment, container, false);
        Toolbar tb_mainpage=(Toolbar)view.findViewById(R.id.tb_mainpage);
        Typeface mtypeface= Typeface.createFromAsset(getActivity().getAssets(),"msyhbd.ttf");
        TextView tv_appname=(TextView)view.findViewById(R.id.tv_appname);
        tv_appname.setTypeface(mtypeface);
        CircleImageView iv_avatar=(CircleImageView) view.findViewById(R.id.iv_avatar);
        if(user!=null&&user.getaPath()!=null)
            Glide.with(getContext()).load(user.getaPath()).into(iv_avatar);
        else
            iv_avatar.setImageResource(R.mipmap.default_head);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        TextView tv_topic=(TextView)view.findViewById(R.id.tv_topic);
        tv_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.check(R.id.rb_topic);
            }
        });
        return view;
    }
}
