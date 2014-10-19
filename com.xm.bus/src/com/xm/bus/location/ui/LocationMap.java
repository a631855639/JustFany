package com.xm.bus.location.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.baidu.mapapi.map.RouteOverlay;
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
import com.xm.bus.common.base.HtmlStopParse;
import com.xm.bus.common.base.HtmlBaseParse.STATE;
import com.xm.bus.common.ui.ExitApplication;
import com.xm.bus.common.ui.LoadingDialog;
import com.xm.bus.location.common.LocationApplication;
import com.xm.bus.location.common.MyLocation;
import com.xm.bus.location.common.MyLocation.DoAfterListener;
import com.xm.bus.location.self.MyItemizedOverlay;
import com.xm.bus.location.self.MyPoiOverlay;
import com.xm.bus.search.stop.RelationStopActivity;
import java.io.Serializable;

public class LocationMap extends Activity implements OnClickListener {
	//private ActionBar actionBar=null;//活动栏
	private MyApplication app = null;
	private ImageButton backButton;//返回键
	private EditText keyText=null;//搜索条件输入框
	private MapController mMapController = null;//地图控制器
	private MapView mMapView = null;//地图
	private MKSearch mMkSearch = null;//搜索模块
	private MyLocation myLocation;//定位
	private MyItemizedOverlay myOverlay = null;
	private GeoPoint p = null;//我的地理位置
	private PoiOverlay poiOverlay = null;//搜索结果图层
	private PopupOverlay pop = null;//poi搜索结果图层
	RouteOverlay routeOverlay=null;//线路图层
	private TextView popupText = null;//我的位置地名
	private ImageButton searchButton=null;//搜索按钮
	private View viewCache = null;//泡泡布局
	
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		/**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
		app = ((MyApplication) getApplication());//获取全局的activity，使得地图生命周期的从打开后，便和activity同样的存活时间
		if (app.mBMapManager == null) {
			/**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init("pGMEW4fzUg34mQkKVegKcEY0",new LocationApplication.MyGeneralListener());
		}
		//设置无标题  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_map_main);//页面布局
		ExitApplication.getInstance().addActivity(this);//释放资源
		myLocation = new MyLocation(this);//获取当前的mylocation或者重新定位
		Intent localIntent = getIntent();//获取刚刚存入数据的intent
		Bundle localBundle = localIntent.getExtras();//一种数据类型，类似map
		p = new GeoPoint(localBundle.getInt("x"), localBundle.getInt("y"));
		
		initMap();
		initMKSerach();
		initActionBar();
		createPaopao();
		
		refreshLocation(p, localIntent.getStringExtra("locationName"));
	}
	
	/**
	 * 初始化地图、设置地图控制器（缩放）
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
		/*actionBar = getSupportActionBar();
		this.actionBar.setDisplayHomeAsUpEnabled(false);//设置成可以向上返回
		this.actionBar.setDisplayShowTitleEnabled(false);//应用程序的名字
		this.actionBar.setDisplayShowHomeEnabled(false);//应用程序的图标
		this.actionBar.setDisplayShowCustomEnabled(true);//设置自定义的布局
		View localView = LayoutInflater.from(this).inflate(R.layout.actionbar_menu_item, null);
		this.backButton = ((ImageButton) localView.findViewById(R.id.back));
		this.searchButton = ((ImageButton) localView.findViewById(R.id.search));
		this.keyText = ((EditText) localView.findViewById(R.id.tip));
		this.backButton.setOnClickListener(this);
		this.searchButton.setOnClickListener(this);
		this.actionBar.setCustomView(localView);*/
		this.backButton = ((ImageButton) findViewById(R.id.back));
		this.searchButton = ((ImageButton) findViewById(R.id.search));
		this.keyText = ((EditText) findViewById(R.id.tip));
		this.backButton.setOnClickListener(this);
		this.searchButton.setOnClickListener(this);
	}
	
	/**
	 * 初始化泡泡图层
	 */
	private void createPaopao() {
		viewCache = getLayoutInflater().inflate(R.layout.paopao_layout,null);//设置泡泡布局
		popupText = ((TextView) viewCache.findViewById(R.id.location_name));//显示当前位置
		PopupClickListener popListener = new PopupClickListener() {
			public void onClickedPopup(int index) {
				if(index==0){//更新位置
					LoadingDialog.getInstance(LocationMap.this).show();
					myLocation.setDoAfterListener(new DoAfterListener() {
						@Override
						public void onDoAfter(BDLocation location) {
							if(location==null){
								Toast.makeText(LocationMap.this, "定位出错了", Toast.LENGTH_LONG).show();
								return;
							}
							p = new GeoPoint((int) (location.getLatitude()*1E6),(int) (location.getLongitude()*1E6));
							String locationName=location.getAddrStr().replace(location.getProvince()+ location.getCity()+ location.getDistrict(), "");
							refreshLocation(p, locationName);
							LoadingDialog.getInstance(LocationMap.this).dismiss();
							Toast.makeText(LocationMap.this, "已重新定位了", Toast.LENGTH_SHORT).show();
						}
					});
				}else if(index==2){//周边站点
					//mMkSearch.reverseGeocode(p);
					mMkSearch.poiSearchNearBy("公交站点", p, 5000);
					LoadingDialog.getInstance(LocationMap.this).show();
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
				LoadingDialog.getInstance(LocationMap.this).dismiss();
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
				poiOverlay = new MyPoiOverlay(LocationMap.this, mMapView,keyText,mMkSearch,p);
				poiOverlay.setData(result.getAllPoi());
				mMapView.getOverlays().add(LocationMap.this.poiOverlay);
				mMapView.refresh();
			   }
			/**
			 * 根据地理坐标点获取地址信息，暂时没用到
			 */
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
			/**
			 * 步行线路搜索结果
			 */
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
				if(result==null||iError!=0){
					Toast.makeText(LocationMap.this, "搜索出错啦..", Toast.LENGTH_LONG).show();  
					return;  
				}
				if(routeOverlay!=null){
					mMapView.getOverlays().remove(routeOverlay);
				}
				routeOverlay=new RouteOverlay(LocationMap.this, mMapView);
				// 两点的步行路线会有多条,取第一个方案
				routeOverlay.setData(result.getPlan(0).getRoute(0));
	            mMapView.getOverlays().add(routeOverlay);
	            mMapView.refresh();
			}
			@Override
			public void onGetBusDetailResult(
					MKBusLineResult paramMKBusLineResult, int paramInt) {
			}
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
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
		});
	}
	
	/**
	 * 刷新定位结果
	 * @param geoPoint
	 * @param locationName
	 */
	private void refreshLocation(GeoPoint geoPoint, String locationName) {
		if (this.myOverlay != null)
			mMapView.getOverlays().clear();
		OverlayItem overlayItem = new OverlayItem(geoPoint, "我的位置", "我的位置");
		this.popupText.setText(locationName);
		//设置定位图标
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
	//根据按钮的id，判断相应的事件
	@Override
	public void onClick(View view){
	    if (view.getId() == R.id.back){//返回键事件
	      onBackPressed();
	    }else if(view.getId() == R.id.search){//搜索按钮事件
		new AsyncTask<Void, Void, STATE>() {
			protected  STATE doInBackground(Void...p) {
				STATE result=HtmlStopParse.getInstance(LocationMap.this).getRelationStop(keyText.getText().toString().trim());
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
					intent.putExtra("relationStopList", (Serializable)HtmlStopParse.getInstance(LocationMap.this).getRelationStopList());
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