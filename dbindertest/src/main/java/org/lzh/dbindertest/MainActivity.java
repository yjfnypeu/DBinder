package org.lzh.dbindertest;

import android.app.Activity;
import android.os.Bundle;

import org.lzh.dbindertest.bean.User;
import org.lzh.dbindertest.bean.User_Bind;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User user = User_Bind.attach(getWindow().getDecorView());
        user.setUsername("My name is zhang san");
        user.setPassword("My password do not talk to you");
    }
}
