package com.xm.bus.common.model;

import java.io.Serializable;

public class LineDetail implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String lineNum;
	private String name;
	private String first;
	private String last;
	private String stops;
	private String url;
	
	public LineDetail(){}
	public LineDetail(String lineNum, String name, String first, String last,
			String stops, String url) {
		super();
		this.lineNum = lineNum;
		this.name = name;
		this.first = first;
		this.last = last;
		this.stops = stops;
		this.url = url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLineNum() {
		return lineNum;
	}
	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getLast() {
		return last;
	}
	public void setLast(String last) {
		this.last = last;
	}
	public String getStops() {
		return stops;
	}
	public void setStops(String stops) {
		this.stops = stops;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "LineDetail [id=" + id + ", lineNum=" + lineNum + ", name="
				+ name + ", first=" + first + ", last=" + last + ", stops="
				+ stops + ", url=" + url + "]";
	}
	
	
	
}
