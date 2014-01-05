package com.xm.bus.location.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.xm.bus.common.MyApplication;
import com.xm.bus.location.common.MyLocation;
import com.xm.bus.location.self.MyItemizedOverlay;

public class LocationMapFragment extends Fragment {
	private MyApplication app = null;
	Intent intent;
	private EditText keyText = null;
	private MapController mMapController = null;
	MKMapViewListener mMapListener = null;
	private MapView mMapView = null;
	MKSearch mMkSearch = null;
	private MyLocation myLocation;
	MyItemizedOverlay myOverlay = null;
	private GeoPoint p = null;
	private PoiOverlay poiOverlay = null;
	private PopupOverlay pop = null;
	private TextView popupText = null;
	View view;
	private View viewCache = null;

	private void initMap() {
		this.mMapController = this.mMapView.getController();
		this.mMapController.enableClick(true);
		this.mMapController.setZoom(15.0F);
		this.mMapView.setBuiltInZoomControls(true);
	}

	public View onCreateView(LayoutInflater paramLayoutInflater,
			ViewGroup paramViewGroup, Bundle paramBundle) {
		this.view = paramLayoutInflater.inflate(2130903074, paramViewGroup,
				false);
		return this.view;
	}

	public void onDestroy() {
		this.myLocation.destroy();
		this.mMkSearch.destory();
		this.mMapView.destroy();
		super.onDestroy();
	}

	public void onPause() {
		this.mMapView.onPause();
		super.onPause();
	}

	public void onResume() {
		this.mMapView.onResume();
		super.onResume();
	}

	public void onSaveInstanceState(Bundle paramBundle) {
		super.onSaveInstanceState(paramBundle);
		this.mMapView.onSaveInstanceState(paramBundle);
	}
}