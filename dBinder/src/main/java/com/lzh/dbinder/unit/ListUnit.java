package com.lzh.dbinder.unit;

import java.util.List;

import android.widget.AbsListView;

import com.lzh.dbinder.adapter.BaseListAdapter;

/**
 * @author Administrator
 *
 * @param <F>
 * @param <V>
 */
public class ListUnit<F,V extends AbsListView> implements IBindUnit<List<F>,V>{

	private V mListView;
	private BaseListAdapter<F> mAdapter;
	
	public ListUnit(V list) {
		this.mListView = list;
	}
	
	public void setAdapter(BaseListAdapter<F> adapter) {
		this.mAdapter = adapter;
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	public List<F> unBindData() {
		return mAdapter.getList();
	}

	@Override
	public void doBind(List<F> e) {
		mAdapter.setList(e);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public V unBindView() {
		return mListView;
	}

	@Override
	public void doBindView(V t) {
		this.mListView = t;
	}

}
