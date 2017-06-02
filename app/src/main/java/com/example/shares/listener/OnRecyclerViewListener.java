package com.example.shares.listener;

import android.view.MotionEvent;

/**为RecycleView添加点击事件
 * @author
 * @project OnRecyclerViewListener
 * @date
 */
public interface OnRecyclerViewListener {
    void onItemClick(int position);
    boolean onItemLongClick(int position);
}
