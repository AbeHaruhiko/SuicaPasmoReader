package jp.caliconography.suicapasmoreader.util;

import java.util.HashMap;
import java.util.Map;

public class HeaderData {
	
	private String reportName = null;

	private Map<String, String> dataMap = new HashMap<String, String>();

	public Map<String, String> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}
