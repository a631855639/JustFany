package com.xm.bus.common.model;

public class VersionInfo {
	
	private String version;
	private String description;
	private String url;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "VersionInfo [version=" + version + ", description="
				+ description + ", url=" + url + "]";
	}
	
	
}
