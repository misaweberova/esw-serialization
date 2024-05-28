package cz.esw.serialization.handler;

import cz.esw.serialization.ResultConsumer;
import cz.esw.serialization.json.DataType;

import java.io.IOException;

/**
 * Handler responsible for receiving data and returning results.
 *
 * @author Marek Cuchý (CVUT)
 */
public interface DataHandler {

    /**
     * (Re)initialize the data handler.
     */
    void initialize();

    /**
     * Handles information about new dataset. Has to be called before the first value from the dataset is handled via
     * {@link #handleValue(int, DataType, double)}.
     */
    void handleNewDataset(int datasetId, long timestamp, String measurerName);

    /**
     * Handles given {@code value}. Before the first value from a dataset with {@code datasetId}, {@link
     * #handleNewDataset(int, long, String)} has to be called.
     *
     * @param datasetId id of the dataset to which the value belongs
     * @param type      type of the data value
     * @param value     the value to be handled
     */
    void handleValue(int datasetId, DataType type, double value);

    /**
     * Process values obtained so far and pass the results to the {@code consumer}.
     *
     * @param consumer of the calculated/obtained results
     */
    void getResults(ResultConsumer consumer) throws IOException;

}
