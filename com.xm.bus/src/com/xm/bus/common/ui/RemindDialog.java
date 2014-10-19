package com.xm.bus.common.ui;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class RemindDialog {
	private static RemindDialog instance;
	private static AlertDialog.Builder builder=null;
	private static boolean isExitApp=false;
	
	private RemindDialog(){}
	
	public static RemindDialog getInstance(Context context){
		if(instance==null){
			instance=new RemindDialog();
		}
		if(builder==null){
			builder=new Builder(context);
		}
		return instance;
	}
	/*public RemindDialog(Context context){
		builder=new Builder(context);
	}*/
	@SuppressWarnings("static-access")
	public void show(String title,String message,boolean haveNegativeButton,boolean isExitApp){
		this.isExitApp=isExitApp;
		builder.setTitle(title);
		builder.setMessage(message);
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				RemindDialog.builder=null;
				if(RemindDialog.isExitApp){
					ExitApplication.getInstance().Exit();
				}
			}
		});
		if(haveNegativeButton){
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					RemindDialog.builder=null;
				}
			});
		}
		builder.create().show();
	}
}