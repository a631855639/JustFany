package com.xm.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.xm.bus.location.common.MyLocation;
import com.xm.bus.location.common.MyLocation.DoAfterListener;
import com.xm.bus.location.ui.DrawerMainActivity;
import com.xm.bus.location.ui.LocationMap;
import com.xm.bus.search.self.LoadingDialog;

public class LocationActivity extends Fragment implements OnClickListener {
	private LocationData locData = null;
	private MyLocation myLocation=null;
	private GeoPoint p = null;
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.locData = new LocationData();
		this.myLocation = new MyLocation(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater paramLayoutInflater,ViewGroup viewGroup, Bundle paramBundle) {
		View localView = paramLayoutInflater.inflate(R.layout.location_main, viewGroup,false);
		Button localButton1 = (Button) localView.findViewById(R.id.button1);
		Button localButton2 = (Button) localView.findViewById(R.id.button2);
		localButton1.setOnClickListener(this);
		localButton2.setOnClickListener(this);
		return localView;
	}
	
	@Override
	public void onClick(View paramView) {
		if (paramView.getId() == R.id.button1) {
			this.myLocation.setDoAfterListener(new DoAfterListener() {
						public void onDoAfter(BDLocation location) {
							if ((location == null)|| (location.getLocType() == BDLocation.TypeNetWorkException)) {
								LoadingDialog.getInstance(LocationActivity.this.getActivity()).dismiss();
								Toast.makeText(LocationActivity.this.getActivity(),"定位失败，请确保打开GPS或网络", Toast.LENGTH_LONG).show();
								return;
							}
							locData.latitude = location.getLatitude();
							locData.longitude = location.getLongitude();
							String str = location.getAddrStr().replace(location.getProvince()+ location.getCity()+ location.getDistrict(), "");
							p = new GeoPoint((int) (locData.latitude*1E6),(int) (locData.longitude*1E6));
							Intent localIntent = new Intent(getActivity(),LocationMap.class);
							Bundle localBundle = new Bundle();
							localBundle.putInt("x",p.getLatitudeE6());
							localBundle.putInt("y",p.getLongitudeE6());
							localIntent.putExtras(localBundle);
							localIntent.putExtra("locationName", str);
							LoadingDialog.getInstance(getActivity()).dismiss();
							startActivity(localIntent);
						}
					});
			LoadingDialog.getInstance(getActivity()).show();
		}else if (paramView.getId() != R.id.button2){
			//startActivity(new Intent(getActivity(), DrawerMainActivity.class));
		}
	}

	@Override
	public void onDestroy() {
		this.myLocation.destroy();
		super.onDestroy();
	}
}