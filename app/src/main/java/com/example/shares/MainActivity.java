package com.example.shares;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shares.db.NewFriendManager;
import com.example.shares.event.RefreshEvent;
import com.example.shares.model.Error;
import com.example.shares.model.MyUser;
import com.example.shares.util.ActivityCollector;
import com.example.shares.util.IMMLeaks;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cn.bmob.v3.c.darkness.log;
import static com.example.shares.OverlappingSoundFragment.home;

public class MainActivity extends FragmentActivity implements ObseverListener {
    private ViewPager viewPager;
    public static RadioGroup radioGroup;
    private RadioButton rbMainPage,rbOverlappingSound,rbTopic,rbNews;
    private long exitTime = 0;
    public static DrawerLayout mDrawerLayout;
    CircleImageView avater;
    TextView navUserName;
    public static MyUser user ;
    public static MyDatabaseHelper dbHelper;
    private String path;
    public static Bitmap avaterBitmap=null;
    public static final int CHANGE_NICK=1;
    public static final int CHOOSE_PHOTO =2;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    public static Map<String,Boolean> checkboxMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDatabaseHelper(MainActivity.this, "User.db", null, 1);
        uploadLog();
        user = BmobUser.getCurrentUser(MyApplication.getContext(),MyUser.class);
        mDrawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView=(NavigationView)findViewById(R.id.nav_view);
        View headerView=navView.getHeaderView(0);
        navUserName=(TextView)headerView.findViewById(R.id.nav_username);
        avater=(CircleImageView) headerView.findViewById(R.id.icon_image) ;
        if (user.getaPath()!=null)
            Glide.with(this).load(user.getaPath()).into(avater);
        else
            avater.setImageResource(R.mipmap.default_head);
        avater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getaPath()!=null) {
                    /*Intent intent = new Intent(MainActivity.this, ImageShowerActivity.class);
                    intent.putExtra("picture",user.getaPath());
                    startActivity(intent);*/
                    Intent intent=new Intent(MainActivity.this,MapActivity.class);
                    startActivity(intent);
                }
            }
        });
        initView();
        connect();
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver=new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);
        navView.setCheckedItem(R.id.nav_setting);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_setting:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_change_nick:
                        Intent changeNickIntent=new Intent(MainActivity.this,ChangeNameActivity.class);
                        startActivityForResult(changeNickIntent,CHANGE_NICK);
                        break;
                    case R.id.nav_friend_list:
                        Intent friendIntent = new Intent(MainActivity.this,ContactActivity.class);
                        startActivity(friendIntent);
                        break;
                    case R.id.nav_quit:
                        BmobUser.logOut(MainActivity.this);   //清除缓存用户对象
                        BmobIM.getInstance().disConnect();
                        SQLiteDatabase db=dbHelper.getWritableDatabase();
                        db.delete("User",null,null);
                        avaterBitmap=null;
                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        finish();
                        startActivity(intent);
                        break;
                    /*case R.id.nav_test:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OkHttpClient client=new OkHttpClient();
                                    String getMessage="/learn/info/get";
                                    Request request = new Request.Builder()
                                            .url("http://" +getMessage)
                                            .build();
                                    Response response=client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithJSONObject(responseData);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;*/
                    case R.id.nav_change_avater:
                        if(ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }
                        else{
                            openAlbum();
                        }
                        break;
                    case R.id.nav_check_update:
                        BmobUpdateAgent.update(MainActivity.this);
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                        break;
                }

                return true;
            }
        });

        MyUser user = BmobUser.getCurrentUser(this,MyUser.class);

        navUserName.setText(user.getNickname());
        if(avaterBitmap==null) {
            avater.setImageResource(R.mipmap.default_head);
        }
        else avater.setImageBitmap(avaterBitmap);

    }



    private void initView(){
        /**
         * 底部导航栏
         *  RadioGroup 部分
         */
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        rbMainPage=(RadioButton)findViewById(R.id.rb_main_page);
        rbOverlappingSound=(RadioButton)findViewById(R.id.rb_overlapping_sound);
        rbTopic=(RadioButton)findViewById(R.id.rb_topic) ;
        rbNews=(RadioButton)findViewById(R.id.rb_news);
        //rbMainPage.setTextColor(Color.parseColor("#00FF7F"));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb_main_page:
                        viewPager.setCurrentItem(0,false);
                        break;
                    case R.id.rb_overlapping_sound:
                        viewPager.setCurrentItem(1,false);
                        home.setImageBitmap(avaterBitmap);
                        break;
                    case R.id.rb_topic:
                        viewPager.setCurrentItem(2,false);
                        break;
                    case R.id.rb_news:
                        viewPager.setCurrentItem(3,false);
                        break;
                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        MainPageFragment mainPageFragment = new MainPageFragment();
        OverlappingSoundFragment overlappingSoundFragment = new OverlappingSoundFragment();
        TopicFragment topicFragment = new TopicFragment();
        NewsFragment newsFragment = new NewsFragment();
        List<Fragment> alFragment = new ArrayList<Fragment>();
        alFragment.add(mainPageFragment);
        alFragment.add(overlappingSoundFragment);
        alFragment.add(topicFragment);
        alFragment.add(newsFragment);
        //ViewPager设置适配器
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), alFragment));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);
        //ViewPager页面切换监听
        Intent intent=getIntent();
        int id=intent.getIntExtra("grxx",-1);
        if(id==1){
            //rbMainPage.setTextColor(Color.parseColor("#B0B0B0"));
            radioGroup.check(R.id.rb_overlapping_sound);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.rb_main_page);
                        break;
                    case 1:
                        radioGroup.check(R.id.rb_overlapping_sound);
                        break;
                    case 2:
                        radioGroup.check(R.id.rb_topic);
                        break;
                    case 3:
                        radioGroup.check(R.id.rb_news);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else
                    Toast.makeText(MainActivity.this, "You denied the permission", Toast.LENGTH_SHORT).show();
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
                        handleImageOnKitKat(data);

                    }
                    else
                        handleImageBeforeKitKat(data);
                }
                break;
            case CHANGE_NICK:
                if(resultCode==RESULT_OK){
                    String newNick=data.getStringExtra("newNick");
                    navUserName.setText(newNick);
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(MainActivity.this,uri)){
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


        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath =getImagePath(uri,null);
        displayImage(imagePath);
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
    private void displayImage(final String imagePath){
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            avater.setImageBitmap(bitmap);
            avaterBitmap=bitmap;
            updateAvater(imagePath);
        }
        else Toast.makeText(MainActivity.this, "failed to get image", Toast.LENGTH_SHORT).show();
    }
    public  void updateAvater(final String imagePath){
        final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("正在更新数据");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final BmobFile avaterFile=new BmobFile(new File(imagePath));
                avaterFile.upload(MainActivity.this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        user.setaPath(avaterFile.getFileUrl(MainActivity.this));
                        user.setAvatar(avaterFile);
                        user.update(MainActivity.this, user.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    @Override
                    public void onProgress(Integer value) {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "上传失败"+s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void connect(){

        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i("connect success");
                    EventBus.getDefault().post(new RefreshEvent());
                } else {
                    Logger.e(e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });

        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                if(status.getCode()==1){
                    Toast.makeText(MainActivity.this, "网络开了点小差，正在尝试重连", Toast.LENGTH_SHORT).show();
                }else if(status.getCode()==2){
                    Toast.makeText(MainActivity.this, "当前网络状况良好", Toast.LENGTH_SHORT).show();
                }else if(status.getCode()==0){
                    Toast.makeText(MainActivity.this, "与网络连接中断", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(MainActivity.this, status.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示小红点
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject=new JSONObject(jsonData);
            final String code=jsonObject.getString("code");
            final String message=jsonObject.getString("message");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "code:"+code+",message:"+message, Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void uploadLog(){

        SharedPreferences preferences=getSharedPreferences("log",MODE_PRIVATE);
        Boolean hasLog=preferences.getBoolean("hasLog",Boolean.FALSE);
        if(Boolean.TRUE.equals(hasLog)){
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("检测到你的设备最近发生过异常，正在将异常日志发送给调试人员\n" +
                    "请放心，该日志不会包含您的隐私信息");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();

            String fileName=preferences.getString("file","");
            final BmobFile file=new BmobFile(new File(fileName));
            file.upload(MainActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    Error error = new Error(file.getFileUrl(MainActivity.this));
                    error.save(MainActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Logger.i("success");
                            SharedPreferences.Editor editor=getSharedPreferences("log",MODE_PRIVATE).edit();
                            editor.putBoolean("hasLog",Boolean.FALSE);
                            editor.apply();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Logger.i(s);
                        }
                    });
                }

                @Override
                public void onFailure(int i, String s) {
                    Logger.i(s);
                }
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        unregisterReceiver(networkChangeReceiver);
    }

    class NetworkChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(
                    context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null&&networkInfo.isAvailable()){
                connect();
            }
        }
    }


    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        log("---主页接收到自定义消息---");
        checkRedPoint();
    }

    private void checkRedPoint(){
        int count = (int)BmobIM.getInstance().getAllUnReadCount();
        /*if(count>0){
            iv_conversation_tips.setVisibility(View.VISIBLE);
        }else{
            iv_conversation_tips.setVisibility(View.GONE);
        }*/
        //是否有好友添加的请求
        if(NewFriendManager.getInstance(this).hasNewFriendInvitation()){
            //iv_contact_tips.setVisibility(View.VISIBLE);
        }else{
            //iv_contact_tips.setVisibility(View.GONE);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawers();
            }
            else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    ActivityCollector.finishAll();
                    System.exit(0);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
