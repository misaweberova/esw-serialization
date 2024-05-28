package cz.esw.serialization;

import cz.esw.serialization.handler.JsonDataHandler;
import cz.esw.serialization.json.DataType;
import cz.esw.serialization.json.Dataset;
import cz.esw.serialization.json.MeasurementInfo;

import java.util.List;

/**
 * @author Marek Cuchý (CVUT)
 */
public class ResultChecker extends JsonDataHandler implements ResultConsumer {

	private static final double DEFAULT_DELTA = 0.000001;

	private Dataset currentDataset = null;

	public ResultChecker() {
		super(null, null);
	}

	public void getResults(ResultConsumer consumer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void acceptMeasurementInfo(int datasetId, long timestamp, String measurerName) {
		checkCurrentDataset();

		currentDataset = datasets.remove(datasetId);
		if (currentDataset == null) {
			throw new IllegalArgumentException("Unknown dataset " + datasetId);
		}
		MeasurementInfo info = currentDataset.getInfo();
		if (info.timestamp() != timestamp) {
			throw new IllegalArgumentException("Timestamp does not match: expected=" + info.timestamp() + ", actual=" + timestamp);
		}
		if (!info.measurerName().equals(measurerName)) {
			throw new IllegalArgumentException("Measurer name does not match: expected=" + info.measurerName() + ", actual=" + measurerName);
		}
	}

	@Override
	public void acceptResult(DataType type, double result) {
		List<Double> values = currentDataset.getRecords().remove(type);
		if (values == null) {
			throw new IllegalArgumentException("Unknown, unused or already checked data type: " + type);
		}

		double expected = values.stream().mapToDouble(Double::doubleValue).average().orElseThrow();

		if (!doubleEquals(expected, result)) {
			throw new IllegalArgumentException("Result for " + type + " does not match: expected=" + expected + ", actual=" + result);
		}
	}

	public boolean doubleEquals(double a, double b) {
		return doubleEquals(a, b, DEFAULT_DELTA);
	}

	public boolean doubleEquals(double a, double b, double delta) {
		return Math.abs(a - b) <= delta;
	}

	public void checkResults(){
		checkCurrentDataset();
		if(!datasets.isEmpty()){
			throw new IllegalStateException("Some of datasets not checked: " + datasets.values().stream().map(Dataset::getInfo).toList());
		}
	}

	private void checkCurrentDataset() {
		if (currentDataset != null && !currentDataset.getRecords().isEmpty()) {
			throw new IllegalStateException("Results for previous dataset not complete. Missing data types:  " + currentDataset.getRecords().keySet());
		}
	}
}
