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
	public static String titleInfo1="";//ͷ��������Ϣ1
	public static String titleInfo2="";//ͷ��������Ϣ2
	public static enum STATE{
		InputError,//�������
		LineNotExistError,//��·������
		NetworkError,//���緱æ
		Success,//�ɹ�
		ServerMaintenance//������ά��
	}
	
	/**
	 * 
	 * @author Le 631855639@qq.com
	 * 2013-11-23 ����7:37:17
	 * @param stopMap
	 * @return
	 * ArrivalInfo
	 * TODO ��ȡ��������վ��Ϣ
	 */
	public static STATE getArrivalInfo(String url){
		arrivalInfo=new ArrivalInfo();
		try {
			doc=Jsoup.connect(url).get();
			//��ǰվ��
			Element currStop=doc.select("div.cmode div.tl font span").get(0);
			arrivalInfo.setCurrentStation(currStop.text());
			//��ǰ����������վ��ͼƬ·��
			Element buspic=doc.select("div.cmode img[src *=buspic]").get(0);
			byte[] b=WebService.getImage(Constant.BASE_URL+buspic.attr("src"));
			Bitmap image=BitmapFactory.decodeByteArray(b, 0, b.length);
			arrivalInfo.setPic(image);
			//��һ��Ԥ�Ʒ���ʱ��
			Elements nextTimeNodes=doc.select("div.cmode img[src *=next]");
			Elements nodes=doc.select("div.cmode font[color *=#0000FF]");
			//��ǰ��·
			arrivalInfo.setCurrLine(nodes.get(0).text());
			//��һ�೵�ڵ����
			if(nextTimeNodes.size()!=0){
				Element nextTime=nextTimeNodes.get(0);
				arrivalInfo.setNextTime(nextTime.nextSibling().toString().replace("&nbsp;", ""));
				if(nodes.size()==1){//����վ����Ϣ�ڵ㲻����
					arrivalInfo.setStopsNum("");
					arrivalInfo.setKilometers("");
				}else{
					//�����ѯվ�㻹�վ
					arrivalInfo.setStopsNum(nodes.get(1).text());
					//������һվ�������
					arrivalInfo.setKilometers(nodes.get(2).text());
				}
			}else{//��һ�೵�ڵ㲻����
				arrivalInfo.setNextTime("");
				if(nodes.size()>1){
				//�����ѯվ�㻹�վ
				arrivalInfo.setStopsNum(nodes.get(1).text());
				//������һվ�������
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
				//������ά��
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}
}