package cz.esw.serialization.handler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.json.DataType;
import cz.esw.serialization.json.Dataset;
import cz.esw.serialization.json.MeasurementInfo;
import cz.esw.serialization.json.Result;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author Marek Cuchý (CVUT)
 */
public class JsonDataHandler implements DataHandler {


	private static final ObjectMapper MAPPER;

	static{
		//prevent socket from closing on write and read JSON
		JsonFactory factory = new JsonFactory();
		factory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
		MAPPER = new ObjectMapper(factory);
	}

	private final InputStream is;
	private final OutputStream os;

	protected Map<Integer, Dataset> datasets;

	/**
	 * @param is
	 * 		input stream from which the results will be read
	 * @param os
	 * 		output stream to which the data have to written
	 */
	public JsonDataHandler(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	@Override
	public void initialize() {
		datasets = new HashMap<>();
	}

	@Override
	public void handleNewDataset(int datasetId, long timestamp, String measurerName) {
		MeasurementInfo info = new MeasurementInfo(datasetId, timestamp, measurerName);
		Map<DataType, List<Double>> recordMap = new EnumMap<>(DataType.class);

		datasets.put(datasetId, new Dataset(info, recordMap));
	}

	@Override
	public void handleValue(int datasetId, DataType type, double value) {
		Dataset dataset = datasets.get(datasetId);
		if (dataset == null) {
			throw new IllegalArgumentException("Dataset with id " + datasetId + " not initialized.");
		}
		dataset.getRecords().computeIfAbsent(type, t -> new ArrayList<>()).add(value);
	}

	@Override
	public void getResults(ResultConsumer consumer) throws IOException {
		//convert datasets to JSON and write them to the output stream
		MAPPER.writeValue(os, datasets.values().toArray());

		os.write(0); //write end

		//parse JSON results from the input stream
		Result[] results = MAPPER.readValue(is, Result[].class);


		for (Result result : results) {
			MeasurementInfo info = result.getInfo();
			consumer.acceptMeasurementInfo(info.id(), info.timestamp(), info.measurerName());
			result.getAverages().forEach(consumer::acceptResult);
		}
	}
}
