package cz.esw.serialization.handler;

import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.json.DataType;
import cz.esw.serialization.proto.MeasurementInfo;
import cz.esw.serialization.proto.PMeasurementsRequest;
import cz.esw.serialization.proto.PMeasurementsResponse;
import cz.esw.serialization.proto.Records;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.ByteBuffer;

/**
 * @author Marek Cuchý (CVUT)
 */
public class ProtoDataHandler implements DataHandler {

    private InputStream is;

    private OutputStream os;

    private List<MeasurementInfo> measurementInfoList;
    private Map<Integer, List<Double>> downloadData;
    private Map<Integer, List<Double>> pingData;
    private Map<Integer, List<Double>> uploadData;


    public ProtoDataHandler(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    @Override
    public void initialize() {
        measurementInfoList = new ArrayList<>();
        downloadData = new HashMap<>();
        pingData = new HashMap<>();
        uploadData = new HashMap<>();
    }

    @Override
    public void handleNewDataset(int datasetId, long timestamp, String measurerName) {
        MeasurementInfo measurementInfo = MeasurementInfo.newBuilder()
                .setId(datasetId)
                .setTimestamp(timestamp)
                .setMeasurerName(measurerName)
                .build();
		measurementInfoList.add(measurementInfo);
        Records records = Records.newBuilder().build();
    }

    @Override
    public void handleValue(int datasetId, DataType type, double value) {
        switch (type) {
            case DOWNLOAD:
                downloadData.computeIfAbsent(datasetId, k -> new ArrayList<>()).add(value);
                break;
            case PING:
                pingData.computeIfAbsent(datasetId, k -> new ArrayList<>()).add(value);
                break;
            case UPLOAD:
                uploadData.computeIfAbsent(datasetId, k -> new ArrayList<>()).add(value);
                break;
        }
    }

    @Override
    public void getResults(ResultConsumer consumer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (MeasurementInfo measurementInfo : measurementInfoList) {
            Integer id = measurementInfo.getId();
            Records records = Records.newBuilder()
                    .addAllDownload(downloadData.getOrDefault(id, new ArrayList<>()))
                    .addAllPing(pingData.getOrDefault(id, new ArrayList<>()))
                    .addAllUpload(uploadData.getOrDefault(id, new ArrayList<>()))
                    .build();
            PMeasurementsRequest.RequestTuple requestTuple = PMeasurementsRequest.RequestTuple.newBuilder()
                    .setRecords(records)
                    .setMeasurementInfo(measurementInfo)
                    .build();
            requestTuple.writeDelimitedTo(baos);
        }

        byte[] dataBytes = baos.toByteArray();

        ByteArrayOutputStream messageSizeStream = new ByteArrayOutputStream();
        messageSizeStream.write(ByteBuffer.allocate(4).putInt(dataBytes.length).array());
        os.write(messageSizeStream.toByteArray());

        os.write(dataBytes);
        os.flush();

        PMeasurementsResponse.ResponseTuple responseTuple;
        while ((responseTuple = PMeasurementsResponse.ResponseTuple.parseDelimitedFrom(is)) != null) {
            MeasurementInfo info = responseTuple.getMeasurementInfo();
            consumer.acceptMeasurementInfo(info.getId(), info.getTimestamp(), info.getMeasurerName());
            consumer.acceptResult(DataType.DOWNLOAD, responseTuple.getAverage().getDownload());
            consumer.acceptResult(DataType.PING, responseTuple.getAverage().getPing());
            consumer.acceptResult(DataType.UPLOAD, responseTuple.getAverage().getUpload());
        }
    }
}
