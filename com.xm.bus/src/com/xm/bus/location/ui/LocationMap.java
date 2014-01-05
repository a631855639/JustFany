package com.xm.bus.location.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.xm.bus.R;
import com.xm.bus.common.MyApplication;
import com.xm.bus.location.common.LocationApplication;
import com.xm.bus.location.common.MyLocation;
import com.xm.bus.location.common.MyLocation.DoAfterListener;
import com.xm.bus.location.self.MyItemizedOverlay;
import com.xm.bus.location.self.MyPoiOverlay;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.stop.RelationStopActivity;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlStopParse;
import java.io.Serializable;

public class LocationMap extends ActionBarActivity implements OnClickListener {
	private ActionBar actionBar=null;
	private MyApplication app = null;
	private ImageButton backButton;
	private EditText keyText=null;
	private MapController mMapController = null;
	private MapView mMapView = null;
	private MKSearch mMkSearch = null;
	private MyLocation myLocation;
	private MyItemizedOverlay myOverlay = null;
	private GeoPoint p = null;
	private PoiOverlay poiOverlay = null;
	private PopupOverlay pop = null;
	private TextView popupText = null;
	private ImageButton searchButton=null;
	private View viewCache = null;
	
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		app = ((MyApplication) getApplication());
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init("pGMEW4fzUg34mQkKVegKcEY0",new LocationApplication.MyGeneralListener());
		}
		setContentView(R.layout.location_map_main);
		ExitApplication.getInstance().addActivity(this);
		myLocation = new MyLocation(this);
		Intent localIntent = getIntent();
		Bundle localBundle = localIntent.getExtras();
		p = new GeoPoint(localBundle.getInt("x"), localBundle.getInt("y"));
		
		initMap();
		initMKSerach();
		initActionBar();
		createPaopao();
		
		refreshLocation(p, localIntent.getStringExtra("locationName"));
	}
	
	/**
	 * 初始化地图
	 */
	private void initMap() {
		mMapView=(MapView) findViewById(R.id.bmapView);
		this.mMapController = mMapView.getController();
		this.mMapController.enableClick(true);
		this.mMapController.setZoom(15);
		this.mMapView.setBuiltInZoomControls(true);
	}
	
	/**
	 * 初始化活动栏
	 */
	private void initActionBar() {
		actionBar = getSupportActionBar();
		this.actionBar.setDisplayHomeAsUpEnabled(true);
		this.actionBar.setDisplayShowTitleEnabled(false);
		this.actionBar.setDisplayShowHomeEnabled(false);
		this.actionBar.setDisplayShowCustomEnabled(true);
		View localView = LayoutInflater.from(this).inflate(R.layout.actionbar_menu_item, null);
		this.backButton = ((ImageButton) localView.findViewById(R.id.back));
		this.searchButton = ((ImageButton) localView.findViewById(R.id.search));
		this.keyText = ((EditText) localView.findViewById(R.id.tip));
		this.backButton.setOnClickListener(this);
		this.searchButton.setOnClickListener(this);
		this.actionBar.setCustomView(localView);
	}
	
	/**
	 * 初始化泡泡图层
	 */
	private void createPaopao() {
		viewCache = getLayoutInflater().inflate(R.layout.paopao_layout,null);
		popupText = ((TextView) viewCache.findViewById(R.id.location_name));
		PopupClickListener popListener = new PopupClickListener() {
			public void onClickedPopup(int index) {
				if(index==0){
					myLocation.setDoAfterListener(new DoAfterListener() {
						@Override
						public void onDoAfter(BDLocation location) {
							// TODO Auto-generated method stub
							if(location==null){
								Toast.makeText(LocationMap.this, "定位出错了", Toast.LENGTH_LONG).show();
								return;
							}
							p = new GeoPoint((int) (location.getLatitude()*1E6),(int) (location.getLongitude()*1E6));
							String locationName=location.getAddrStr().replace(location.getProvince()+ location.getCity()+ location.getDistrict(), "");
							refreshLocation(p, locationName);
							Toast.makeText(LocationMap.this, "已重新定位了", Toast.LENGTH_SHORT).show();
						}
					});
				}else if(index==2){
					//mMkSearch.reverseGeocode(p);
					mMkSearch.poiSearchNearBy("公交站点", p, 5000);
				}
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
	}
	
	/**
	 * 初始化POI搜索模块
	 */
	private void initMKSerach() {
		this.mMkSearch = new MKSearch();
		this.mMkSearch.init(app.mBMapManager, new MKSearchListener() {
			/**
			 * poi搜索结果
			 */
			@Override
			public void onGetPoiResult(MKPoiResult result,int type, int iError) {
				// 错误号
				if (iError ==MKEvent.ERROR_RESULT_NOT_FOUND){  
					Toast.makeText(LocationMap.this, "抱歉，未找到结果",Toast.LENGTH_LONG).show();  
						return ;  
				}else if (iError != 0 || result == null) {  
					Toast.makeText(LocationMap.this, "搜索出错啦..", Toast.LENGTH_LONG).show();  
						return;  
				}  
				if (poiOverlay != null){
					mMapView.getOverlays().remove(poiOverlay);
					poiOverlay=null;
				}
				poiOverlay = new MyPoiOverlay(LocationMap.this, mMapView,keyText);
				poiOverlay.setData(result.getAllPoi());
				mMapView.getOverlays().add(LocationMap.this.poiOverlay);
				mMapView.refresh();
			   }
			
			@Override
			public void onGetAddrResult(MKAddrInfo mKAddrInfo, int iError) {
				if (iError != 0||mKAddrInfo==null) {
					Toast.makeText(LocationMap.this, "定位出错了",Toast.LENGTH_LONG).show();
					return;
				}
				if(mKAddrInfo.type == MKAddrInfo.MK_REVERSEGEOCODE){
					String str = mKAddrInfo.strAddr;
					Toast.makeText(LocationMap.this, str,Toast.LENGTH_SHORT).show();
					//popupText.setText(str);
				}
			}
			@Override
			public void onGetBusDetailResult(
					MKBusLineResult paramMKBusLineResult, int paramInt) {
			}
			@Override
			public void onGetDrivingRouteResult(
					MKDrivingRouteResult paramMKDrivingRouteResult, int paramInt) {
			}
			@Override
			public void onGetPoiDetailSearchResult(int paramInt1, int paramInt2) {
			}
			@Override
			public void onGetShareUrlResult(
					MKShareUrlResult paramMKShareUrlResult, int paramInt1,
					int paramInt2) {
			}
			@Override
			public void onGetSuggestionResult(
					MKSuggestionResult paramMKSuggestionResult, int paramInt) {
			}
			@Override
			public void onGetTransitRouteResult(
					MKTransitRouteResult paramMKTransitRouteResult, int paramInt) {
			}
			@Override
			public void onGetWalkingRouteResult(
					MKWalkingRouteResult paramMKWalkingRouteResult, int paramInt) {
			}
		});
	}
	
	/**
	 * 刷新定位结果
	 * @param paramGeoPoint
	 * @param paramString
	 */
	private void refreshLocation(GeoPoint geoPoint, String locationName) {
		if (this.myOverlay != null)
			mMapView.getOverlays().clear();
		OverlayItem overlayItem = new OverlayItem(geoPoint, "我的位置", "我的位置");
		this.popupText.setText(locationName);
		this.myOverlay = new MyItemizedOverlay(getResources().getDrawable(R.drawable.mylocation_name), this.mMapView, this.viewCache, this.pop);
		this.myOverlay.addItem(overlayItem);
		this.mMapView.getOverlays().add(myOverlay);
		this.mMapView.refresh();
		this.mMapController.animateTo(geoPoint);
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	@Override
	public void onClick(View view){
	    if (view.getId() == R.id.back){
	      onBackPressed();
	    }else if(view.getId() == R.id.search){
		new AsyncTask<Void, Void, STATE>() {
			protected  STATE doInBackground(Void...p) {
				STATE result=HtmlStopParse.getRelationStop(keyText.getText().toString().trim());
				return result;
			};
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(LocationMap.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(LocationMap.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.LineNotExistError){
					Toast.makeText(LocationMap.this, "你所查询的线路不存在，请重新输入", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.InputError){
					Toast.makeText(LocationMap.this, "你输入的线路或站点格式不对，请重新输入", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.NetworkError){
					Toast.makeText(LocationMap.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.Success){
					Intent intent=new Intent();
					intent.putExtra("relationStopList", (Serializable)HtmlStopParse.relationStopList);
					intent.setClass(LocationMap.this, RelationStopActivity.class);
					startActivity(intent);
				}
			}
		}.execute();
		LoadingDialog.getInstance(this).show();
	   }
  }

	protected void onDestroy() {
		this.myLocation.destroy();
		this.mMkSearch.destory();
		this.mMapView.destroy();
		super.onDestroy();
	}

	protected void onPause() {
		this.mMapView.onPause();
		super.onPause();
	}

	protected void onRestoreInstanceState(Bundle paramBundle) {
		super.onRestoreInstanceState(paramBundle);
		this.mMapView.onRestoreInstanceState(paramBundle);
	}

	protected void onResume() {
		this.mMapView.onResume();
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle paramBundle) {
		super.onSaveInstanceState(paramBundle);
		this.mMapView.onSaveInstanceState(paramBundle);
	}
}