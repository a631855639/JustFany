package com.xm.bus;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.bus.common.Constant;
import com.xm.bus.common.MyApplication;
import com.xm.bus.common.base.HtmlChangeParse;
import com.xm.bus.common.base.HtmlLineParse;
import com.xm.bus.common.base.HtmlStopParse;
import com.xm.bus.common.base.HtmlBaseParse.STATE;
import com.xm.bus.common.db.DBHelper;
import com.xm.bus.common.model.LineDetail;
import com.xm.bus.common.ui.ExitApplication;
import com.xm.bus.common.ui.LoadingDialog;
import com.xm.bus.common.utils.NetworkCheck;
import com.xm.bus.search.change.DestinationStopActivity;
import com.xm.bus.search.change.PlansLineActivity;
import com.xm.bus.search.change.SourceStopActivity;
import com.xm.bus.search.line.SelectLineActivity;
import com.xm.bus.search.self.AutoCompleteAdapter;
import com.xm.bus.search.self.AutoCompleteAndClearTextView;
import com.xm.bus.search.stop.RelationStopActivity;
import java.io.Serializable;
import java.util.Map;

public class SearchActivity extends Fragment implements OnClickListener {
	private  int show_limit;//历史记录最多显示条数
	private boolean isNetworkConnection = false;
	private String line = "";
	private MyApplication myApp;
	private MyThread queryThread = null;
	private String stop = "";
	private String to = "";
	private String from = "";
	private Button bt_change_query = null;
	private Button bt_line_query = null;
	private Button bt_stop_query = null;
	private AutoCompleteAndClearTextView et_change_from = null;
	private AutoCompleteAndClearTextView et_change_to = null;
	private AutoCompleteAndClearTextView et_line = null;
	private AutoCompleteAndClearTextView et_stop = null;
	
	
	private SharedPreferences sp_history;
	private SharedPreferences sp_setting;
	private AutoCompleteAdapter lineAdapter;
	private AutoCompleteAdapter stopAdapter;
	private AutoCompleteAdapter fromAdapter;
	private AutoCompleteAdapter toAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup paramViewGroup, Bundle paramBundle) {
		
		View view = inflater.inflate(R.layout.search_main, paramViewGroup,false);
		
		this.isNetworkConnection = NetworkCheck.isConnection(getActivity());
		ExitApplication.getInstance().addActivity(getActivity());
		sp_history=getActivity().getSharedPreferences(Constant.HISTORY_RECORDS,0 );
		sp_setting=getActivity().getSharedPreferences(Constant.SETTING,0 );
		
		show_limit=sp_history.getInt(Constant.HISTORY_SHOW_LIMIT, 5);
		String limitStr=getResources().getString(R.string.completion_hint);
		String completion_hint=String.format(limitStr, show_limit);
		
		this.myApp = ((MyApplication) getActivity().getApplication());
		this.et_line = ((AutoCompleteAndClearTextView) view.findViewById(R.id.et_line));
		et_line.setCompletionHint(completion_hint);
		this.bt_line_query = ((Button) view.findViewById(R.id.bt_line_query));
		this.bt_line_query.setOnClickListener(this);
		this.et_stop = ((AutoCompleteAndClearTextView) view.findViewById(R.id.et_stop));
		et_stop.setCompletionHint(completion_hint);
		this.bt_stop_query = ((Button) view.findViewById(R.id.bt_stop_query));
		this.bt_stop_query.setOnClickListener(this);
		this.et_change_from = ((AutoCompleteAndClearTextView) view.findViewById(R.id.et_change_from));
		et_change_from.setCompletionHint(completion_hint);
		this.et_change_to = ((AutoCompleteAndClearTextView) view.findViewById(R.id.et_change_to));
		et_change_to.setCompletionHint(completion_hint);
		this.bt_change_query = ((Button) view.findViewById(R.id.bt_change_query));
		this.bt_change_query.setOnClickListener(this);
		
		initDatas();
		return view;
	}
	
	/**
	 * 初始化历史搜索数据
	 */
	@SuppressLint("InlinedApi")
	private void initDatas(){
		String longhistory=sp_history.getString(Constant.LINE_HISTORY, "");
		String[] histories=longhistory.split(",");
		lineAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
		et_line.setAdapter(lineAdapter);
		et_line.setDropDownHeight(LayoutParams.WRAP_CONTENT);
		
		longhistory=sp_history.getString(Constant.STOP_HISTORY, "");
		histories=longhistory.split(",");
		stopAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
		et_stop.setAdapter(stopAdapter);
		et_stop.setDropDownHeight(LayoutParams.WRAP_CONTENT);
		
		longhistory=sp_history.getString(Constant.CHANGE_FROM_HISTORY, "");
		histories=longhistory.split(",");
		fromAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
		et_change_from.setAdapter(fromAdapter);
		et_change_from.setDropDownHeight(LayoutParams.WRAP_CONTENT);
		
		longhistory=sp_history.getString(Constant.CHANGE_TO_HISTORY, "");
		histories=longhistory.split(",");
		toAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
		et_change_to.setAdapter(toAdapter);
		et_change_to.setDropDownHeight(LayoutParams.WRAP_CONTENT);
		
	}
	/**
	 * 保存历史搜索记录
	 * @param values
	 */
	private void savaData(String values){
		if(values.equals("line")){
			String text=et_line.getText().toString().trim();
			String longhistory=sp_history.getString(Constant.LINE_HISTORY, "");
			if(!longhistory.contains(text+",")){
				StringBuilder sb=new StringBuilder(longhistory);
				sb.insert(0, text+",");
				sp_history.edit().putString(Constant.LINE_HISTORY, sb.toString()).commit();
			}
		}else if(values.equals("stop")){
			String text=et_stop.getText().toString().trim();
			String longhistory=sp_history.getString(Constant.STOP_HISTORY, "");
			if(!longhistory.contains(text+",")){
				StringBuilder sb=new StringBuilder(longhistory);
				sb.insert(0, text+",");
				sp_history.edit().putString(Constant.STOP_HISTORY, sb.toString()).commit();
			}
		}else if(values.equals("change")){
			String text=et_change_from.getText().toString().trim();
			String longhistory=sp_history.getString(Constant.CHANGE_FROM_HISTORY, "");
			if(!longhistory.contains(text+",")){
				StringBuilder sb=new StringBuilder(longhistory);
				sb.insert(0, text+",");
				sp_history.edit().putString(Constant.CHANGE_FROM_HISTORY, sb.toString()).commit();
			}
			text=et_change_to.getText().toString().trim();
			longhistory=sp_history.getString(Constant.CHANGE_TO_HISTORY, "");
			if(!longhistory.contains(text+",")){
				StringBuilder sb=new StringBuilder(longhistory);
				sb.insert(0, text+",");
				sp_history.edit().putString(Constant.CHANGE_TO_HISTORY, sb.toString()).commit();
			}
		}
		updateData(values);
	}
	/**
	 * 更新搜索记录
	 * @param values
	 */
	private void updateData(String values){
		if(values.equals("line")){
			String longhistory=sp_history.getString(Constant.LINE_HISTORY, "");
			String[] histories=longhistory.split(",");
			lineAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
			et_line.setAdapter(lineAdapter);
		}else if(values.equals("stop")){
			String longhistory=sp_history.getString(Constant.STOP_HISTORY, "");
			String[] histories=longhistory.split(",");
			stopAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
			et_stop.setAdapter(stopAdapter);
		}else{
			String longhistory=sp_history.getString(Constant.CHANGE_FROM_HISTORY, "");
			String[] histories=longhistory.split(",");
			fromAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
			et_change_from.setAdapter(fromAdapter);
			
			longhistory=sp_history.getString(Constant.CHANGE_TO_HISTORY, "");
			histories=longhistory.split(",");
			toAdapter=new AutoCompleteAdapter(getActivity(), histories, show_limit);
			et_change_to.setAdapter(toAdapter);
		}
	}
	@Override
	public void onClick(View v) {
		if(isNetworkConnection){
		switch (v.getId()) {
		case R.id.bt_line_query://按线路查询
			line=et_line.getText().toString().trim();
			LoadingDialog.getInstance(getActivity()).show();
			if(!DBHelper.getInstance(getActivity()).isLineExists(line)){
				//queryThread=new MyThread(LINE);
				//queryThread.start();
				//Toast.makeText(getActivity(), "联网查询", Toast.LENGTH_SHORT).show();
				//数据库中没有该线路的数据，则联网搜索线路，并将搜索结果缓存到数据库中
				new AsyncTask<String, Void, STATE>() {
					@Override
					protected STATE doInBackground(String...p) {
						return HtmlLineParse.getInstance(getActivity()).getLine(p[0]);
					}
					@Override
					protected void onPostExecute(STATE result) {
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
							savaData("line");//保存搜索记录
							DBHelper.getInstance(getActivity()).addLine(line);//搜索成功将线路保存进数据库缓存
							for (Map<String, String> map:HtmlLineParse.getInstance(getActivity()).getLineList()) {
								LineDetail lineDetail=new LineDetail(line, map.get("lineName"), "", "", "", map.get("lineUrl"));
								DBHelper.getInstance(getActivity()).addLineDetail(lineDetail);
							}
							intent.putExtra("line", (Serializable)HtmlLineParse.getInstance(getActivity()).getLineList());
							intent.setClass(getActivity(), SelectLineActivity.class);
							startActivity(intent);
						}
						super.onPostExecute(result);
					}
				}.execute(line);
			}else{
				//Toast.makeText(getActivity(), "非联网查询", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent();
				intent.putExtra("line", (Serializable)DBHelper.getInstance(getActivity()).getLineDetailListByLine(line));
				intent.setClass(getActivity(), SelectLineActivity.class);
				LoadingDialog.getInstance(getActivity()).dismiss();
				startActivity(intent);
			}
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
				/*if(type.equals("line")){
					result=HtmlLineParse.getInstance(getActivity()).getLine(line);
					msg.arg1=1;
				}else */if(type.equals("stop")){
					result=HtmlStopParse.getInstance(getActivity()).getRelationStop(stop);
					msg.arg1=2;
				}else if(type.equals("change")){
					msg.arg2=1;
					result=HtmlChangeParse.getInstance(getActivity()).getSourceStop(from);
					if(HtmlChangeParse.getInstance(getActivity()).getSourceStopList().size()==1){//出发地相关站点只有一个
						myApp.setFrom(HtmlChangeParse.getInstance(getActivity()).getSourceStopList().get(0).get("relationStopName"));
						msg.arg2=2;
						result=HtmlChangeParse.getInstance(getActivity()).getDestinationStop(to);
						if(HtmlChangeParse.getInstance(getActivity()).getDestinationStopList().size()==1){//目的地相关站点只有一个
							myApp.setTo(HtmlChangeParse.getInstance(getActivity()).getDestinationStopList().get(0).get("relationStopName"));
							msg.arg2=3;
							result=HtmlChangeParse.getInstance(getActivity()).getChangePlans(myApp.getFrom(), myApp.getTo());
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
				/*if(type==1){
					savaData("line");//保存搜索记录
					//if(!DBHelper.getInstance(getActivity()).isLineExists(line)){
						DBHelper.getInstance(getActivity()).addLine(line);//搜索成功将线路保存进数据库缓存
					//}
					for (Map<String, String> map:HtmlLineParse.getInstance(getActivity()).getLineList()) {
						LineDetail lineDetail=new LineDetail(line, map.get("lineName"), "", "", "", map.get("lineUrl"));
						//if(!DBHelper.getInstance(getActivity()).isLineDetailExists(line)){
							DBHelper.getInstance(getActivity()).addLineDetail(lineDetail);
						//}
					}
					intent.putExtra("line", (Serializable)HtmlLineParse.getInstance(getActivity()).getLineList());
					intent.setClass(getActivity(), SelectLineActivity.class);
				}else */if(type==2){
					savaData("stop");
					intent.putExtra("relationStopList", (Serializable)HtmlStopParse.getInstance(getActivity()).getRelationStopList());
					intent.setClass(getActivity(), RelationStopActivity.class);
				}else if(type==3){
					savaData("change");
					if(type2==1){//起始站不止一个
						intent.putExtra("sourceStopList", (Serializable)HtmlChangeParse.getInstance(getActivity()).getSourceStopList());
						intent.setClass(getActivity(), SourceStopActivity.class);
					}else if(type2==2){//起始站只有一个，直接打开终点站选择页面
						intent.putExtra("destinationStopList", (Serializable)HtmlChangeParse.getInstance(getActivity()).getDestinationStopList());
						intent.setClass(getActivity(), DestinationStopActivity.class);
					}else if(type2==3){//起始站和终点站都只有一个，直接打开两个站点间的乘车方案
						intent.putExtra("planLineList", (Serializable)HtmlChangeParse.getInstance(getActivity()).getPlanLineList());
						intent.setClass(getActivity(), PlansLineActivity.class);
					}
				}
				startActivity(intent);
			}
		}
	};
}