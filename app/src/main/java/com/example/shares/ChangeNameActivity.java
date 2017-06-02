package com.example.shares;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shares.model.MyUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.shares.MainActivity.user;

public class ChangeNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        Toolbar toolbar=(Toolbar)findViewById(R.id.change_nickname_toolbar);
        final EditText change_nickname=(EditText)findViewById(R.id.change_nickname);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add:
                        final String newnick=change_nickname.getText().toString();
                        user = BmobUser.getCurrentUser(ChangeNameActivity.this,MyUser.class);
                        if(newnick.equals("")){
                            Toast.makeText(ChangeNameActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            user.setNickname(newnick);
                            user.update(ChangeNameActivity.this, user.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(ChangeNameActivity.this, "昵称修改成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ChangeNameActivity.this, MainActivity.class);
                                    intent.putExtra("newNick", newnick);
                                    setResult(RESULT_OK,intent);

                                    finish();

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(ChangeNameActivity.this, s, Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }

                }
                return true;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_change,menu);
        return true;

    }
}
