package jp.caliconography.suicapasmoreader.excelutil;

import java.util.HashMap;
import java.util.Map;

/**
 * original source by http://codezine.jp/
 */
public class DetailsData {
	
	private int numOfDtails = 0;

	private Map<String, Object[]> dataListMap = new HashMap<String, Object[]>();

	public Map<String, Object[]> getDataListMap() {
		return dataListMap;
	}

	public void setDataListMap(Map<String, Object[]> dataListMap) {
		this.dataListMap = dataListMap;
	}

	public int getNumOfDetails() {
		return numOfDtails;
	}

	public void setNumOfDetails(int numOfDtails) {
		this.numOfDtails = numOfDtails;
	}
	
	
}
