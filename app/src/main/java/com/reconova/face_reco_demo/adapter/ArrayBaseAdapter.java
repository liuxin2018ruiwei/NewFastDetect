package com.reconova.face_reco_demo.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ArrayBaseAdapter<T> extends BaseAdapter{
	
	protected List<T> mDatas;
	
	protected  ArrayBaseAdapter(List<T> datas) {
		mDatas = datas;
	}
	
	protected abstract LayoutInflater getLayoutInflater();
	

	@Override
	public int getCount() {
		return (mDatas == null ? 0 : mDatas.size());
		
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
