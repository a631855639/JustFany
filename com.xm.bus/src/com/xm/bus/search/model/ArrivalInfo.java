package com.xm.bus.search.model;

import android.graphics.Bitmap;
import java.io.Serializable;

public class ArrivalInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String currLine;
	private String currentStation;
	private String kilometers;
	private String nextTime;
	private Bitmap pic;
	private String stopsNum;

	public String getCurrLine() {
		return this.currLine;
	}

	public String getCurrentStation() {
		return this.currentStation;
	}

	public String getKilometers() {
		return this.kilometers;
	}

	public String getNextTime() {
		return this.nextTime;
	}

	public Bitmap getPic() {
		return this.pic;
	}

	public String getStopsNum() {
		return this.stopsNum;
	}

	public void setCurrLine(String paramString) {
		this.currLine = paramString;
	}

	public void setCurrentStation(String paramString) {
		this.currentStation = paramString;
	}

	public void setKilometers(String paramString) {
		this.kilometers = paramString;
	}

	public void setNextTime(String paramString) {
		this.nextTime = paramString;
	}

	public void setPic(Bitmap paramBitmap) {
		this.pic = paramBitmap;
	}

	public void setStopsNum(String paramString) {
		this.stopsNum = paramString;
	}

	public String toString() {
		return "ArrivalInfo [currLine=" + this.currLine + ", currentStation="
				+ this.currentStation + ", pic=" + this.pic + ", stopsNum="
				+ this.stopsNum + ", kilometers=" + this.kilometers
				+ ", nextTime=" + this.nextTime + "]";
	}
}