package cz.esw.serialization.handler;

import cz.esw.serialization.avro.ARecords;
import cz.esw.serialization.json.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AvroDataHandlerTest {

    private AvroDataHandler handler;
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;

    @BeforeEach
    void setUp() {
        inputStream = new ByteArrayInputStream(new byte[0]);
        outputStream = new ByteArrayOutputStream();
        handler = new AvroDataHandler(inputStream, outputStream);
        handler.initialize();
    }

    @Test
    void testHandleValue() {
        int datasetId = 1;
        long timestamp = System.currentTimeMillis();
        String measurerName = "TestMeasurer";

        // Initialize dataset
        handler.handleNewDataset(datasetId, timestamp, measurerName);

        // Add values
        handler.handleValue(datasetId, DataType.DOWNLOAD, 123.45);
        handler.handleValue(datasetId, DataType.PING, 10.0);
        handler.handleValue(datasetId, DataType.UPLOAD, 54.32);
        handler.handleValue(datasetId, DataType.DOWNLOAD, 123);

        ARecords records = handler.datasets.get(datasetId);

        assertNotNull(records, "Dataset should not be null");
        assertEquals(2, records.getDOWNLOAD().size(), "Should have one download record");
        assertEquals(123.45, records.getDOWNLOAD().get(0), "Download value should match");
        assertEquals(123, records.getDOWNLOAD().get(1), "Download value should match");
        assertEquals(1, records.getPING().size(), "Should have one ping record");
        assertEquals(10.0, records.getPING().get(0), "Ping value should match");
        assertEquals(1, records.getUPLOAD().size(), "Should have one upload record");
        assertEquals(54.32, records.getUPLOAD().get(0), "Upload value should match");
    }
}
