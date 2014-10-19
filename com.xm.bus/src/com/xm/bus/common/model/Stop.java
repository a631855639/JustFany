package com.xm.bus.common.model;

import java.io.Serializable;

public class Stop implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String lineName;
	private String name;
	private String url;
	
	public Stop(){}
	public Stop(String lineName, String name, String url) {
		super();
		this.lineName = lineName;
		this.name = name;
		this.url = url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return "Stop [id=" + id + ", lineName=" + lineName + ", name=" + name
				+ ", url=" + url + "]";
	}
	
	

}
