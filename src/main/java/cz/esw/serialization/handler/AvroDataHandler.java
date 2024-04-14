package cz.esw.serialization.handler;

import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.avro.ADataset;
import cz.esw.serialization.avro.AMeasurementInfo;
import cz.esw.serialization.avro.AResult;
import cz.esw.serialization.json.DataType;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author Marek Cuchý (CVUT)
 */
public class AvroDataHandler implements DataHandler {
    private InputStream is;
    private OutputStream os;

    protected Map<Integer, ADataset> datasets;

    public AvroDataHandler(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    @Override
    public void initialize() {
        datasets = new HashMap<>();
    }

    @Override
    public void handleNewDataset(int datasetId, long timestamp, String measurerName) {
        AMeasurementInfo info = new AMeasurementInfo(datasetId, timestamp, measurerName);
        Map<CharSequence, List<Double>> recordMap = new HashMap<>();
        datasets.put(datasetId, new ADataset(info, recordMap));
    }

    @Override
    public void handleValue(int datasetId, DataType type, double value) {
        ADataset dataset = datasets.get(datasetId);
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset with id " + datasetId + " not initialized.");
        }
        dataset.getRecords().computeIfAbsent(type.name(), t -> new ArrayList<>()).add(value);
    }

    @Override
    public void getResults(ResultConsumer consumer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DatumWriter<ADataset> datumWriter = new SpecificDatumWriter<>(ADataset.class);
        DataFileWriter<ADataset> fileWriter = new DataFileWriter<>(datumWriter);
        fileWriter.create(datasets.values().iterator().next().getSchema(), baos);

        for (ADataset dataset : datasets.values()) {
            fileWriter.append(dataset);
        }

        fileWriter.close();
        byte[] dataBytes = baos.toByteArray();

        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(dataBytes.length);

        // Write the length of the message in bytes
        os.write(lengthBuffer.array());

        // Write the data
        os.write(dataBytes);

        // Receive response from server
        DatumReader<AResult> resultsReader = new SpecificDatumReader<>(AResult.class);
        DataFileStream<AResult> dataFileStream = new DataFileStream<>(is, resultsReader);

        while (dataFileStream.hasNext()) {
            AResult result = dataFileStream.next();
            AMeasurementInfo info = result.getInfo();
            consumer.acceptMeasurementInfo(info.getId(), info.getTimestamp(), info.getMeasurerName().toString());
            result.getAverages().entrySet().stream().forEach(
                    entry -> consumer.acceptResult(DataType.valueOf(entry.getKey().toString()), entry.getValue().getValue())
            );
        }
        dataFileStream.close();
    }

}
