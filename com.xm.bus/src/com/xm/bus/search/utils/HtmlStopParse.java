package com.xm.bus.search.utils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlStopParse extends HtmlBaseParse {
	public static List<Map<String, String>> relationStopList;// 相关站点列表
	public static List<Map<String, String>> relationLineList;// 包含相关站点的公交线路列表

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-26 下午3:40:25
	 * @param stopName
	 *            站点名称
	 * @return STATE TODO 根据站点名称获取相关车站
	 */
	public static STATE getRelationStop(String stopName) {
		relationStopList = new ArrayList<Map<String, String>>();
		try {
			String name = URLEncoder.encode(stopName, "utf-8");
			doc = Jsoup.connect(Constant.STOP_URL + name).get();

			if (doc.select("div.error").size() == 0) {
				Elements nodes = doc.select("div.cmode");
				titleInfo1 = nodes.select("div.tl font").get(0).nextSibling()
						.toString().trim();
				Elements nextpages;
				boolean isNext = false;// 是否有下一页
				do {
					Elements lines = nodes.select("a[href *=" + stopName + "]");
					for (Element node : lines) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("relationStopName", node.text());
						map.put("relationStopUrl", node.attr("href"));
						relationStopList.add(map);
					}
					nextpages = nodes.select("a[href *=query]");
					if (nextpages.size() != 0
							&& nextpages.get(0).text().equals("下页")) {
						Element nextpage = nextpages.get(0);
						doc = Jsoup.connect(
								Constant.BASE_URL + nextpage.attr("href"))
								.get();
						nodes = doc.select("div.cmode");
						isNext = true;
					} else {
						isNext = false;
					}
				} while (isNext);
				return STATE.Success;
			} else {
				// 没找到相关车站
				return STATE.LineNotExistError;
			}
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

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-26 下午3:42:29
	 * @param relationStopMap
	 * @return STATE TODO 根据站点名称返回所有包含该站点的公交线路列表
	 */
	public static STATE getRelationLine(Map<String, String> relationStopMap) {
		relationLineList = new ArrayList<Map<String, String>>();
		try {
			String url = relationStopMap.get("relationStopUrl").replace(
					relationStopMap.get("relationStopName"),
					URLEncoder.encode(relationStopMap.get("relationStopName"),
							"utf-8"));
			doc = Jsoup.connect(Constant.BASE_URL + url).get();
			Elements nodes = doc.select("div.cmode");
			titleInfo2 = nodes.select("div.tl").get(0).text();
			Elements nextpages;
			boolean isNext = false;// 是否有下一页
			do {
				Elements lines = nodes.select("a[href *=nextbus]");
				for (Element line : lines) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("relationLineName", line.text());
					map.put("relationLineUrl", line.attr("href"));
					relationLineList.add(map);
				}
				nextpages = nodes.select("a[href *=stoptoline]");
				if (nextpages.size() != 0
						&& nextpages.get(0).text().equals("下页")) {
					Element nextpage = nextpages.get(0);
					doc = Jsoup.connect(
							Constant.BASE_URL + nextpage.attr("href")).get();
					nodes = doc.select("div.cmode");
					isNext = true;
				} else {
					isNext = false;
				}
			} while (isNext);
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

	/**
	 * 
	 * @author Le 631855639@qq.com 2013-11-26 下午3:55:18
	 * @param relationLineMap
	 * @return STATE TODO 根据线路名称获取公交到站信息
	 */
	public static STATE getArrivalInfo(Map<String, String> relationLineMap) {
		String url = Constant.BASE_URL + relationLineMap.get("relationLineUrl");
		return getArrivalInfo(url);
	}
}