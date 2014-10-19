package com.xm.bus.common.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import android.content.Context;

import com.xm.bus.common.Constant;
import com.xm.bus.common.base.HtmlBaseParse.STATE;

public class HtmlLineParse{
	private  Document doc;
	private  String theFirstAndLastTime1 = "";// 起点站首末班车发车时间
	private  String theFirstAndLastTime2 = "";// 终点站首末班车发车时间
	private  List<Map<String, String>> lineList = null;// 线路列表
	private  List<Map<String, String>> stopList = null;// 站点列表
	private static Context c;
	private static HtmlLineParse instance;
	
	private HtmlLineParse(){}
	
	public static synchronized HtmlLineParse getInstance(Context context){
		if(instance==null){
			instance=new HtmlLineParse();
		}
		c=context;
		return instance;
	}
	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-23 下午6:43:59
	 * @param line
	 *            要查询的线路
	 * @return STATE 返回状态 TODO 根据线路获取公交相关线路信息
	 */
	public  STATE getLine(String line) {
		lineList = new ArrayList<Map<String, String>>();
		if(line.equals("")){
			return STATE.InputError;
		}
		try {
			int lineNum = Integer.parseInt(line.trim());
			String url = Constant.LINE_URL + lineNum;
			doc = Jsoup.connect(url).get();
			if (doc.select("div.error").size() == 0) {
				Elements nodes = doc.select("div.cmode");
				/*titleInfo1 = nodes.select("div.tl font").get(0).nextSibling()
						.toString().trim();*/
				Elements lines = nodes.select("a[href *=" + lineNum + "]");
				for (Element node : lines) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("lineNum", line);
					map.put("lineName", node.text());
					map.put("lineUrl", node.attr("href"));
					lineList.add(map);
				}
				return STATE.Success;
			} else {
				// 没找到线路
				return STATE.LineNotExistError;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getClass().getName()
					.equals("java.lang.NumberFormatException")) {
				// 线路转化错误
				return STATE.InputError;
			} else if (e.getClass().getName()
					.equals("java.net.UnknownHostException")
					|| e.getClass().getName()
							.equals("java.net.SocketException")) {
				// 服务器维护
				return STATE.ServerMaintenance;
			} else {
				// 网络出现问题
				return STATE.NetworkError;
			}
		}
	}

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-23 下午7:27:46
	 * @param lineMap
	 * @return STATE TODO 获取线路中所有站点名称
	 */
	public  STATE getStopInLine(Map<String, String> lineMap) {
		stopList = new ArrayList<Map<String, String>>();
		try {
			doc = Jsoup.connect(Constant.BASE_URL + lineMap.get("lineUrl"))
					.get();
			Elements nodes = doc.select("div.cmode");
			/*titleInfo2 = nodes.select("a[href *=lname]").get(0)
					.previousSibling().toString().trim();*/
			Elements stops = nodes.select("a[href *=nextbus]");
			int i = 1;
			for (Element stop : stops) {
				Map<String, String> stopMap = new HashMap<String, String>();
				stopMap.put("stopName", i + "." + stop.text());
				stopMap.put("stopUrl", stop.attr("href"));
				stopList.add(stopMap);
				i++;
			}
			// 终点和起点的首末发车时间
			Elements first_Last = nodes.select("font[color *=blue]");
			if (first_Last.size() != 0) {
				Element firstAndLast = first_Last.get(0);
				theFirstAndLastTime1 = firstAndLast.nextElementSibling()
						.nextSibling().toString().trim().replace("&nbsp;", "");
				theFirstAndLastTime2 = firstAndLast.nextElementSibling()
						.nextElementSibling().nextSibling().toString().trim().replace("&nbsp;", "");
			}

			return STATE.Success;
		} catch (Exception e) {
			if (e.getClass().getName().equals("java.net.UnknownHostException")
					|| e.getClass().getName()
							.equals("java.net.SocketException")) {
				// 服务器维护
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-26 下午3:58:56
	 * @param stopMap
	 * @return STATE TODO 根据选择的站点获取公交到站信息
	 */
	public  STATE getArrivalInfo(Map<String, String> stopMap) {
		String url = Constant.BASE_URL + stopMap.get("stopUrl");

		return HtmlBaseParse.getInstacne(c).getArrivalInfo(url);
	}

	public String getTheFirstAndLastTime1() {
		return theFirstAndLastTime1;
	}


	public String getTheFirstAndLastTime2() {
		return theFirstAndLastTime2;
	}


	public List<Map<String, String>> getLineList() {
		return lineList;
	}


	public List<Map<String, String>> getStopList() {
		return stopList;
	}

	
}