package com.xm.bus.common.ui;


import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog {
	private static LoadingDialog instance;
	private static ProgressDialog progressDialog = null;

	public static LoadingDialog getInstance(Context context) {
		if (instance == null)
			instance = new LoadingDialog();
		if (progressDialog == null)
			progressDialog = new ProgressDialog(context);
		return instance;
	}

	public void dismiss() {
		progressDialog.dismiss();
		progressDialog = null;
	}

	public void show() {
		progressDialog.setTitle("��ʾ");
		progressDialog.setMessage("�����У����Ե�...");
		progressDialog.show();
	}
}