package com.xm.bus.location.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.baidu.mapapi.BMapManager;
import com.xm.bus.R;
import com.xm.bus.common.MyApplication;
import com.xm.bus.location.common.LocationApplication;
import com.xm.bus.location.common.LocationApplication.MyGeneralListener;

public class DrawerFragment extends Fragment {
	private MyApplication app = null;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.app = ((MyApplication) getActivity().getApplication());
		if (this.app.mBMapManager == null) {
			this.app.mBMapManager = new BMapManager(getActivity());
			this.app.mBMapManager.init("pGMEW4fzUg34mQkKVegKcEY0",new LocationApplication.MyGeneralListener());
		}
	}

	public View onCreateView(LayoutInflater paramLayoutInflater,
			ViewGroup paramViewGroup, Bundle paramBundle) {
		return paramLayoutInflater.inflate(R.layout.location_map_main, paramViewGroup, false);
	}
}