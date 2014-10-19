package com.xm.bus.common.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.xm.bus.common.model.VersionInfo;


public class XMLContentHandle extends DefaultHandler{
	private VersionInfo info=null;
	private String currentTag="";
	

	public VersionInfo getInfo() {
		return info;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		info=new VersionInfo();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		this.currentTag=qName;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String temp=new String(ch, start, length);
		if(currentTag.equals("version")){//½âÎöversion×Ö¶Î
			info.setVersion(temp);
		}else if(currentTag.equals("description")){//½âÎödescription×Ö¶Î
			info.setDescription(temp);
		}else if(currentTag.equals("apkurl")){//½âÎöapkurl×Ö¶Î
			info.setUrl(temp);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		this.currentTag="";
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

}
