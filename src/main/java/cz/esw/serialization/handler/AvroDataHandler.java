package cz.esw.serialization.handler;

import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.avro.AAverage;
import cz.esw.serialization.avro.ARecords;
import cz.esw.serialization.avro.ARequestTuple;
import cz.esw.serialization.avro.AResponseTuple;
import cz.esw.serialization.avro.AMeasurementInfo;
import cz.esw.serialization.json.DataType;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.io.BinaryEncoder;


import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author Marek Cuchý (CVUT)
 */
public class AvroDataHandler implements DataHandler {
    private InputStream is;
    private OutputStream os;

    protected Map<Integer, ARecords> datasets;

    protected List<AMeasurementInfo> measurementInfoList;

    public AvroDataHandler(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    @Override
    public void initialize() {
        datasets = new HashMap<>();
        measurementInfoList = new ArrayList<>();
    }

    @Override
    public void handleNewDataset(int datasetId, long timestamp, String measurerName) {
        AMeasurementInfo info = new AMeasurementInfo(datasetId, timestamp, measurerName);
        measurementInfoList.add(info);
        ARecords aRecords =  new ARecords(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        datasets.put(datasetId, aRecords);
    }

    @Override
    public void handleValue(int datasetId, DataType type, double value) {
        ARecords dataset = datasets.get(datasetId);
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset with id " + datasetId + " not initialized.");
        }

        if(type.equals(DataType.DOWNLOAD)) {
            dataset.getDOWNLOAD().add(value);
        }
        else if(type.equals(DataType.PING)) {
            dataset.getPING().add(value);
        }
        else {
            dataset.getUPLOAD().add(value);
        }
    }

    @Override
    public void getResults(ResultConsumer consumer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DatumWriter<ARequestTuple> datumWriter = new SpecificDatumWriter<>(ARequestTuple.class);
        BinaryEncoder encoder =
                EncoderFactory.get().binaryEncoder(baos , null);

        for(AMeasurementInfo measurementInfo : measurementInfoList) {
            ARecords aRecords = datasets.get(measurementInfo.getId());
            ARequestTuple requestTuple = new ARequestTuple(aRecords, measurementInfo);
            datumWriter.write(requestTuple, encoder);
            encoder.flush();
            byte[] dataBytes = baos.toByteArray();
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            lengthBuffer.putInt(dataBytes.length);
            System.out.println("Data bytes " + dataBytes.length);
            for(int i = 0; i != 4; ++i) {
                System.out.println(lengthBuffer.get(i));
            }
            os.write(lengthBuffer.array());
            os.write(dataBytes);
        }

        // Receive response from server
        DatumReader<AResponseTuple> resultsReader = new SpecificDatumReader<>(AResponseTuple.class);
        DataFileStream<AResponseTuple> dataFileStream = new DataFileStream<>(is, resultsReader);

        while (dataFileStream.hasNext()) {
            AResponseTuple result = dataFileStream.next();
            AMeasurementInfo info = result.getMeasurementInfo();
            AAverage average = result.getAverage();
            consumer.acceptMeasurementInfo(info.getId(), info.getTimestamp(), info.getMeasurerName().toString());
            consumer.acceptResult(DataType.DOWNLOAD, average.getDOWNLOAD());
            consumer.acceptResult(DataType.PING, average.getPING());
            consumer.acceptResult(DataType.UPLOAD, average.getUPLOAD());
        }
        dataFileStream.close();
    }
}
