package com.xm.bus.search.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xm.bus.search.model.ArrivalInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class HtmlBaseParse {
	public static Document doc;
	public static ArrivalInfo arrivalInfo;
	public static String titleInfo1="";//头部提醒信息1
	public static String titleInfo2="";//头部提醒信息2
	public static enum STATE{
		InputError,//输入错误
		LineNotExistError,//线路不存在
		NetworkError,//网络繁忙
		Success,//成功
		ServerMaintenance//服务器维护
	}
	
	/**
	 * 
	 * @author Le 631855639@qq.com
	 * 2013-11-23 下午7:37:17
	 * @param stopMap
	 * @return
	 * ArrivalInfo
	 * TODO 获取公交车到站信息
	 */
	public static STATE getArrivalInfo(String url){
		arrivalInfo=new ArrivalInfo();
		try {
			doc=Jsoup.connect(url).get();
			//当前站点
			Element currStop=doc.select("div.cmode div.tl font span").get(0);
			arrivalInfo.setCurrentStation(currStop.text());
			//当前公交车所在站点图片路径
			Element buspic=doc.select("div.cmode img[src *=buspic]").get(0);
			byte[] b=WebService.getImage(Constant.BASE_URL+buspic.attr("src"));
			Bitmap image=BitmapFactory.decodeByteArray(b, 0, b.length);
			arrivalInfo.setPic(image);
			//下一班预计发车时间
			Elements nextTimeNodes=doc.select("div.cmode img[src *=next]");
			Elements nodes=doc.select("div.cmode font[color *=#0000FF]");
			//当前线路
			arrivalInfo.setCurrLine(nodes.get(0).text());
			//下一班车节点存在
			if(nextTimeNodes.size()!=0){
				Element nextTime=nextTimeNodes.get(0);
				arrivalInfo.setNextTime(nextTime.nextSibling().toString().replace("&nbsp;", ""));
				if(nodes.size()==1){//距离站点信息节点不存在
					arrivalInfo.setStopsNum("");
					arrivalInfo.setKilometers("");
				}else{
					//距离查询站点还差几站
					arrivalInfo.setStopsNum(nodes.get(1).text());
					//距离下一站还差几公里
					arrivalInfo.setKilometers(nodes.get(2).text());
				}
			}else{//下一班车节点不存在
				arrivalInfo.setNextTime("");
				if(nodes.size()>1){
				//距离查询站点还差几站
				arrivalInfo.setStopsNum(nodes.get(1).text());
				//距离下一站还差几公里
				arrivalInfo.setKilometers(nodes.get(2).text());
				}else{
					arrivalInfo.setStopsNum("0");
					arrivalInfo.setKilometers("0");
				}
			}
			return STATE.Success;
		} catch (Exception e) {
			//e.printStackTrace();
			if(e.getClass().getName().equals("java.net.UnknownHostException")){
				//服务器维护
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}
}