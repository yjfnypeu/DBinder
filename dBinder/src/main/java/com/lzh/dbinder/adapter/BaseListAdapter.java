package com.lzh.dbinder.adapter;

import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

	private List<T> list = null;
	
	public BaseListAdapter(List<T> list) {
		this.list = list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public List<T> getList() {
		return list;
	}
	
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}
	
	@Override
	public T getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
}
