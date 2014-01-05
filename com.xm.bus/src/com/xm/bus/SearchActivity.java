package com.xm.bus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.xm.bus.common.MyApplication;
import com.xm.bus.search.change.DestinationStopActivity;
import com.xm.bus.search.change.PlansLineActivity;
import com.xm.bus.search.change.SourceStopActivity;
import com.xm.bus.search.line.SelectLineActivity;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.stop.RelationStopActivity;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlChangeParse;
import com.xm.bus.search.utils.HtmlLineParse;
import com.xm.bus.search.utils.HtmlStopParse;
import com.xm.bus.search.utils.NetworkCheck;
import java.io.Serializable;

public class SearchActivity extends Fragment implements OnClickListener {
	private boolean isNetworkConnection = false;
	private String line = "";
	private MyApplication myApp;
	private MyThread queryThread = null;
	private String stop = "";
	private String to = "";
	private Button bt_change_query = null;
	private Button bt_line_query = null;
	private Button bt_stop_query = null;
	private EditText et_change_from = null;
	private EditText et_change_to = null;
	private EditText et_line = null;
	private EditText et_stop = null;
	private String from = "";

	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup paramViewGroup, Bundle paramBundle) {
		
		View view = inflater.inflate(R.layout.search_main, paramViewGroup,false);
		this.isNetworkConnection = NetworkCheck.isConnection(getActivity());
		ExitApplication.getInstance().addActivity(getActivity());
		this.myApp = ((MyApplication) getActivity().getApplication());
		this.et_line = ((EditText) view.findViewById(R.id.et_line));
		this.bt_line_query = ((Button) view.findViewById(R.id.bt_line_query));
		this.bt_line_query.setOnClickListener(this);
		this.et_stop = ((EditText) view.findViewById(R.id.et_stop));
		this.bt_stop_query = ((Button) view.findViewById(R.id.bt_stop_query));
		this.bt_stop_query.setOnClickListener(this);
		this.et_change_from = ((EditText) view.findViewById(R.id.et_change_from));
		this.et_change_to = ((EditText) view.findViewById(R.id.et_change_to));
		this.bt_change_query = ((Button) view.findViewById(R.id.bt_change_query));
		this.bt_change_query.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if(isNetworkConnection){
		switch (v.getId()) {
		case R.id.bt_line_query://按线路查询
			line=et_line.getText().toString().trim();
			queryThread=new MyThread("line");
			queryThread.start();
			LoadingDialog.getInstance(getActivity()).show();
			break;
		case R.id.bt_stop_query://按站点查询
			stop=et_stop.getText().toString().trim();
			queryThread=new MyThread("stop");
			queryThread.start();
			LoadingDialog.getInstance(getActivity()).show();
			break;
		case R.id.bt_change_query://
			from=et_change_from.getText().toString().trim();
			to=et_change_to.getText().toString().trim();
			myApp.setFrom(from);
			myApp.setTo(to);
			queryThread=new MyThread("change");
			queryThread.start();
			LoadingDialog.getInstance(getActivity()).show();
		}
		}else{
			Toast.makeText(getActivity(), "请确保手机能上网", Toast.LENGTH_LONG).show();
		}
	}
	//加载线程
	class MyThread extends Thread{
		private String type;
		private STATE result=null;
		public MyThread(String type){
			this.type=type;
		}
		@Override
		public void run() {
			while(!queryThread.isInterrupted()){
				Message msg=Message.obtain();
				if(type.equals("line")){
					result=HtmlLineParse.getLine(line);
					msg.arg1=1;
				}else if(type.equals("stop")){
					result=HtmlStopParse.getRelationStop(stop);
					msg.arg1=2;
				}else if(type.equals("change")){
					msg.arg2=1;
					result=HtmlChangeParse.getSourceStop(from);
					if(HtmlChangeParse.sourceStopList.size()==1){//出发地相关站点只有一个
						myApp.setFrom(HtmlChangeParse.sourceStopList.get(0).get("relationStopName"));
						msg.arg2=2;
						result=HtmlChangeParse.getDestinationStop(to);
						if(HtmlChangeParse.destinationStopList.size()==1){//目的地相关站点只有一个
							myApp.setTo(HtmlChangeParse.destinationStopList.get(0).get("relationStopName"));
							msg.arg2=3;
							result=HtmlChangeParse.getChangePlans(myApp.getFrom(), myApp.getTo());
						}
					}
					msg.arg1=3;
				}
				//if(result!=STATE.NetworkError){
					queryThread.interrupt();
					msg.obj=result;
					handler.sendMessage(msg);
				//}
				
			}
		}
	}
	//处理线程加载结果
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			STATE result=(STATE) msg.obj;
			int type=msg.arg1;
			int type2=msg.arg2;
			LoadingDialog.getInstance(getActivity()).dismiss();
			if(result==STATE.ServerMaintenance){
				Toast.makeText(getActivity(), "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
			}else if(result==STATE.LineNotExistError){
				Toast.makeText(getActivity(), "你所查询的线路不存在，请重新输入", Toast.LENGTH_SHORT).show();
			}else if(result==STATE.InputError){
				Toast.makeText(getActivity(), "你输入的线路或站点格式不对，请重新输入", Toast.LENGTH_SHORT).show();
			}else if(result==STATE.NetworkError){
				Toast.makeText(getActivity(), "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
			}else if(result==STATE.Success){
				Intent intent=new Intent();
				if(type==1){
					intent.putExtra("line", (Serializable)HtmlLineParse.lineList);
					intent.setClass(getActivity(), SelectLineActivity.class);
				}else if(type==2){
					intent.putExtra("relationStopList", (Serializable)HtmlStopParse.relationStopList);
					intent.setClass(getActivity(), RelationStopActivity.class);
				}else if(type==3){
					if(type2==1){
						intent.putExtra("sourceStopList", (Serializable)HtmlChangeParse.sourceStopList);
						intent.setClass(getActivity(), SourceStopActivity.class);
					}else if(type2==2){
						intent.putExtra("destinationStopList", (Serializable)HtmlChangeParse.destinationStopList);
						intent.setClass(getActivity(), DestinationStopActivity.class);
					}else if(type2==3){
						intent.putExtra("planLineList", (Serializable)HtmlChangeParse.planLineList);
						intent.setClass(getActivity(), PlansLineActivity.class);
					}
				}
				startActivity(intent);
			}
		}
	};
}