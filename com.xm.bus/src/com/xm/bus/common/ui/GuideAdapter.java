package com.xm.bus.common.ui;


import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GuideAdapter extends PagerAdapter{
	List<View> views;
	
	public GuideAdapter(){}
	public GuideAdapter(List<View> views){
		this.views=views;
	}
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view==obj;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}
	
	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position),0);
		return views.get(position);
	}
}
