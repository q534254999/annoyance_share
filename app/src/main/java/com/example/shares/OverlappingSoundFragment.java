package com.example.shares;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shares.model.Annoyance;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;


import static com.example.shares.LoginWelcomeActivity.already_refresh_annoyance;
import static com.example.shares.MainActivity.avaterBitmap;
import static com.example.shares.MainActivity.user;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class OverlappingSoundFragment extends Fragment {
    public static List<Annoyance> annoyanceList=new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private AnnoyanceAdapter annoyanceAdapter;
    public static CircleImageView home;



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.overlappingsound_fragment, container, false);

        Toolbar toolbar=(Toolbar)view.findViewById(R.id.os_toolbar);

        home=(CircleImageView) view.findViewById(R.id.toolbar_home);
        if(user.getaPath()!=null)
            Glide.with(view.getContext()).load(user.getaPath()).placeholder(R.mipmap.default_head).into(home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.os_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorLightBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(user.getaPath()!=null)
                    Glide.with(view.getContext()).load(user.getaPath()).into(home);
                else
                    home.setImageResource(R.mipmap.default_head);
                refreshAnnoyance(1);
            }
        });

        refreshAnnoyance(0);
        RecyclerView osRecyclerView=(RecyclerView)view.findViewById(R.id.os_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        osRecyclerView.setLayoutManager(layoutManager);
        annoyanceAdapter=new AnnoyanceAdapter(annoyanceList);
        osRecyclerView.setAdapter(annoyanceAdapter);





        FloatingActionButton fab=(FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AddAnnoyaceActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }
    private void refreshAnnoyance(final int flag){
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("正在获取数据");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        annoyanceList.clear();
        BmobQuery<Annoyance> query=new BmobQuery<>();
        query.order("-createdAt");
        query.include("author");
        query.findObjects(view.getContext(), new FindListener<Annoyance>() {
            @Override
            public void onSuccess(List<Annoyance> lists) {
                annoyanceList.addAll(lists);

                annoyanceAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onError(int i, String s) {
                progressDialog.dismiss();
                Toast.makeText(view.getContext(), s, Toast.LENGTH_SHORT).show();

            }
        });
        if(flag==1)
            swipeRefreshLayout.setRefreshing(false);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.annoyance_fragment,fragment);
        transaction.commit();
    }
    private Annoyance downloadFile(final Annoyance annoyance){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if(annoyance.getaPath()!=null) {

                        URL myUrl = new URL(annoyance.getaPath());
                        HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                        connection.setConnectTimeout(5000);
                        connection.connect();
                        InputStream is = connection.getInputStream();
                        annoyance.setAvater(BitmapFactory.decodeStream(is));


                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        return annoyance;
    }
}
