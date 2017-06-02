package com.example.shares.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.shares.ChatActivity;
import com.example.shares.ContactActivity;
import com.example.shares.MyApplication;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.util.PopupList;

import java.util.ArrayList;
import java.util.List;


/**
 * 建议使用BaseRecyclerAdapter
 * @param <T>
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    public OnRecyclerViewListener onRecyclerViewListener;
    protected Context context;



    public BaseViewHolder(Context context, ViewGroup root, int layoutRes,OnRecyclerViewListener listener) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
        this.context=context;
        this.onRecyclerViewListener =listener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInputView();
                return false;
            }
        });
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public abstract void bindData(T t);

    private Toast toast;
    public void toast(final Object obj) {
        try {
            ((Activity) context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
                    toast.setText(obj.toString());
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(onRecyclerViewListener!=null){
            onRecyclerViewListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(onRecyclerViewListener!=null){

            onRecyclerViewListener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }



    /**启动指定Activity
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getContext(), target);
        if (bundle != null)
            intent.putExtra(getContext().getPackageName(), bundle);
        getContext().startActivity(intent);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (((Activity)context).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (((Activity)context).getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}