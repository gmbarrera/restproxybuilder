package com.gbarrera.tap;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Configuration {
	private String logicPath;
	private String proxyPath;
	private String tempPath;
	private String serverClassName;

	public String getLogicPath() {
		return logicPath;
	}

	public String getProxyPath() {
		return proxyPath;
	}
	
	public String getTempPath() {
		return tempPath;
	}
	
	public String getServerClassName() {
		return serverClassName;
	}
	
	public void load() throws Exception {

		try {
			File config = new File("config.xml");

			if (!config.exists()) {
				throw new FileNotFoundException("config.xml");
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(config);

			Element serverNode = (Element) doc.getElementsByTagName("server").item(0);
			logicPath = serverNode.getElementsByTagName("logic_path").item(0).getTextContent();

			Element clientNode = (Element) doc.getElementsByTagName("client").item(0);
			proxyPath = clientNode.getElementsByTagName("proxy_path").item(0).getTextContent();

			tempPath = doc.getElementsByTagName("temp_path").item(0).getTextContent();
			
			serverClassName = doc.getElementsByTagName("serverClassName").item(0).getTextContent();
			
		} catch (Exception e) {
			throw e;
		}
	}

	public void show() {
		System.out.println("Current configuration: ");

		System.out.println("\tPath where are the logic files: " + this.logicPath);
		System.out.println("\tPath for proxy files: " + this.proxyPath);
		System.out.println("\tPath for temporary files: " + this.tempPath);
		System.out.println("\tServer Class Name: " + this.serverClassName);
	}
}
