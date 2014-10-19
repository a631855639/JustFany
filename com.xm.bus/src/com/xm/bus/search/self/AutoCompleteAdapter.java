package com.xm.bus.search.self;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable{
	private Context context;
	private ArrayFilter mFilter;
	private ArrayList<String> mOriginalValues;//所有的Item
	private List<String> mObjects;//过滤后的item  
	private final Object mLock = new Object();   
	private int maxMatch;//最多显示多少个选项,负数表示全部 
	
	
	public AutoCompleteAdapter(Context context,List<String> mObjects,int maxMatch){
		this.context=context;
		this.mObjects=mObjects;
		this.maxMatch=maxMatch;
	}
	public AutoCompleteAdapter(Context context,String[] mObjects,int maxMatch){
		this.context=context;
		this.mObjects=Arrays.asList(mObjects);
		this.maxMatch=maxMatch;
	}
	@Override
	public int getCount() {
		if(maxMatch<0||mObjects.size()<maxMatch){//maxMatch负数返回全部，否则返回maxMatch个数
			return mObjects.size();
		}
		return maxMatch;
	}

	@Override
	public Object getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if(view==null){
			holder=new ViewHolder();
			LayoutInflater inflater=LayoutInflater.from(context);
			view=inflater.inflate(android.R.layout.simple_dropdown_item_1line, null);
			holder.textView=(TextView) view.findViewById(android.R.id.text1);
			holder.textView.setTextColor(context.getResources().getColor(android.R.color.background_dark));
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		holder.textView.setText(mObjects.get(position));
		return view;
	}
	
	private class ViewHolder{
		TextView textView;
	}

	@Override
	public Filter getFilter() {
		if(mFilter==null){
			mFilter=new ArrayFilter();
		}
		return mFilter;
	}
	
	private class ArrayFilter extends Filter{
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results=new FilterResults();
			if(mOriginalValues==null){
				synchronized (mLock) {
					mOriginalValues=new ArrayList<String>(mObjects);
				}
			}
			
			if(prefix==null||prefix.equals("")){
				ArrayList<String> list;
				synchronized (mLock) {
					list=new ArrayList<String>(mOriginalValues);
				}
				results.values=list;
				//设置检索结果个数
				if(maxMatch<0){//maxMatch负数返回全部检索结果，否则返回maxMatch个数
					results.count=list.size();
				}else{
					results.count=maxMatch;
				}
			}else{
				 String prefixString = prefix.toString().toLowerCase();
				 
				 ArrayList<String> values;
				 synchronized (mLock) {
					 values=new ArrayList<String>(mOriginalValues);
				}
				 final int count=values.size();
				 final ArrayList<String> newValues = new ArrayList<String>();
				 for(int i=0;i<count;i++){
					 final String value=values.get(i);
					 final String valueText=value.toString().toLowerCase();
					 if(valueText.startsWith(prefixString)){
						 newValues.add(value);
					 }
					 
					 if(maxMatch>0){//当maxMatch非负时，检索结果超过maxMatch时立即停止检索
						if(newValues.size()>maxMatch-1){    
							 break;     
						}     
					}     

				 }
				 results.values = newValues;
	             results.count = newValues.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mObjects = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
		}
		
		
	}

}
