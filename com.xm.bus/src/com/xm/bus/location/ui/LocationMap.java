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
	//private ActionBar actionBar=null;//���
	private MyApplication app = null;
	private ImageButton backButton;//���ؼ�
	private EditText keyText=null;//�������������
	private MapController mMapController = null;//��ͼ������
	private MapView mMapView = null;//��ͼ
	private MKSearch mMkSearch = null;//����ģ��
	private MyLocation myLocation;//��λ
	private MyItemizedOverlay myOverlay = null;
	private GeoPoint p = null;//�ҵĵ���λ��
	private PoiOverlay poiOverlay = null;//�������ͼ��
	private PopupOverlay pop = null;//poi�������ͼ��
	RouteOverlay routeOverlay=null;//��·ͼ��
	private TextView popupText = null;//�ҵ�λ�õ���
	private ImageButton searchButton=null;//������ť
	private View viewCache = null;//���ݲ���
	
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		/**
         * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager.
         * BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
         * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
         */
		app = ((MyApplication) getApplication());//��ȡȫ�ֵ�activity��ʹ�õ�ͼ�������ڵĴӴ򿪺󣬱��activityͬ���Ĵ��ʱ��
		if (app.mBMapManager == null) {
			/**
             * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
             */
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init("pGMEW4fzUg34mQkKVegKcEY0",new LocationApplication.MyGeneralListener());
		}
		//�����ޱ���  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_map_main);//ҳ�沼��
		ExitApplication.getInstance().addActivity(this);//�ͷ���Դ
		myLocation = new MyLocation(this);//��ȡ��ǰ��mylocation�������¶�λ
		Intent localIntent = getIntent();//��ȡ�ոմ������ݵ�intent
		Bundle localBundle = localIntent.getExtras();//һ���������ͣ�����map
		p = new GeoPoint(localBundle.getInt("x"), localBundle.getInt("y"));
		
		initMap();
		initMKSerach();
		initActionBar();
		createPaopao();
		
		refreshLocation(p, localIntent.getStringExtra("locationName"));
	}
	
	/**
	 * ��ʼ����ͼ�����õ�ͼ�����������ţ�
	 */
	private void initMap() {
		mMapView=(MapView) findViewById(R.id.bmapView);
		this.mMapController = mMapView.getController();
		this.mMapController.enableClick(true);
		this.mMapController.setZoom(15);
		this.mMapView.setBuiltInZoomControls(true);
	}
	
	/**
	 * ��ʼ�����
	 */
	private void initActionBar() {
		/*actionBar = getSupportActionBar();
		this.actionBar.setDisplayHomeAsUpEnabled(false);//���óɿ������Ϸ���
		this.actionBar.setDisplayShowTitleEnabled(false);//Ӧ�ó��������
		this.actionBar.setDisplayShowHomeEnabled(false);//Ӧ�ó����ͼ��
		this.actionBar.setDisplayShowCustomEnabled(true);//�����Զ���Ĳ���
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
	 * ��ʼ������ͼ��
	 */
	private void createPaopao() {
		viewCache = getLayoutInflater().inflate(R.layout.paopao_layout,null);//�������ݲ���
		popupText = ((TextView) viewCache.findViewById(R.id.location_name));//��ʾ��ǰλ��
		PopupClickListener popListener = new PopupClickListener() {
			public void onClickedPopup(int index) {
				if(index==0){//����λ��
					LoadingDialog.getInstance(LocationMap.this).show();
					myLocation.setDoAfterListener(new DoAfterListener() {
						@Override
						public void onDoAfter(BDLocation location) {
							if(location==null){
								Toast.makeText(LocationMap.this, "��λ������", Toast.LENGTH_LONG).show();
								return;
							}
							p = new GeoPoint((int) (location.getLatitude()*1E6),(int) (location.getLongitude()*1E6));
							String locationName=location.getAddrStr().replace(location.getProvince()+ location.getCity()+ location.getDistrict(), "");
							refreshLocation(p, locationName);
							LoadingDialog.getInstance(LocationMap.this).dismiss();
							Toast.makeText(LocationMap.this, "�����¶�λ��", Toast.LENGTH_SHORT).show();
						}
					});
				}else if(index==2){//�ܱ�վ��
					//mMkSearch.reverseGeocode(p);
					mMkSearch.poiSearchNearBy("����վ��", p, 5000);
					LoadingDialog.getInstance(LocationMap.this).show();
				}
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
	}
	
	/**
	 * ��ʼ��POI����ģ��
	 */
	private void initMKSerach() {
		this.mMkSearch = new MKSearch();
		this.mMkSearch.init(app.mBMapManager, new MKSearchListener() {
			/**
			 * poi�������
			 */
			@Override
			public void onGetPoiResult(MKPoiResult result,int type, int iError) {
				// �����
				LoadingDialog.getInstance(LocationMap.this).dismiss();
				if (iError ==MKEvent.ERROR_RESULT_NOT_FOUND){  
					Toast.makeText(LocationMap.this, "��Ǹ��δ�ҵ����",Toast.LENGTH_LONG).show();  
						return ;  
				}else if (iError != 0 || result == null) {  
					Toast.makeText(LocationMap.this, "����������..", Toast.LENGTH_LONG).show();  
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
			 * ���ݵ���������ȡ��ַ��Ϣ����ʱû�õ�
			 */
			@Override
			public void onGetAddrResult(MKAddrInfo mKAddrInfo, int iError) {
				if (iError != 0||mKAddrInfo==null) {
					Toast.makeText(LocationMap.this, "��λ������",Toast.LENGTH_LONG).show();
					return;
				}
				if(mKAddrInfo.type == MKAddrInfo.MK_REVERSEGEOCODE){
					String str = mKAddrInfo.strAddr;
					Toast.makeText(LocationMap.this, str,Toast.LENGTH_SHORT).show();
					//popupText.setText(str);
				}
			}
			/**
			 * ������·�������
			 */
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
				if(result==null||iError!=0){
					Toast.makeText(LocationMap.this, "����������..", Toast.LENGTH_LONG).show();  
					return;  
				}
				if(routeOverlay!=null){
					mMapView.getOverlays().remove(routeOverlay);
				}
				routeOverlay=new RouteOverlay(LocationMap.this, mMapView);
				// ����Ĳ���·�߻��ж���,ȡ��һ������
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
	 * ˢ�¶�λ���
	 * @param geoPoint
	 * @param locationName
	 */
	private void refreshLocation(GeoPoint geoPoint, String locationName) {
		if (this.myOverlay != null)
			mMapView.getOverlays().clear();
		OverlayItem overlayItem = new OverlayItem(geoPoint, "�ҵ�λ��", "�ҵ�λ��");
		this.popupText.setText(locationName);
		//���ö�λͼ��
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
	//���ݰ�ť��id���ж���Ӧ���¼�
	@Override
	public void onClick(View view){
	    if (view.getId() == R.id.back){//���ؼ��¼�
	      onBackPressed();
	    }else if(view.getId() == R.id.search){//������ť�¼�
		new AsyncTask<Void, Void, STATE>() {
			protected  STATE doInBackground(Void...p) {
				STATE result=HtmlStopParse.getInstance(LocationMap.this).getRelationStop(keyText.getText().toString().trim());
				return result;
			};
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(LocationMap.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(LocationMap.this, "�Բ��𣬷���������ά�������Ժ�����", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.LineNotExistError){
					Toast.makeText(LocationMap.this, "������ѯ����·�����ڣ�����������", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.InputError){
					Toast.makeText(LocationMap.this, "���������·��վ���ʽ���ԣ�����������", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.NetworkError){
					Toast.makeText(LocationMap.this, "���緱æ�������³���", Toast.LENGTH_SHORT).show();
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