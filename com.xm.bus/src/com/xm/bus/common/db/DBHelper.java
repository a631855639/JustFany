package com.xm.bus.common.db;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xm.bus.common.model.LineDetail;
import com.xm.bus.common.model.Stop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	private static String TAG="DBHelper";
	private static final String DATABASE_NAME="bus.db";
	private static final int DATABASE_VERSION=1;
	
	private static final String TB_LINE="tb_line";//线路表
	private static final String TB_LINE_DETAIL="tb_line_detail";//线路详细表
	private static final String TB_STOP="tb_stop";//站点表	
	private static DBHelper mDbHelper;
	private Context context;
	private SQLiteDatabase wdb,rdb;
	
	public DBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
		this.context=context;
	}
	
	public static synchronized DBHelper getInstance(Context context){
		if(mDbHelper==null){
			mDbHelper=new DBHelper(context);
		}
		return mDbHelper;
	}
	//建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "onCreate");
		
		String line="CREATE TABLE IF NOT EXISTS " +
				TB_LINE +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE DEFAULT 1 ," +
				"num  VARCHAR " +//线路号
				")";
		//Log.v(TAG, line);
		String lineDetail="CREATE TABLE IF NOT EXISTS " +
				TB_LINE_DETAIL +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE DEFAULT 1," +
				"line_num VARCHAR ," +//线路号
				"name VARCHAR," +//线路名称
				"first VARCHAR ," +//首班发车时间
				"last VARCHAR ," +//末班发车时间
				"stops VARCHAR ," +//该线路包含的站点个数
				"url VARHCAR" +//访问该线路的网络地址
				")";
		
		String stop="CREATE TABLE IF NOT EXISTS " +
				TB_STOP +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE DEFAULT 1 ," +
				"line_name VARCHAR ," +//线路名称
				"name VARCHAR ," +//站点名称
				"url VARHCAR" +//访问该站点的网络地址
				")";
		db.beginTransaction();
		db.execSQL(line);
		db.execSQL(lineDetail);
		db.execSQL(stop);
		db.setTransactionSuccessful();
		db.endTransaction();
		
	}
	//更新时调用	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	/**
	 * 
	 * @param line
	 * @return
	 * boolean
	 * TODO 该线路是否存在站点
	 */
	public boolean isStopsExists(String lineName){
		synchronized (mDbHelper) {
			boolean result=false;
			rdb=mDbHelper.getReadableDatabase();
			String where="line_name='"+lineName+"'";
			Cursor c=rdb.query(TB_STOP, new String[]{"_id"}, where, null, null, null, null);
			if(c!=null&&c.getCount()>0){
				result=true;
			}
			c.close();
			rdb.close();
			closeDB();
			return result;
		}
	}
	/**
	 * 
	 * @param line
	 * @return
	 * boolean
	 * TODO 该线路是否存在线路列表
	 */
	public boolean isLineDetailExists(String line){
		synchronized (mDbHelper) {
			boolean result=false;
			rdb=mDbHelper.getReadableDatabase();
			String where="line_num='"+line+"'";
			Cursor c=rdb.query(TB_LINE_DETAIL, new String[]{"_id"}, where, null, null, null, null);
			if(c!=null&&c.getCount()>0){
				result=true;
			}
			c.close();
			rdb.close();
			closeDB();
			return result;
		}
	}
	/**
	 * 
	 * @param line
	 * @return
	 * boolean
	 * TODO 该线路是否已存在
	 */
	public boolean isLineExists(String line) {
		synchronized (mDbHelper) {
			boolean result=false;
			rdb=mDbHelper.getReadableDatabase();
			String where="num='"+line+"'";
			Cursor c=rdb.query(TB_LINE, new String[]{"_id"}, where, null, null, null, null);
			if(c!=null&&c.getCount()>0){
				result=true;
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
			rdb.close();
			closeDB();
			return result;
		}
	}
	/**
	 * 添加线路号
	 * @param line 线路号
	 */
	public void addLine(String line){
		synchronized (mDbHelper) {
			wdb=mDbHelper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("num", line);
			long id=wdb.insert(TB_LINE, null, values);
			wdb.close();
			closeDB();
		}
	}
	/**
	 * 添加详细线路
	 * @param lineDetail
	 */
	public void addLineDetail(LineDetail lineDetail){
		synchronized (mDbHelper) {
			wdb=mDbHelper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("line_num", lineDetail.getLineNum());
			values.put("name", lineDetail.getName());
			values.put("first", lineDetail.getFirst());
			values.put("last", lineDetail.getLast());
			values.put("stops", lineDetail.getStops());
			values.put("url", lineDetail.getUrl());
			long id=wdb.insert(TB_LINE_DETAIL, null, values);
			wdb.close();
			closeDB();
		}
	}
	/**
	 * 添加站点
	 * @param stop
	 */
	public void addStop(Stop stop){
		synchronized (mDbHelper) {
			wdb=mDbHelper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("line_name", stop.getLineName());
			values.put("name", stop.getName());
			values.put("url", stop.getUrl());
			long id=wdb.insert(TB_STOP, null, values);
			wdb.close();
			closeDB();
		}
	}
	/**
	 * 更新线路列表，修改首末班发车时间以及站点总数
	 * @param lineDetail
	 */
	public void updateLineDetail(LineDetail lineDetail){
		synchronized (mDbHelper) {
			wdb=mDbHelper.getWritableDatabase();
			String where="name='"+lineDetail.getName()+"' and line_num='"+lineDetail.getLineNum()+"'";
			ContentValues values=new ContentValues();
			values.put("first", lineDetail.getFirst());
			values.put("last", lineDetail.getLast());
			values.put("stops", lineDetail.getStops());
			int id=wdb.update(TB_LINE_DETAIL, values, where, null);
			wdb.close();
			closeDB();
		}
	}
	/**
	 * 
	 * @param line 线路号 
	 * void
	 * TODO 根据线路号获取线路列表	 
	 * */
	public List<Map<String, String>> getLineDetailListByLine(String lineNum){
		synchronized (mDbHelper) {
			List<LineDetail> lines=new ArrayList<LineDetail>();
			rdb=mDbHelper.getReadableDatabase();
			String where="line_num='"+lineNum+"'";
			Cursor results=rdb.query(TB_LINE_DETAIL, null, where, null, null, null, null);
			while(results.moveToNext()){
				LineDetail lineDetail=new LineDetail();
				lineDetail.setId(results.getInt(0));
				lineDetail.setLineNum(results.getString(1));
				lineDetail.setName(results.getString(2));
				lineDetail.setFirst(results.getString(3));
				lineDetail.setLast(results.getString(4));
				lineDetail.setStops(results.getString(5));
				lineDetail.setUrl(results.getString(6));
				lines.add(lineDetail);
			}
			if (results != null && !results.isClosed()) {
				results.close();
			}
			rdb.close();
			closeDB();
			List<Map<String, String>> lists=new ArrayList<Map<String,String>>();
			for (LineDetail lineDetail : lines) {
				Map<String, String> map=new HashMap<String, String>();
				map.put("lineNum", lineDetail.getLineNum());
				map.put("lineName", lineDetail.getName());
				map.put("lineUrl",lineDetail.getUrl());
				lists.add(map);
			}
			return lists;
		}
	}
	/**
	 * 根据线路名称获取详细线路对象
	 * @param  lineNum 线路号
	 * @param  lineName 线路名称
	 * @return
	 */
	public LineDetail getLineDetail(String lineNum,String lineName){
		synchronized (mDbHelper) {
			LineDetail lineDetail=new LineDetail();
			rdb=mDbHelper.getReadableDatabase();
			String where="name='"+lineName+"' and line_num='"+lineNum+"'";
			Cursor result=rdb.query(TB_LINE_DETAIL, null, where, null, null, null, null);
			//result.moveToFirst();
			if(result.moveToFirst()){
				lineDetail.setId(result.getInt(0));
				lineDetail.setLineNum(result.getString(1));
				lineDetail.setName(result.getString(2));
				lineDetail.setFirst(result.getString(3));
				lineDetail.setLast(result.getString(4));
				lineDetail.setUrl(result.getString(5));
			}
			result.close();
			rdb.close();
			closeDB();
			return lineDetail;
		}
		
	}
	
	/*public List<Map<String, String>> getLineListMap(String line){
		List<Map<String, String>> lists=new ArrayList<Map<String,String>>();
		List<LineDetail> details=getLineDetailByLine(line);
		for (LineDetail lineDetail : details) {
			Map<String, String> map=new HashMap<String, String>();
			map.put("lineName", lineDetail.getName());
			map.put("lineUrl",lineDetail.getUrl());
			lists.add(map);
		}
		return lists;
	}*/
	/**
	 * 
	 * @param lineName
	 * @return 获取线路下的站点列表
	 */
	public List<Map<String, String>> getStopsListByLineName(String lineName){
		synchronized (mDbHelper) {
			List<Stop> stops=new ArrayList<Stop>();
			rdb=mDbHelper.getReadableDatabase();
			String where="line_name='"+lineName+"'";
			Cursor results=rdb.query(TB_STOP, null, where, null, null, null, null);
			while(results.moveToNext()){
				Stop stop=new Stop();
				stop.setId(results.getInt(0));
				stop.setLineName(results.getString(1));
				stop.setName(results.getString(2));
				stop.setUrl(results.getString(3));
				stops.add(stop);
			}
			if (results != null && !results.isClosed()) {
				results.close();
			}
			rdb.close();
			closeDB();
			List<Map<String, String>> lists=new ArrayList<Map<String,String>>();
			for (Stop stop : stops) {
				Map<String, String> map=new HashMap<String, String>();
				map.put("stopName", stop.getName());
				map.put("stopUrl", stop.getUrl());
				lists.add(map);
			}
			return lists;
		}
		
	}
	/**
	 * 
	 * @return
	 * boolean
	 * TODO 清空数据
	 */
	public  void  clear(){
		synchronized (mDbHelper) {
			wdb=getWritableDatabase();
			wdb.beginTransaction();
			wdb.delete(TB_LINE, null, null);
			wdb.delete(TB_LINE_DETAIL, null, null);
			wdb.delete(TB_STOP, null, null);
			wdb.setTransactionSuccessful();
			wdb.endTransaction();
			wdb.close();
			closeDB();
		}
		
	}
	/**
	 * 
	 * void
	 * TODO 关闭资源
	 */
	public void closeDB(){
		synchronized (mDbHelper) {
			super.close();
		}
	}
	
}
