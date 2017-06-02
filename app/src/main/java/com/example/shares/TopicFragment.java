package com.example.shares;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shares.model.Topic;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class TopicFragment extends Fragment {
    private RecyclerView recyclerView;
    public static List<Topic> topicList=new ArrayList<>();
    private View view;
    private TopicAdapter topicadapter;
    private static int refresh_count=0;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view =inflater.inflate(R.layout.topic_fragment, container, false);

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.os_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorLightBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
           //     home.setImageBitmap(avaterBitmap);
                refreshTopic(1);
            }
        });
  //      if((already_refresh_annoyance!=4)||(refresh_count!=0)) {
            refreshTopic(0);
    //    }
        recyclerView=(RecyclerView)view.findViewById(R.id.topic_info_recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
      //  refreshTopic();
        LinearLayoutManager layoutManager =new LinearLayoutManager(view.getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        topicadapter=new TopicAdapter(topicList);
        recyclerView.setAdapter(topicadapter);

        return view;
    }
    private void refreshTopic(final int flag){
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("正在获取数据");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        topicList.clear();
        BmobQuery<Topic> query=new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(view.getContext(), new FindListener<Topic>() {
            @Override
            public void onSuccess(List<Topic> lists) {
                for (Topic list:lists) {
                    Log.i("test",list.getContentTitle());
                }
               topicList.addAll(lists);

                topicadapter.notifyDataSetChanged();
                progressDialog.dismiss();
                if(flag==1)
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                progressDialog.dismiss();
                Toast.makeText(view.getContext(), s, Toast.LENGTH_SHORT).show();
                replaceFragment(new BlankFragment());
                if(flag==1)
                    swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.topic_fragment,fragment);
        transaction.commit();
    }

}
