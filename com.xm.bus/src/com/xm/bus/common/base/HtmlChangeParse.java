package com.xm.bus.common.base;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import com.xm.bus.common.Constant;
import com.xm.bus.common.base.HtmlBaseParse.STATE;

public class HtmlChangeParse {
	private Document doc;
	private static HtmlChangeParse instance;
	public  List<Map<String, String>> sourceStopList;//起始站点
	public  List<Map<String, String>> destinationStopList;//目的地站点
	public  List<Map<String, String>> planLineList;//换乘方案
	public  List<Map<String, String>> changeDetailList;//换乘方案详细介绍信息
	private static Context c;
	private HtmlChangeParse(){}
	
	public static synchronized HtmlChangeParse getInstance(Context context){
		if(instance==null){
			instance=new HtmlChangeParse();
		}
		c=context;
		return instance;
	}
	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-27 下午3:02:19
	 * @param from
	 *            出发地
	 * @param to
	 *            目的地
	 * @return STATE TODO 获取出发地相关站点
	 */
	public  STATE getSourceStop(String from) {

		sourceStopList = new ArrayList<Map<String, String>>();
		if (!from.trim().equals("")) {
			STATE state = HtmlStopParse.getInstance(c).getRelationStop(from);
			if (state == STATE.Success) {
				sourceStopList = HtmlStopParse.getInstance(c).getRelationStopList();
			}
			return state;
		}
		return STATE.InputError;
	}

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-27 下午8:41:37
	 * @param srcStopMap
	 * @return STATE TODO 获取目的地相关站点
	 */
	public  STATE getDestinationStop(String to) {
		destinationStopList = new ArrayList<Map<String, String>>();
		if (!to.trim().equals("")) {
			STATE state = HtmlStopParse.getInstance(c).getRelationStop(to);
			if (state == STATE.Success) {
				destinationStopList = HtmlStopParse.getInstance(c).getRelationStopList();
			}
			return state;
		}

		return STATE.InputError;

	}

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-27 下午9:57:02
	 * @param srcStopMap
	 * @param desStopMap
	 * @return STATE TODO 获取换乘方案
	 */
	public  STATE getChangePlans(String from, String to) {
		try {
			planLineList = new ArrayList<Map<String, String>>();
			doc = Jsoup.connect(Constant.CHANGE_URL).data("from", from)
					.data("to", to).get();
			if (doc.select("div.error").size() == 0) {
				Elements nodes = doc.select("div.list a[href *=p]");
				/*titleInfo1 = doc.select("div.tl br").get(0).nextSibling()
						.toString();// 线路*/
				for (Element node : nodes) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("changeLineName", node.text());
					map.put("changeLineUrl", node.attr("href"));
					planLineList.add(map);
				}
				return STATE.Success;
			} else {
				// 很抱歉，两站之间没有公交换乘方案
				return STATE.LineNotExistError;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	 * @author Le 631855639@qq.com 2013-11-27 下午9:57:35
	 * @param plansLineMap
	 * @return STATE TODO 获取换乘方案详细介绍信息
	 */
	public  STATE getChangeDetail(Map<String, String> planLineMap,
			String from, String to) {
		try {
			changeDetailList = new ArrayList<Map<String, String>>();
			String fromEncoder = URLEncoder.encode(from, "utf-8");
			String toEncoder = URLEncoder.encode(to, "utf-8");
			String url = planLineMap.get("changeLineUrl")
					.replace(from, fromEncoder).replace(to, toEncoder);
			doc = Jsoup.connect(Constant.BASE_URL + url).get();
			Elements nodes = doc.select("div.list");
			/*titleInfo2 = nodes.select("br").get(0).previousSibling().toString()
					.trim();*/
			Elements links = nodes.select("a[href *=nextbus]");
			for (Element node : links) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("relationLineName", node.text());
				map.put("relationLineUrl", node.attr("href"));
				map.put("up", node.nextElementSibling().nextSibling()
						.toString());
				map.put("down", node.nextElementSibling().nextElementSibling()
						.nextSibling().toString());
				changeDetailList.add(map);
			}
			return STATE.Success;
		} catch (Exception e) {
			// e.printStackTrace();
			if (e.getClass().getName().equals("java.net.UnknownHostException")
					|| e.getClass().getName()
							.equals("java.net.SocketException")) {
				// 服务器维护
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}

	public List<Map<String, String>> getSourceStopList() {
		return sourceStopList;
	}


	public List<Map<String, String>> getDestinationStopList() {
		return destinationStopList;
	}


	public List<Map<String, String>> getPlanLineList() {
		return planLineList;
	}


	public List<Map<String, String>> getChangeDetailList() {
		return changeDetailList;
	}

	
	
}