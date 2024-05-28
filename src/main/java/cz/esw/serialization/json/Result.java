package cz.esw.serialization.json;

import java.util.Map;

/**
 * @author Marek Cuchý (CVUT)
 */
public class Result {

	private MeasurementInfo info;
	private Map<DataType, Double> averages;

	public MeasurementInfo getInfo() {
		return info;
	}

	public void setInfo(MeasurementInfo info) {
		this.info = info;
	}

	public Map<DataType, Double> getAverages() {
		return averages;
	}

	public void setAverages(Map<DataType, Double> averages) {
		this.averages = averages;
	}
}
