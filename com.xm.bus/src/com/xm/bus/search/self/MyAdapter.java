package com.xm.bus.search.self;

import com.xm.bus.R;
import com.xm.bus.search.model.ArrivalInfo;
import com.xm.bus.search.utils.HtmlLineParse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	
	private TextView tv_current_line=null;
	private TextView tv_current_stop=null;
	private TextView tv_next_stop_info=null;//��һվ��վ��Ϣ
	private TextView tv_next_bus_info=null;//��һ����������Ϣ
	private ImageView bus_stop_image=null;
	
	public MyAdapter(){}
	public MyAdapter(Context context){
		this.context=context;
		inflater=LayoutInflater.from(this.context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//convertView=LayoutInflater.from(this.context).inflate(R.layout.a, null);
		View view=convertView;
		try{
			ArrivalInfo infos=HtmlLineParse.arrivalInfo;
			convertView=inflater.inflate(R.layout.arrival_item, null);
			tv_current_line=(TextView) convertView.findViewById(R.id.tv_current_line);
			tv_current_stop=(TextView) convertView.findViewById(R.id.tv_current_stop);
			tv_next_stop_info=(TextView) convertView.findViewById(R.id.tv_next_stop_info);
			tv_next_bus_info=(TextView) convertView.findViewById(R.id.tv_next_bus_info);
			bus_stop_image=(ImageView) convertView.findViewById(R.id.bus_stop_image);
			
			bus_stop_image.setImageBitmap(infos.getPic());
			
			tv_current_line.setText("��ǰ��·��"+infos.getCurrLine());
			tv_current_stop.setText("��ǰվ�㣺"+infos.getCurrentStation());
			
			//��վ��Ϣ
			if(infos.getStopsNum().equals("0")&&infos.getKilometers().equals("0")){
				tv_next_stop_info.setText("�Ѿ���վ��");
			}else if(!infos.getStopsNum().equals("")&&!infos.getKilometers().equals("")){
				tv_next_stop_info.setText("����վ�㻹��"+infos.getStopsNum()+"վԼ"+infos.getKilometers()+"��վ");
			}else{
				tv_next_stop_info.setVisibility(View.GONE);
			}
			//��һ���������ƻ�
			if(infos.getNextTime().equals("")){
				tv_next_bus_info.setVisibility(View.GONE);
			}else{
				tv_next_bus_info.setText(infos.getNextTime());
			}
		}catch(Exception e){
			return view;
		}
		return convertView;
	}

}