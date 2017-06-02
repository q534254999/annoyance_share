package com.example.shares;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shares.base.IMutlipleItem;
import com.example.shares.listener.OnRecyclerViewListener;
import com.example.shares.model.Friend;
import com.example.shares.model.MyUser;
import com.example.shares.util.PopupList;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;

import static com.example.shares.MainActivity.CHANGE_NICK;
import static com.example.shares.MainActivity.CHOOSE_PHOTO;
import static com.example.shares.MainActivity.user;

public class ChatActivity extends AppCompatActivity implements MessageListHandler,ObseverListener {


    MyUser anotherUser;
    BmobIMUserInfo info;
    private Uri imageUri;
    LinearLayout ll_chat;
    //ImageView iv_record;
    RecyclerView rc_view;
    public static final int TAKE_PHOTO = 1;
    EditText edit_msg;
    private Drawable[] drawable_Anims;// 话筒动画
    private List<String> popupMenuItemList ;//气泡
    BmobRecordManager recordManager;
    LinearLayout layout_add;
    LinearLayout layout_more;
    LinearLayout layout_emo;
    Button btn_chat_add;
    ChatAdapter adapter;
    Button btn_chat_emo;
    Button btn_chat_voice;
    Button btn_chat_keyboard;
    Button btn_chat_send;
    TextView tv_picture;
    TextView tv_camera;
    TextView tv_chat_toolbar;
    Toolbar chat_toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    protected LinearLayoutManager layoutManager;
    BmobIMConversation c;
    String path;
    private final int SCROLLTOBOTTOM=1;

    private Handler mHandler =new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case SCROLLTOBOTTOM:
                    scrollToBottom();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bundle=new Bundle();
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            bundle = getIntent().getBundleExtra(getPackageName());
        else
            bundle=null;

        c= BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) bundle.getSerializable("c"));


        bindlayout();

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
        initBottomView();
    }

    private void bindlayout(){
        ll_chat=(LinearLayout) findViewById(R.id.ll_chat);
        rc_view=(RecyclerView)findViewById(R.id.msg_recyclerview);
        edit_msg=(EditText)findViewById(R.id.edit_msg);
        btn_chat_add=(Button)findViewById(R.id.btn_chat_add);
        btn_chat_emo=(Button)findViewById(R.id.btn_chat_emo);
        btn_chat_keyboard=(Button)findViewById(R.id.btn_chat_keyboard);
        btn_chat_send=(Button)findViewById(R.id.btn_chat_send) ;
        btn_chat_voice=(Button)findViewById(R.id.btn_chat_voice);
        layout_add=(LinearLayout)findViewById(R.id.layout_add);
        layout_emo=(LinearLayout)findViewById(R.id.layout_emo);
        layout_more=(LinearLayout)findViewById(R.id.layout_more) ;
        tv_picture=(TextView)findViewById(R.id.tv_picture) ;
        tv_camera=(TextView)findViewById(R.id.tv_camera);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.chat_refresh);
        swipeRefreshLayout.setEnabled(true);
        chat_toolbar=(Toolbar)findViewById(R.id.chat_toolbar) ;
        tv_chat_toolbar=(TextView)findViewById(R.id.tv_chat_toolbar);
        tv_chat_toolbar.setText(c.getConversationTitle());
        chat_toolbar.setTitle("");
        setSupportActionBar(chat_toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        layoutManager=new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        adapter=new ChatAdapter(this,c);
        rc_view.setAdapter(adapter);
        setupUI(ll_chat);
        ll_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_chat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                swipeRefreshLayout.setRefreshing(true);
                queryMessages(null);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });
        btn_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });
        btn_chat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == View.VISIBLE) {
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
            }
        });
        tv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChatActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ChatActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else{
                    openAlbum();
                }
            }
        });
        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(ChatActivity.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                }else
                    imageUri=Uri.fromFile(outputImage);
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        edit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
            }
        });

        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {


            @Override
            public void onItemClick(int position) {
                Logger.i("" + position);
            }

            @Override
            public boolean onItemLongClick(final int position) {
                return true;
            }
        });
    }


    private void initBottomView(){
        edit_msg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_UP){
                    mHandler.sendEmptyMessageDelayed(SCROLLTOBOTTOM,200);
                }
                return false;
            }
        });
        edit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
            }
        });
        edit_msg.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    private void sendMessage(){
        String text=edit_msg.getText().toString();
        if(TextUtils.isEmpty(text.trim())){
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobIMTextMessage msg =new BmobIMTextMessage();
        msg.setContent(text);
        //可设置额外信息
        Map<String,Object> map =new HashMap<>();
        map.put("level", "1");//随意增加信息
        msg.setExtraMap(map);
        c.sendMessage(msg, listener);
    }
    public MessageSendListener listener =new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            Logger.i("onProgress："+value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            edit_msg.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            edit_msg.setText("");
            scrollToBottom();
            if (e != null) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }else
                Logger.d("success");
        }
    };

    public void queryMessages(BmobIMMessage msg){
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                swipeRefreshLayout.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, e.getMessage()+"("+e.getErrorCode()+")", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        Logger.i("聊天页面接收到消息：" + list.size());
        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i=0;i<list.size();i++){
            addMessage2Chat(list.get(i));
        }
    }

