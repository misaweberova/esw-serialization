package cz.esw.serialization.handler;

import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.json.DataType;

/**
 * @author Marek Cuchý (CVUT)
 */
public class AvroDataHandler implements DataHandler {
	@Override
	public void start() {

	}

	@Override
	public void handleNewDataset(int datasetId, long timestamp, String measurerName) {

	}

	@Override
	public void handleValue(int datasetId, DataType type, double value) {

	}

	@Override
	public void getResults(ResultConsumer consumer) {

	}
}
