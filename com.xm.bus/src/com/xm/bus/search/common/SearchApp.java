package com.xm.bus.search.common;


import android.app.Application;

public class SearchApp extends Application {
	public String from;
	public String to;
	public String url;
	//public ArrivalInfo infos;

	public String getFrom() {
		return this.from;
	}

	public String getTo() {
		return this.to;
	}

	public String getUrl() {
		return this.url;
	}

	public void setFrom(String paramString) {
		this.from = paramString;
	}

	public void setTo(String paramString) {
		this.to = paramString;
	}

	public void setUrl(String paramString) {
		this.url = paramString;
	}

	/*public ArrivalInfo getInfos() {
		return infos;
	}

	public void setInfos(ArrivalInfo infos) {
		this.infos = infos;
	}*/
}