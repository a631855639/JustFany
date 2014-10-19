package com.xm.bus.common.ui;

import java.util.ArrayList;
import java.util.List;

import com.xm.bus.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class GuidePager extends Activity{
	private View page1,page2,page3,page4,page5;
	private ViewPager viewPager;
	private List<View> views;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//…Ë÷√»´∆¡  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.guide_layout);
		initViews();
	}
	
	private void initViews() {
		viewPager=(ViewPager) findViewById(R.id.view_pager);
		
		LayoutInflater inflater=getLayoutInflater();
		page1=inflater.inflate(R.layout.page1, null);
		page2=inflater.inflate(R.layout.page2, null);
		page3=inflater.inflate(R.layout.page3, null);
		page4=inflater.inflate(R.layout.page4, null);
		page5=inflater.inflate(R.layout.page5, null);
		Button startUsing=(Button) page5.findViewById(R.id.start);
		startUsing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		views=new ArrayList<View>();
		views.add(page1);
		views.add(page2);
		views.add(page3);
		views.add(page4);
		views.add(page5);
		viewPager.setAdapter(new GuideAdapter(views));
		viewPager.setCurrentItem(0);
	}
	@Override
	protected void onDestroy() {
		if(views!=null){
			views.clear();
			views=null;
		}
		super.onDestroy();
	}
}
