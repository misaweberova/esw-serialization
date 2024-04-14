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

    protected Map<Integer, Records> datasets;


    public ProtoDataHandler(InputStream is, OutputStream os) {
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
        MeasurementInfo measurementInfo = MeasurementInfo.newBuilder()
                .setId(datasetId)
                .setTimestamp(timestamp)
                .setMeasurerName(measurerName)
                .build();
		measurementInfoList.add(measurementInfo);
        Records records = Records.newBuilder().build();
        datasets.put(datasetId, records);
    }

    @Override
    public void handleValue(int datasetId, DataType type, double value) {
        Records dataset = datasets.get(datasetId);
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset with id " + datasetId + " not initialized.");
        }

        if(type.equals(DataType.DOWNLOAD)) {
            dataset.getDownloadList().add(value);
        }
        else if(type.equals(DataType.PING)) {
            dataset.getPingList().add(value);
        }
        else {
            dataset.getUploadList().add(value);
        }
    }

    @Override
    public void getResults(ResultConsumer consumer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (MeasurementInfo measurementInfo : measurementInfoList) {
            Records records = datasets.get(measurementInfo.getId());
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
