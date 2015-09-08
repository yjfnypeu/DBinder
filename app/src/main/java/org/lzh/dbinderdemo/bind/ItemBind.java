package org.lzh.dbinderdemo.bind;

import android.widget.ImageView;
import android.widget.TextView;

import com.lzh.dbinder.annotation.Bind;
import com.lzh.dbinder.unit.IBindUnit;
import com.lzh.dbinder.unit.ImageUnit;
import com.lzh.dbinder.unit.TVUnit;

import org.lzh.dbinderdemo.R;

/**
 * Created by lzh on 2015/9/7.
 */
public class ItemBind {
    @Bind(value = R.id.item_title,clz = TVUnit.class)
    private IBindUnit<String,TextView> title;
    @Bind(value = R.id.item_content,clz = TVUnit.class)
    private IBindUnit<String,TextView> content;
    @Bind(value = R.id.head,clz = ImageUnit.class)
    private IBindUnit<Integer,ImageView> head;

    public IBindUnit<String, TextView> getTitle() {
        return title;
    }

    public void setTitle(IBindUnit<String, TextView> title) {
        this.title = title;
    }

    public IBindUnit<String, TextView> getContent() {
        return content;
    }

    public void setContent(IBindUnit<String, TextView> content) {
        this.content = content;
    }

    public IBindUnit<Integer, ImageView> getHead() {
        return head;
    }

    public void setHead(IBindUnit<Integer, ImageView> head) {
        this.head = head;
    }
}