//    /**接收到聊天消息
//     * @param event
//     */
//    @Subscribe
//    public void onEventMainThread(MessageEvent event){
//        addMessage2Chat(event);
//    }
//
//    @Subscribe
//    public void onEventMainThread(OfflineMessageEvent event){
//        Map<String,List<MessageEvent>> map =event.getEventMap();
//        if(map!=null&&map.size()>0){
//            //只获取当前聊天对象的离线消息
//            List<MessageEvent> list = map.get(c.getConversationId());
//            if(list!=null && list.size()>0){
//                for (int i=0;i<list.size();i++){
//                    addMessage2Chat(list.get(i));
//                }
//            }
//        }
//    }

    /**添加消息到聊天界面中
     * @param event
     */
    private void addMessage2Chat(MessageEvent event){
        BmobIMMessage msg =event.getMessage();
        if(c!=null && event!=null && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()){//并且不为暂态消息
            if(adapter.findPosition(msg)<0){//如果未添加到界面中
                adapter.addMessage(msg);
                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
            }
            scrollToBottom();
        }else{
            Logger.i("不是与当前聊天对象的消息");
        }
    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage(){
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if(cache.size()>0){
            int size =cache.size();
            for(int i=0;i<size;i++){
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    public void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else
                    Toast.makeText(ChatActivity.this, "You denied the permission", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        imageUri=data.getData();
                        handleImageOnKitKat(imageUri);

                    }
                    else{
                        imageUri=data.getData();
                        handleImageBeforeKitKat(imageUri);
                    }

                }
                break;
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(resultCode==RESULT_OK){
                        if(Build.VERSION.SDK_INT>=19){
                            handleImageOnKitKat(imageUri);

                        }
                        else
                            handleImageBeforeKitKat(imageUri);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Uri uri){
        String imagePath=null;

        if(DocumentsContract.isDocumentUri(ChatActivity.this,uri)){
            String docId =DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {

                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content:"+
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        BmobIMImageMessage image =new BmobIMImageMessage(imagePath);
        c.sendMessage(image,listener);


    }
    private void handleImageBeforeKitKat(Uri uri){
        String imagePath =getImagePath(uri,null);
        BmobIMImageMessage image =new BmobIMImageMessage(imagePath);
        c.sendMessage(image,listener);
    }
    private String getImagePath(Uri uri,String selection){
        path =null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }






    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //清理资源
        if(recordManager!=null){
            recordManager.clear();
        }
        //更新此会话的所有消息为已读状态
        if(c!=null){
            c.updateLocalCache();
        }
        hideSoftInputView();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(ChatActivity.this.INPUT_METHOD_SERVICE);
        boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if(isOpen) {
            hideSoftInputView();
            return true;
        }else
            return super.onTouchEvent(event);
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
    public void setupUI(View view) {

        if (!(view instanceof EditText)&&!(view instanceof Button)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftInputView();
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }



}
