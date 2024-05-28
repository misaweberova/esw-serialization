package cz.esw.serialization.json;

import java.util.List;
import java.util.Map;

/**
 * @author Marek Cuchý
 */
public class Dataset {

    private MeasurementInfo info;
    private Map<DataType, List<Double>> records;

    public Dataset() {
    }

    public Dataset(MeasurementInfo info, Map<DataType, List<Double>> records) {
        this.info = info;
        this.records = records;
    }

    public MeasurementInfo getInfo() {
        return info;
    }

    public void setInfo(MeasurementInfo info) {
        this.info = info;
    }

    public Map<DataType, List<Double>> getRecords() {
        return records;
    }

    public void setRecords(Map<DataType, List<Double>> records) {
        this.records = records;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dataset{");
        sb.append("info=").append(info).append(",\n");
        sb.append("records={");

        for (Map.Entry<DataType, List<Double>> entry : records.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }

        // Remove trailing comma and space
        if (!records.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("}");
        sb.append("}");


        return sb.toString();
    }
    
}
