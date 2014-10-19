package com.xm.bus.common;

public class Constant {
	public static final String APPSID = "c034420e";
	public static final String InlineID = "16TLuJjoApIgSNU-351EkzEs";
	public static final String PublisherID = "56OJw0dIuNPDy1Ejn8";
	public static final String BASE_URL = "http://www.5320000.com";
	public static final String CHANGE_URL = "http://www.5320000.com/index.php/Home/Line/plans/";
	public static final String LINE_URL = "http://www.5320000.com/index.php/Line/line?line=";
	public static final String STOP_URL = "http://www.5320000.com/index.php/Line/stop?stop=";
	public static final String VERSION = "2.0";
	
	public static final String SETTING="Setting";//使用SharedPreferences存放设置的数据库
	public static final String REMIND_STOPS="remind_stops";//提前几站提醒
	public static final String REFRESH_TIME="refresh_time";//刷新频率,单位毫秒
	public static final String IS_REMIND="is_remind";//是否设置了提醒
	public static final String IS_RING="is_ring";//是否响铃
	public static final String IS_VIBRATE="is_vibrate";//是否震动
	public static final String HAS_WIDGET="has_widget";//是否有小组件
	
	public static final String HISTORY_RECORDS="history_records";//使用SharedPreferences存放搜索历史记录数据库
	public static final String LINE_HISTORY="history_line";//线路历史搜索记录
	public static final String STOP_HISTORY="history_stop";//站点历史搜索记录
	public static final String CHANGE_FROM_HISTORY="history_from";//换乘的起始站点历史搜索记录
	public static final String CHANGE_TO_HISTORY="history_to";//换乘的目的地站点历史搜索记录
	public static final String HISTORY_SHOW_LIMIT="histroy_show_limit";//历史记录最多显示条数
	
	public static final String DOWNLOAD_URL="http://192.168.137.1:8080/mybus/version.xml";//更新下载地址
	public static final String FILE_PATH="/mybus/update";//文件夹路径
	public static final String APK_NAME="mybus_"+VERSION+"_new.apk";
	public static final String APK_PATH=FILE_PATH+APK_NAME;//更新下来的新apk包放置的路径与包名
}