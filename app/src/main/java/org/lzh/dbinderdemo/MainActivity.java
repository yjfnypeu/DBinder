package org.lzh.dbinderdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzh.dbinder.DBBindUtil;
import com.lzh.dbinder.adapter.BaseListAdapter;

import org.lzh.dbinderdemo.bean.ItemInfo;
import org.lzh.dbinderdemo.bind.ItemBind;
import org.lzh.dbinderdemo.bind.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserInfo info = DBBindUtil.bind(getWindow().getDecorView(),UserInfo.class);
        info.getUsername().doBind("张三");
        info.getPassword().doBind("112114116");

        List<ItemInfo> datas = generateList();
        info.getLv().setAdapter(new MyAdapter(datas));
    }

    class MyAdapter extends BaseListAdapter<ItemInfo> {

        public MyAdapter(List<ItemInfo> list) {
            super(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemBind bind = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_lv,parent,false);
                bind = DBBindUtil.bind(convertView,ItemBind.class);
                convertView.setTag(bind);
            } else {
                bind = (ItemBind)convertView.getTag();
            }
            ItemInfo info = getList().get(position);
            bind.getTitle().doBind(info.getTitle());
            bind.getContent().doBind(info.getContent());
            return convertView;
        }
    }

    private List<ItemInfo> generateList() {
        List<ItemInfo> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            ItemInfo info = new ItemInfo();
            info.setTitle("item title :" + i);
            info.setContent("item content :" + i);
            list.add(info);
        }
        return list;
    }
}
