package cz.esw.serialization.handler;

import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.json.DataType;

import cz.esw.serialization.json.Dataset;
import cz.esw.serialization.json.MeasurementInfo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import cz.esw.serialization.proto.PBInfo;
import cz.esw.serialization.proto.PBMeasurement;
import cz.esw.serialization.proto.PBMeasurementArray;
import cz.esw.serialization.proto.PBAvgData;
import cz.esw.serialization.proto.PBAvgDataArray;

/**
 * @author Marek Cuchý (CVUT)
 */
public class ProtoDataHandler implements DataHandler {

    private final InputStream is;
    private final OutputStream os;
    protected Map<Integer, Dataset> datasets;

    public ProtoDataHandler(InputStream is, OutputStream os) {
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
    public void getResults(ResultConsumer consumer) {
        PBMeasurementArray.Builder measurementArrayBuilder = PBMeasurementArray.newBuilder();

        datasets.forEach((datasetid, dataset) -> {
            MeasurementInfo measurementInfo = dataset.getInfo();
            PBInfo info = PBInfo.newBuilder()
                    .setId(measurementInfo.id())
                    .setMeasurerName(measurementInfo.measurerName())
                    .setTimestamp(measurementInfo.timestamp())
                    .build();

            List<Double> downloadList = dataset.getRecords().get(DataType.DOWNLOAD);
            List<Double> uploadList = dataset.getRecords().get(DataType.UPLOAD);
            List<Double> pingList = dataset.getRecords().get(DataType.PING);

            PBMeasurement.Builder measurementBuilder = PBMeasurement.newBuilder()
                    .setInfo(info);

            measurementBuilder.addAllDownload(downloadList);
            measurementBuilder.addAllUpload(uploadList);
            measurementBuilder.addAllPing(pingList);

            PBMeasurement measurement = measurementBuilder.build();

            measurementArrayBuilder.addDatasets(measurement);
        });

        PBMeasurementArray measurementArray = measurementArrayBuilder.build();
        int messageSize = 0;
        try {
            messageSize = measurementArray.getSerializedSize();
            os.write((messageSize >>> 24) & 0xFF);
            os.write((messageSize >>> 16) & 0xFF);
            os.write((messageSize >>> 8) & 0xFF);
            os.write(messageSize & 0xFF);
            measurementArray.writeTo(os);
        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("Sending bytes: " + messageSize);
        try {
            byte[] byteArray = new byte[4];

            is.read(byteArray);
            int recvCount = 0;
            for (int i = 0; i < 4; i++) {
                recvCount |= (byteArray[i] & 0xFF) << (8 * i);
            }

            while (is.available() < recvCount) {
                Thread.sleep(100);
            }
            byte[] recv = new byte[recvCount];
            System.out.println("Recieved bytes: " + is.read(recv));
            PBAvgDataArray avgDataArray = PBAvgDataArray.parseFrom(recv);
            for (PBAvgData avgData : avgDataArray.getDataArrayList()) {
                consumer.acceptMeasurementInfo((int) avgData.getInfo().getId(), (long) avgData.getInfo().getTimestamp(),
                        avgData.getInfo().getMeasurerName());
                consumer.acceptResult(DataType.UPLOAD, avgData.getUpload());
                consumer.acceptResult(DataType.DOWNLOAD, avgData.getDownload());
                consumer.acceptResult(DataType.PING, avgData.getPing());
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
