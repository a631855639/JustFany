package com.xm.bus.search.utils;

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

public class HtmlLineParse extends HtmlBaseParse {
	public static String theFirstAndLastTime1 = "";// 起点站首末班车发车时间
	public static String theFirstAndLastTime2 = "";// 终点站首末班车发车时间
	public static List<Map<String, String>> lineList = null;// 线路列表
	public static List<Map<String, String>> stopList = null;// 站点列表

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-23 下午6:43:59
	 * @param line
	 *            要查询的线路
	 * @return STATE 返回状态 TODO 根据线路获取公交相关线路信息
	 */
	public static STATE getLine(String line) {
		lineList = new ArrayList<Map<String, String>>();
		try {
			int lineNum = Integer.parseInt(line.trim());
			String temp = Constant.LINE_URL + lineNum;
			doc = Jsoup.connect(Constant.LINE_URL + lineNum).get();
			if (doc.select("div.error").size() == 0) {
				Elements nodes = doc.select("div.cmode");
				titleInfo1 = nodes.select("div.tl font").get(0).nextSibling()
						.toString().trim();
				Elements lines = nodes.select("a[href *=" + lineNum + "]");
				for (Element node : lines) {
					Map<String, String> map = new HashMap<String, String>();
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
	public static STATE getStopInLine(Map<String, String> lineMap) {
		stopList = new ArrayList<Map<String, String>>();
		try {
			doc = Jsoup.connect(Constant.BASE_URL + lineMap.get("lineUrl"))
					.get();
			Elements nodes = doc.select("div.cmode");
			titleInfo2 = nodes.select("a[href *=lname]").get(0)
					.previousSibling().toString().trim();
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
						.nextSibling().toString();
				theFirstAndLastTime2 = firstAndLast.nextElementSibling()
						.nextElementSibling().nextSibling().toString();
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
	public static STATE getArrivalInfo(Map<String, String> stopMap) {
		String url = Constant.BASE_URL + stopMap.get("stopUrl");

		return getArrivalInfo(url);
	}
}