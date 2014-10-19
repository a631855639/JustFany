package com.xm.bus.common.utils;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.xm.bus.common.model.VersionInfo;



public class XMLSaxParser {
	
	public static VersionInfo getVersionInfo(InputStream is){
		
		try {
			SAXParserFactory factory=SAXParserFactory.newInstance();
			SAXParser parser=factory.newSAXParser();
			XMLContentHandle contentHandle=new XMLContentHandle();
			parser.parse(is, contentHandle);
			is.close();
			return contentHandle.getInfo();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
