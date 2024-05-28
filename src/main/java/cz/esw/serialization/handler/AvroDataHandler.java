package cz.esw.serialization.handler;

import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.json.DataType;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Marek Cuchý (CVUT)
 */
public class AvroDataHandler implements DataHandler {

    public AvroDataHandler(InputStream is, OutputStream os) {

    }

    @Override
    public void initialize() {

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